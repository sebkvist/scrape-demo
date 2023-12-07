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

public class WebScraper {
    private Logger logger = Logger.getLogger(WebScraper.class.getName());

    private LocalFileService localFileService;

    private UrlKeeper urlKeeper = new UrlKeeper();

    private AtomicInteger recurseLevel = new AtomicInteger();

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

        urlKeeper.extractAndKeepUniqueUrls(Set.of(scrapeUrl));
        savePageAndDiscoverUrlsRecursively(scrapeUrl);
    }

    private void savePageAndDiscoverUrlsRecursively(String scrapeUrl) {
        recurseLevel.incrementAndGet();
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

        recurseLevel.decrementAndGet();
        logger.info("recursive level up:" + recurseLevel);
    }
    private void savePage(String url, String content)  {
        logger.fine("Saving page content for url:" + url);

        String path = UrlHandler.getPathFromUrl(url);
        localFileService.savePage(path, content);
    }

    private void saveImage(String url)  {
        logger.fine("Saving image for url:" + url);
        saveFileData(url);
    }

    private void saveStyles(String url) {
        logger.info("Saving style for url:" + url);
        saveFileData(url);
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
