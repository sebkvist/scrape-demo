package com.webscraper.scrapedemo;

import com.webscraper.scrapedemo.controller.WebScraper;
import com.webscraper.scrapedemo.service.file.LocalFileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class ScrapeDemoApplication {

	public static final String BASE_URL = "https://books.toscrape.com/index.html";

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") +  "ScrapeResult";

	private static WebScraper scraper;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ScrapeDemoApplication.class, args);

		System.out.println("args: " + Arrays.toString(args));
		scraper = new WebScraper(new LocalFileService(TEMP_DIR));

		scraper.scrapeWebSite(BASE_URL);
	}

}
