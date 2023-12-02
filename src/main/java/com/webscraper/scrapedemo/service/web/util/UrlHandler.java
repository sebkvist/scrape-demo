package com.webscraper.scrapedemo.service.web.util;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlHandler {
    public static InputStream getImage(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            return url.openStream();
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + imgUrl, ioexception);
        }
    }

    public static String getPathFromUrl(String url) {
        try {
            URL urlObject = new URL(url);
            return urlObject
                    .getPath()
                    .replaceFirst("/", "");
        } catch (MalformedURLException ex) {
            throw new ScrapeException("error converting url to path:" + url, ex);
        }
    }
}
