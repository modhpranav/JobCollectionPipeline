import asyncio
import random
import json
import time

import pandas as pd
from tqdm import tqdm
from datetime import datetime
from kafka_producer import KafkaProducer


class LinkedinScrapper:

    def __init__(self, page, job):
        self.page = page
        self.ids = []
        self.data = []
        self.job = job
        self.unscrappable = []
        self.iter = 0
        self.today = datetime.now().strftime("%Y_%m_%d")
        self.producer = KafkaProducer("job-details-topic")

    async def get_data(self):
        print("Getting data: Linkedin")
        for id_ in tqdm(self.ids):
            try:
                await self.page.goto(f"https://www.linkedin.com/jobs/view/{id_}")
                await asyncio.sleep(2)
                if not self.page.url.startswith("https://www.linkedin.com/jobs/view/"):
                    await self.page.goto(f"https://www.linkedin.com/jobs/view/{id_}")
                    await asyncio.sleep(2)
                jobTitle = await self.page.Jeval(
                    "h1", "(element) => element.textContent"
                )
                company = await self.page.Jeval(
                    'span[class="topcard__flavor"] a',
                    "(element) => element.textContent",
                )
                await asyncio.gather(
                    *[
                        self.page.waitForSelector(".show-more-less-button"),
                        self.page.click(".show-more-less-button"),
                    ],
                    return_exceptions=False,
                )
                await asyncio.sleep(2)
                jobDescription = await self.page.JJeval(
                    'section[class="show-more-less-html show-more-less-html--more"]',
                    "(sections => sections.map(section => section.innerText).join(' '))",
                )
                temp = {
                    "postingURL": self.page.url,
                    "jobDescription": jobDescription.replace("\n", " ").replace("  ", ""),
                    "jobTitle": jobTitle.strip(),
                    "company": company.strip(),
                }
                self.data.append(temp)
                kafka_message = json.dumps(temp)
                self.send_to_kafka("result", kafka_message)
                await asyncio.sleep(random.randint(1, 3))
            except Exception as e:
                self.unscrappable.append(id_)
                print("Unscrapable: ", id_, e)

    async def scrape_all_ids(self):
        ids = []
        iteration = 0
        while iteration < 1 and len(ids) < 100:
            print(f"Scrolled {iteration} times")
            try:
                await asyncio.sleep(2)
                if self.page.url != "https://www.linkedin.com":
                    await self.page.evaluate(
                        "window.scrollTo(0, document.body.scrollHeight - 200)"
                    )
                else:
                    return "retry"
                await asyncio.sleep(2)
                ids = await self.page.JJeval(
                    "li div.job-search-card",
                    "(nodes) => nodes.map(n => n.getAttribute('data-entity-urn'))",
                )
                iteration += 1
                ids = set(ids)
            except Exception as e:
                print(e)
                print("Last Iteration: ", iteration)
                break
        self.ids = list(map(lambda x: x.split(":")[-1], ids))
        print(self.ids)
    
    def send_to_kafka(self, key, value):
        return self.producer.publish_message(key, value)

    def send_data(self):
        pd.DataFrame(self.data).to_csv(f"{self.today}_linkedin_{self.job}.csv", index=False)
        self.unscrappable = {"Links": self.unscrappable}
        pd.DataFrame(self.unscrappable).to_csv(f"{self.today}_linkedin_unscrapable_{self.job}.csv", index=False)

    async def scrape(self):
        try:
            status = await self.scrape_all_ids()
            if status == "retry" and self.iter < 3:
                time.sleep(2)
                self.iter += 1
                return await self.scrape()
            else:
                await self.get_data()
        except Exception as e:
            print(e)
        finally:
            await self.page.close()
            self.send_data()
        return {"data": self.data}
