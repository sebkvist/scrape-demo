package com.webscraper.scrapedemo.util;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A Url utility for tasks involving url:s
 */
public class UrlHandler {
    /**
     * Get access to file with specified url path.
     *
     * @param url
     * @return
     */
    public static InputStream getFileData(String url) {
        try {
            URL urlObject = new URL(url);
            return urlObject.openStream();
        } catch (IOException ioexception) {
            throw new ScrapeException("Error connecting to url: " + url, ioexception);
        }
    }

    /**
     * Extract the subfolder path of the url.
     *
     * @param url the total url, eg "www.website.com/path/to/file"
     * @return the extract path, eg "path/to/file.html"
     */
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
