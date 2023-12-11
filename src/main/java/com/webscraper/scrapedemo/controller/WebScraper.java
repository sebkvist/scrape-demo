package com.webscraper.scrapedemo.controller;

import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import com.webscraper.scrapedemo.service.file.LocalFileService;
import com.webscraper.scrapedemo.service.web.PageAnalyzer;
import com.webscraper.scrapedemo.util.UrlKeeper;
import com.webscraper.scrapedemo.util.UrlHandler;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


import java.util.concurrent.TimeUnit;

/**
 * The web scraper class, that takes a URL and scrapes the page of that URL,
 * and recursively scrapes the pages of the URLs found in the current page.
 *
 */
public class WebScraper {
    private Logger logger = Logger.getLogger(WebScraper.class.getName());

    private LocalFileService localFileService;

    private UrlKeeper urlKeeper = new UrlKeeper();

    private Integer recurseLevel = 0;

    private ExecutorService executorService;

    public WebScraper(LocalFileService localFileService) {
        this.localFileService = localFileService;
    }

    /**
     * Scrape a website recursively and save all files and content.
     *
     * @param scrapeUrl the root url to start the scraping from
     */
    public void scrapeWebSite(String scrapeUrl) {
        logger.info("Scraping website with root url..:" + scrapeUrl);

        executorService = Executors.newFixedThreadPool(16);

        urlKeeper.extractAndKeepUniqueUrls(Set.of(scrapeUrl));
        savePageAndDiscoverUrlsRecursively(scrapeUrl);

        logger.info("Traversal done, shutting down..:" + scrapeUrl);
        executorService.shutdown();

        logger.info("Traversal done, awaiting threads..:" + scrapeUrl);
        try {
            executorService.awaitTermination(20, TimeUnit.SECONDS);

        } catch (Exception exception) {
            throw new ScrapeException("Error in shutting down!", exception);
        }
        logger.info("Traversal complete!");
    }

    private void savePageAndDiscoverUrlsRecursively(String scrapeUrl) {
        recurseLevel++;
        logger.info("recurse level:" + recurseLevel);

        logger.info("Scraping url..:" + scrapeUrl);
        ScrapeResult scrapeResult = PageAnalyzer.scrape(scrapeUrl);

        // save current page
        savePage(scrapeUrl, scrapeResult.content());

        // handle images
        Set<String> filteredImages = urlKeeper.extractAndKeepUniqueUrls(scrapeResult.imgLinks());
        logger.fine("number of new images:" + filteredImages.size());
        filteredImages.forEach((src) -> {
            saveImage(src);
        });

        // handle styles
        Set<String> styleSheets = urlKeeper.extractAndKeepUniqueUrls(scrapeResult.styleLinks());
        logger.fine("number of new styles:" + styleSheets.size());
        styleSheets.forEach((ref) -> {
            saveStyles(ref);
        });

        // handle urls
        Set<String> filteredUrls = urlKeeper.extractAndKeepUniqueUrls(scrapeResult.urlsToScrape());
        logger.info("number of new urls:" + filteredUrls.size());

        // log stats
        logger.info("total discovered urls:" + urlKeeper.getTotalUniqueUrls());

        // handle new urls recursively
        filteredUrls.forEach(this::savePageAndDiscoverUrlsRecursively);

        recurseLevel--;
        logger.info("back to recursive level:" + recurseLevel);
    }
    private void savePage(String url, String content)  {
        Runnable runnable = () -> {
            logger.info("Saving page content for url:" + url);
            String path = UrlHandler.getPathFromUrl(url);
            localFileService.savePage(path, content);
        };
        executorService.execute(runnable);
    }

    private void saveImage(String url)  {
        Runnable runnable = () -> {
            logger.info("Saving image for url:" + url);
            saveFileData(url);
        };
        executorService.execute(runnable);
    }

    private void saveStyles(String url) {
        Runnable runnable = () -> {
            logger.info("Saving style for url:" + url);
            saveFileData(url);
        };
        executorService.execute(runnable);
    }

    private void saveFileData(String url) {
        try(InputStream inputStream = UrlHandler.getFileData(url)) {
            String path = UrlHandler.getPathFromUrl(url);
            localFileService.saveFileData(path, inputStream.readAllBytes());

        } catch (Exception ex) {
            throw new ScrapeException("Error saving file:" + url, ex);
        }
    }


}
