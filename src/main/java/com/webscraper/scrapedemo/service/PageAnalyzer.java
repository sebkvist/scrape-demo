package com.webscraper.scrapedemo.service;

import com.webscraper.scrapedemo.controller.WebScraper;
import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PageAnalyzer {
     Logger logger = Logger.getLogger(PageAnalyzer.class.getName());

    private Document doc;
    public ScrapeResult analyze(String scrapeUrl) {
         doc = WebConnector.getPage(scrapeUrl);

        return new ScrapeResult(doc.text(), discoverUrls(), discoverImageLinks());
    }
    private Document connect(String scrapeUrl) {
        try {
            doc = Jsoup.connect(scrapeUrl).get();
            return doc;
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + scrapeUrl, ioexception);
        }
    }
    public Set<String> discoverUrls() {
        return discoverElementLinks("a", "href");
    }

    public Set<String> discoverImageLinks() {
        return discoverElementLinks("img", "src");
    }

    private Set<String> discoverElementLinks(String element, String attribute) {
        Set<String> urls = new HashSet<>();

        logger.fine("Page size to discover:"+ doc.text().length());
        Elements elList = doc.select(element);
        logger.info("a href:" + Arrays.toString(elList.toArray()));
        Set<String> links = elList.stream()
                .map(el -> el.absUrl(attribute))
                .collect(Collectors.toSet());
        urls.addAll(links);

        return urls;
    }

}
