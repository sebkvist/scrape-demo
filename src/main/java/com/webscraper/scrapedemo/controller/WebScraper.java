package com.webscraper.scrapedemo.controller;

import com.webscraper.scrapedemo.model.ScrapeResult;
import com.webscraper.scrapedemo.service.LocalFileService;
import com.webscraper.scrapedemo.service.PageAnalyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class WebScraper {

    static Logger logger = Logger.getLogger(WebScraper.class.getName());

    public static final String BASE_URL = "https://books.toscrape.com/index.html";

    private PageAnalyzer analyzer = new PageAnalyzer();
    private LocalFileService fileService = new LocalFileService();

    private Set<String> urlSet = new HashSet<>();
    public void scrapeWebPages(String scrapeUrl) {
        savePageAndDiscoverUrlsRecursively(scrapeUrl);
    }

    private void savePageAndDiscoverUrlsRecursively(String scrapeUrl) {

        ScrapeResult scrapeResult = analyzer.analyze(scrapeUrl);
        savePage(scrapeUrl, scrapeResult.content());

        urlSet = scrapeResult.urlsToScrape();
        logger.info("urls discovered:");
        urlSet.forEach((entry) -> logger.info(entry));
        logger.info(Arrays.toString(urlSet.toArray()));
        urlSet.forEach(this::savePageAndDiscoverUrlsRecursively);
    }

    private void savePage(String scrapeUrl, String content)  {
        String path = fileService.urlToPath(scrapeUrl);
        fileService.savePage(path, content);
    }

}
