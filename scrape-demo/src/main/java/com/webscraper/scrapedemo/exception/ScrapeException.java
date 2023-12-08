package com.webscraper.scrapedemo.exception;

import java.io.IOException;

public class ScrapeException extends RuntimeException {

    public ScrapeException(String mess, Exception ex) {
        super(mess, ex);
    }

    public ScrapeException(String mess) {
        super(mess);
    }
}
