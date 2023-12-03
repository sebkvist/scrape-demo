package com.webscraper.scrapedemo.model;

import java.util.HashSet;
import java.util.Set;

public record ScrapeResult (String content, Set<String> urlsToScrape, Set<String> imgLinks, Set<String> styleLinks) {

}
