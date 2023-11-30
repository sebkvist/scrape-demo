package com.webscraper.scrapedemo.service;

import com.webscraper.scrapedemo.exception.ScrapeException;
import com.webscraper.scrapedemo.model.ScrapeResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PageAnalyzer {

    private Document doc;
    public ScrapeResult analyze(String scrapeUrl) {
         doc = WebConnector.getPage(scrapeUrl);

        return new ScrapeResult(doc.text(), discoverUrls());
    }
    private Document connect(String scrapeUrl) {
        try {
            doc = Jsoup.connect(scrapeUrl).get();
            return doc;
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + scrapeUrl, ioexception);
        }
    }
    private Set<String> discoverUrls() {
        Set<String> urls = new HashSet<>();

        //TODO: implement
        Elements hrefList = doc.select("href");
        urls.add(hrefList.first().text());

        return urls;
    }
}
