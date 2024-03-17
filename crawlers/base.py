from pyppeteer import launch
import os
import asyncio
from indeed_scrapper import IndeedScrapper
from linkedin_scrapper import LinkedinScrapper
from dotenv import load_dotenv
from constants import indeed_url, linkedin_url, jobs

load_dotenv()

import asyncio

class BaseScrapping:

    # method to launch browser
    async def get_browser(self):
        print("Launching browser")
        return await launch(
            ignoreHTTPSErrors=True,
            headless=False,
            args=[
            '--no-sandbox',
            '--disable-setuid-sandbox',
            '--disable-dev-shm-usage',
            '--disable-accelerated-2d-canvas',
            '--no-first-run',
            '--no-zygote',
            '--single-process',
            '--disable-gpu'
        ]
    )


    # method to open page
    async def get_page(self, url):
        browser = await self.get_browser()
        print("Opening page")
        page = await browser.newPage()
        print("Going to url")
        await page.goto(url, timeout=1000000)
        return page

    # extracting data by calling particular scrapper
    async def extract(self, url, job, source):
        page = await self.get_page(url)
        if source == "indeed":
            data = await IndeedScrapper(page, job).scrape()
        elif source == "linkedin":
            data = await LinkedinScrapper(page, job).scrape()
        else:
            data = "Failed"
        return data

    
try:
    loop = asyncio.get_event_loop()
    for job in jobs:
        try:
            indeed_url = indeed_url.replace("JOBWORD", job.replace(" ", "+"))
            # linkedin_url = linkedin_url.replace("JOBWORD", job.replace(" ", "%20"))
            indeed_data = loop.run_until_complete(BaseScrapping().extract(indeed_url, job, source="indeed"))
            # linkedin_data = loop.run_until_complete(BaseScrapping().extract(linkedin_url, job, source="linkedin"))
            print("Crawling Finished!")
            break
        except Exception as e:
            print(e)
            print({"Error": e})
except Exception as e:
    print(e)
    print({"Error": e})

