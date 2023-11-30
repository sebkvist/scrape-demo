package com.webscraper.scrapedemo.controller;

import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import com.webscraper.scrapedemo.service.LocalFileService;
import com.webscraper.scrapedemo.service.PageAnalyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebScraper {

    private static final int PAGE_LIMIT = 256;
    static Logger logger = Logger.getLogger(WebScraper.class.getName());

    public static final String BASE_URL = "https://books.toscrape.com/index.html";

    private PageAnalyzer analyzer = new PageAnalyzer();
    private LocalFileService fileService = new LocalFileService();

    private Integer recurseLevel = 0;

//    private Set<String> urlSet = new HashSet<>();

    private Set<String> discoveredUrls = new HashSet<>();
    public void scrapeWebPages(String scrapeUrl) {
        logger.info("Scraping website with root url..:" + scrapeUrl);
        savePageAndDiscoverUrlsRecursively(scrapeUrl);
    }

    private void savePageAndDiscoverUrlsRecursively(String scrapeUrl) {
        logger.info("Scraping url..:" + scrapeUrl);

        ScrapeResult scrapeResult = analyzer.analyze(scrapeUrl);
        savePage(scrapeUrl, scrapeResult.content());

        Set<String> filtered = filterDiscoveredUrls(scrapeResult);

//        logger.info("number of new urls:" + filtered.size());
        logger.info("total scraped urls:" + discoveredUrls.size());
        if (discoveredUrls.size() > PAGE_LIMIT) {
            logger.info(String.format("reached page limit of %d, stopping recursion.", PAGE_LIMIT));

        } else {
            logger.info("starting to recurse..., on level:" + recurseLevel);
            recurseLevel++;
            filtered.forEach(this::savePageAndDiscoverUrlsRecursively);
            recurseLevel--;
            logger.info("recursed up, level:" + recurseLevel);
        }
    }

    private Set<String> filterDiscoveredUrls(ScrapeResult scrapeResult) {
        Set<String> discovered = scrapeResult.urlsToScrape();
//        logger.info("urls discovered:");
//        logger.info(Arrays.toString(discovered.toArray()));

        Set<String> filtered = discovered.stream().filter((entry) -> !discoveredUrls.contains(entry))
                .collect(Collectors.toSet());

        logger.info("new urls filtered:");
//        logger.info(Arrays.toString(filtered.toArray()));
        filtered.forEach((entry) -> {
            if (!entry.contains("http")) {
                logger.info("relative url:" + entry);
            }
        });

        discoveredUrls.addAll(filtered);
        return filtered;
    }

    private void savePage(String scrapeUrl, String content)  {
  //      logger.info("Saving content for url:" + scrapeUrl);
        String path = fileService.urlToPath(scrapeUrl);
        fileService.savePage(path, content);
    }

}
