package com.webscraper.scrapedemo.service;

import com.webscraper.scrapedemo.exception.ScrapeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WebConnector {
    public static Document getPage(String scrapeUrl) {
        try {
            Document doc = Jsoup.connect(scrapeUrl).get();
            return doc;
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + scrapeUrl, ioexception);
        }
    }
    public static InputStream getImage(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            return url.openStream();
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + imgUrl, ioexception);
        }
    }

}
