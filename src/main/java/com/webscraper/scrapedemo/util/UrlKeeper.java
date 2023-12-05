package com.webscraper.scrapedemo.util;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A helper class to extract and keep track of unique discovered urls.
 *
 */
public class UrlKeeper {
    static Logger logger = Logger.getLogger(UrlKeeper.class.getName());

    private Set<String> totalUniqueUrls = new HashSet<>();

    public Integer getTotalUniqueUrls() {
        return totalUniqueUrls.size();
    }

    public Set<String> extractAndKeepUniqueUrls(Set<String> discovered) {
        // filter already discovered urls
        Set<String> filtered = discovered.stream().filter((entry) -> !totalUniqueUrls.contains(entry))
                .collect(Collectors.toSet());


        totalUniqueUrls.addAll(filtered);
        return filtered;
    }

}
