package com.webscraper.scrapedemo.controller;

import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import com.webscraper.scrapedemo.service.LocalFileService;
import com.webscraper.scrapedemo.service.PageAnalyzer;
import com.webscraper.scrapedemo.service.WebConnector;

import java.io.InputStream;
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
        recurseLevel++;
        logger.info("Scraping url..:" + scrapeUrl);

        ScrapeResult scrapeResult = analyzer.analyze(scrapeUrl);

        // save current page
        savePage(scrapeUrl, scrapeResult.content());

        // handle images
        Set<String> filteredImages = filterDiscoveredUrls(scrapeResult.imgLinks());
        logger.info("number of new images:" + filteredImages.size());
        filteredImages.forEach((src) -> {
            saveImage(src);
        });

        // handle urls recursively
        Set<String> filteredUrls = filterDiscoveredUrls(scrapeResult.urlsToScrape());
        logger.info("number of new urls:" + filteredUrls.size());

        logger.info("total discovered urls:" + discoveredUrls.size());

        if (discoveredUrls.size() > PAGE_LIMIT) {
            logger.info(String.format("reached page limit of %d, stopping recursion.", PAGE_LIMIT));

        } else {
            logger.info("starting to recurse..., on level:" + recurseLevel);
            filteredUrls.forEach(this::savePageAndDiscoverUrlsRecursively);
        }
        recurseLevel--;
        logger.info("recursive level up:" + recurseLevel);
    }

    private Set<String> filterDiscoveredUrls(Set<String> discovered) {
        // filter alredy discovered urls
        Set<String> filtered = discovered.stream().filter((entry) -> !discoveredUrls.contains(entry))
                .collect(Collectors.toSet());

//        logger.info("new urls filtered:");
//        logger.info(Arrays.toString(filtered.toArray()));
        // filter relative, to start with
        filtered.forEach((entry) -> {
            if (!entry.contains("http")) {
                logger.info("new relative url:" + entry);
            }
        });

        discoveredUrls.addAll(filtered);
        return filtered;
    }

    private void savePage(String url, String content)  {
        logger.info("Saving content for url:" + url);
        if (!url.contains("http")) {
            logger.warning("WARNING, RELATIVE: " + url);
        }

        String path = fileService.urlToPath(url);
        fileService.savePage(path, content);
    }

    private void saveImage(String url)  {
        try(InputStream inputStream = WebConnector.getImage(url)) {
            //logger.info("Saving image for url:" + url);
            if (!url.contains("http")) {
                logger.warning("WARNING, RELATIVE: " + url);
            }
            String path = fileService.urlToPath(url);
            fileService.saveImage(path, inputStream.readAllBytes());

        } catch (Exception ex) {
            throw new ScrapeException("Error saving image:" + url, ex);

        }
    }

}
