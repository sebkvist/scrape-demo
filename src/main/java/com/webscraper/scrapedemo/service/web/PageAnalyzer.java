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

public class PageAnalyzer {
     Logger logger = Logger.getLogger(PageAnalyzer.class.getName());

    private Document doc;
    public ScrapeResult analyze(String scrapeUrl) {
        getPage(scrapeUrl);

        return new ScrapeResult(doc.toString(), discoverUrls(), discoverImageLinks(), discoverStyleLinks());
    }


    public Set<String> discoverUrls() {
        return discoverElementLinks("a", "href");
    }

    public Set<String> discoverImageLinks() {
        return discoverElementLinks("img", "src");
    }

    private Set<String> discoverStyleLinks() {
        return discoverElementLinks("link", "href");
    }

    private void getPage(String scrapeUrl) {
        try {
            doc = Jsoup.connect(scrapeUrl).get();
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + scrapeUrl, ioexception);
        }
    }

    private Set<String> discoverElementLinks(String element, String attribute) {
        Elements elList = doc.select(element);
        logger.info("selected links of type" + element +": " + Arrays.toString(elList.toArray()));
        Set<String> links = elList.stream()
                .map(el -> el.absUrl(attribute))
                .collect(Collectors.toSet());

        Set<String> urls = new HashSet<>();
        urls.addAll(links);
        return urls;
    }

}
