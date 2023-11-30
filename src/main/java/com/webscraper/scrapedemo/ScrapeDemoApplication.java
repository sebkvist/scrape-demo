package com.webscraper.scrapedemo;

import com.webscraper.scrapedemo.controller.WebScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class ScrapeDemoApplication {

	private static WebScraper scraper = new WebScraper();

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ScrapeDemoApplication.class, args);

		System.out.println("args: " + Arrays.toString(args));

		scraper.scrapeWebPages(WebScraper.BASE_URL);
	}

}
