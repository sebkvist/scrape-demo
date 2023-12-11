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

    /**
     * Analyzes a page and returns the relevant info in a ScrapeResult.
     *
     * @param scrapeUrl the URL of the page to analyze
     * @return a ScrapeResult, containing page content and url links found on the webpage.
     */
    public static ScrapeResult scrape(String scrapeUrl) {
        PageAnalyzer analyzer = new PageAnalyzer(scrapeUrl);

        return new ScrapeResult(analyzer.getContent(),
                analyzer.discoverUrls(),
                analyzer.discoverImageLinks(),
                analyzer.discoverStyleLinks());
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

    /**
     * Convenience method to select elements (named element) with an attribute (named attribute) that contains an url,
     * (that may be relative or absolute) and get the absolute form of the url.
     *
     * @param element the name of the elements to select
     * @param attribute the attribute of the elements to select. Note that the attribute should contain a URL value.
     * @return a set of all selected urls, in absolute form.
     */
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
