package com.webscraper.scrapedemo;

import com.webscraper.scrapedemo.service.file.LocalFileService;
import com.webscraper.scrapedemo.service.web.WebScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

@SpringBootApplication
public class ScrapeDemoApplication {

	private static WebScraper scraper = new WebScraper();

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ScrapeDemoApplication.class, args);

		System.out.println("args: " + Arrays.toString(args));

		System.out.println("current dir:" + System.getProperty("user.dir"));
		System.out.println("temp dir:" + System.getProperty("java.io.tmpdir"));
//		if (true) throw new RuntimeException("test");

		LocalFileService.createPathFromUrl("somedir/index.html");
		LocalFileService.createPathFromUrl("somedir2/subdir/index.html");
		LocalFileService.createPathFromUrl("index.html");

		scraper.scrapeWebPages(WebScraper.BASE_URL);
	}

}
