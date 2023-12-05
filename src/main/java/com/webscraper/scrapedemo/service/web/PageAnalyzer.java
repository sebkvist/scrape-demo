package com.webscraper.scrapedemo.service.web;

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

/**
 * A utility service to analyze and scrape a webpage.
 */
public class PageAnalyzer {
     Logger logger = Logger.getLogger(PageAnalyzer.class.getName());

    private Document doc;
    public static ScrapeResult scrape(String scrapeUrl) {
        PageAnalyzer analyzer = of(scrapeUrl);

        return new ScrapeResult(analyzer.getContent(),
                analyzer.discoverUrls(),
                analyzer.discoverImageLinks(),
                analyzer.discoverStyleLinks());
    }

    public static PageAnalyzer of(String scrapeUrl) {
        PageAnalyzer analyzer = new PageAnalyzer(scrapeUrl);
        return analyzer;
    }

    private PageAnalyzer(String scrapeUrl) {
        try {
            doc = Jsoup.connect(scrapeUrl).get();
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + scrapeUrl, ioexception);
        }
    }

    public String  getContent() {
        return doc.toString();
    }

    public Set<String> discoverUrls() {
        return discoverElementLinks("a", "href");
    }

    public Set<String> discoverImageLinks() {
        return discoverElementLinks("img", "src");
    }

    public Set<String> discoverStyleLinks() {
        return discoverElementLinks("link", "href");
    }

    private Set<String> discoverElementLinks(String element, String attribute) {
        Elements elList = doc.select(element);
        logger.fine("selected links of type: " + element +": " + Arrays.toString(elList.toArray()));
        Set<String> links = elList.stream()
                .map(el -> el.absUrl(attribute))
                .collect(Collectors.toSet());

        Set<String> urls = new HashSet<>();
        urls.addAll(links);
        return urls;
    }
}
