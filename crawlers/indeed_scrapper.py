import asyncio
import random
import json
import pandas as pd
from tqdm import tqdm
from datetime import datetime
from kafka_producer import KafkaProducer


class IndeedScrapper:

    def __init__(self, page, job):
        self.page = page
        self.postings = []
        self.data = []
        self.job = job
        self.unscrappable = []
        self.today = datetime.now().strftime("%Y_%m_%d")
        self.producer = KafkaProducer("job-details-topic")

    async def get_data(self):
        print("Getting data: Indeed")
        for post in tqdm(self.postings):
            try:
                await self.page.goto(post)
                await asyncio.gather(
                *[
                    self.page.waitForSelector('.css-1saizt3'),
                ],
                return_exceptions=False
                )
                jobDescription = await self.page.Jeval('#jobDescriptionText', "(element) => element.textContent")
                jobTitle = await self.page.Jeval('h1 span', "(element) => element.textContent")
                company = await self.page.Jeval('div[data-testid="inlineHeader-companyName"] span a', "(element) => element.textContent")
                temp = {"postingURL": self.page.url, "jobDescription": jobDescription.replace('\n', " ").replace("  ", ""), "jobTitle": jobTitle, "company": company.split(".")[0]}
                self.data.append(temp)
                kafka_message = json.dumps(temp)
                self.send_to_kafka("result", kafka_message)
                print("Sending data to Kafka: ", temp['postingURL'])
                await asyncio.sleep(random.randint(1, 3))
            except Exception as e:
                # print("Unscrapable: ", post, e)
                self.unscrappable.append(post)
    
    async def scrape_all_links(self):
        next_page = 1
        while next_page < 10:
            try:
                links = await self.page.querySelectorAll('.jcs-JobTitle.css-jspxzf.eu4oa1w0')
                for link in links:
                    href = await self.page.evaluate('(element) => element.href', link)
                    self.postings.append(href)
                await self.page.click('a[aria-label="Next Page"]')
                await asyncio.sleep(2)
                next_page += 1
            except Exception as e:
                print("Last Page: ", next_page)
                break

    def send_to_kafka(self, key, value):
        return self.producer.publish_message(key, value)    
    
    def send_data(self):
        pd.DataFrame(self.data).to_csv(f"{self.today}_indeed_{self.job}.csv", index=False)
        self.unscrappable = {"Links": self.unscrappable}
        pd.DataFrame(self.unscrappable).to_csv(f"{self.today}_indeed_unscrapable_{self.job}.csv", index=False)
    
    async def scrape(self):
        try:
            print(f"Started Scraping Job Postings URLS for {self.job}. . .")
            await self.scrape_all_links()
            print("Finished Scraping Job Postings URLS. . .")
            print("Started Scraping Job Details. . .")
            await self.get_data()
            print(f"Scraping Job Completed for {self.job}. . .")
        except Exception as e:
            print(e)
        finally:
            await self.page.close()
            self.send_data()
        return {
            "data": self.data
        }