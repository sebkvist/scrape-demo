package com.webscraper.scrapedemo.service.web;

import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import com.webscraper.scrapedemo.service.file.LocalFileService;
import com.webscraper.scrapedemo.service.web.util.UrlKeeper;
import com.webscraper.scrapedemo.service.web.util.UrlHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Logger;

public class WebScraper {

    private static final int PAGE_LIMIT = 512;
    static Logger logger = Logger.getLogger(WebScraper.class.getName());

    public static final String BASE_URL = "https://books.toscrape.com/index.html";

    private PageAnalyzer analyzer = new PageAnalyzer();
    private LocalFileService fileService = new LocalFileService();

    private UrlKeeper urlKeeper = new UrlKeeper();

    private Integer recurseLevel = 0;

    public void scrapeWebPages(String scrapeUrl) {
        logger.info("Scraping website with root url..:" + scrapeUrl);
        urlKeeper.extractAndSaveUniqueUrls(Set.of(scrapeUrl));
        savePageAndDiscoverUrlsRecursively(scrapeUrl);
    }

    private void savePageAndDiscoverUrlsRecursively(String scrapeUrl) {
        recurseLevel++;
        logger.info("Scraping url..:" + scrapeUrl);

        ScrapeResult scrapeResult = analyzer.analyze(scrapeUrl);

        // save current page
        savePage(scrapeUrl, scrapeResult.content());

        // handle images
        Set<String> filteredImages = urlKeeper.extractAndSaveUniqueUrls(scrapeResult.imgLinks());
        logger.info("number of new images:" + filteredImages.size());
        filteredImages.forEach((src) -> {
            saveImage(src);
        });

        // handle styles
        Set<String> styleSheets = urlKeeper.extractAndSaveUniqueUrls(scrapeResult.styleLinks());
        logger.info("number of new images:" + styleSheets.size());
        styleSheets.forEach((ref) -> {
            saveStyles(ref);
        });

        // handle urls recursively
        Set<String> filteredUrls = urlKeeper.extractAndSaveUniqueUrls(scrapeResult.urlsToScrape());
        logger.info("number of new urls:" + filteredUrls.size());

        Set<String> totalDiscoveredUrls = urlKeeper.getTotalUniqueUrls();
        logger.info("total discovered urls:" + totalDiscoveredUrls.size());

        if (totalDiscoveredUrls.size() > PAGE_LIMIT) {
            logger.info(String.format("reached page limit of %d, stopping recursion.", PAGE_LIMIT));

        } else {
            logger.info("starting to recurse..., on level:" + recurseLevel);
            filteredUrls.forEach(this::savePageAndDiscoverUrlsRecursively);
        }
        recurseLevel--;
        logger.info("recursive level up:" + recurseLevel);
    }


    private void savePage(String url, String content)  {
        logger.info("Saving content for url:" + url);
        throwIfRelative(url);

        String path = UrlHandler.getPathFromUrl(url);
        fileService.savePage(path, content);
    }

    private static void throwIfRelative(String url) {
        if (!url.contains("http")) {
            //TODO:
            throw new ScrapeException("WARNING, RELATIVE URL: " + url);
        }
    }

    private void saveImage(String url)  {
        try(InputStream inputStream = UrlHandler.getImage(url)) {
            //logger.info("Saving image for url:" + url);
            throwIfRelative(url);

            String path = UrlHandler.getPathFromUrl(url);
            fileService.saveImage(path, inputStream.readAllBytes());

        } catch (Exception ex) {
            throw new ScrapeException("Error saving image:" + url, ex);

        }
    }

    private void saveStyles(String url) {
        try(InputStream reader = UrlHandler.getStyle(url)) {
            logger.info("Saving style for url:" + url);
            throwIfRelative(url);


            String path = UrlHandler.getPathFromUrl(url);
            fileService.saveStyle(path, reader.readAllBytes());

        } catch (Exception ex) {
            throw new ScrapeException("Error saving style:" + url, ex);

        }
    }


}
