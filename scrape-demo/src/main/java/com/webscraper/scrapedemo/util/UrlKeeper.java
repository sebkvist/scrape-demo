package com.webscraper.scrapedemo.util;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A helper class to extract and keep track of unique discovered urls.
 *
 */
public class UrlKeeper {

    private Set<String> totalUniqueUrls;

    public UrlKeeper() {
//        totalUniqueUrls = Collections.synchronizedSet(new HashSet<>());
        totalUniqueUrls = new HashSet<>();
    }

    /**
     * Returns the total number of unique urls discovered.
     *
     * @return
     */
    public synchronized Integer getTotalUniqueUrls() {
        return totalUniqueUrls.size();
    }

    /**
     * Filters out the unique URLs from the newly discovered ones and keeps track of them.
     *
     * @param discovered the whole set of discovered URLs.
     *
     * @return the unique URLs extracted from the whole set of discovered URLs.
     */
    public synchronized Set<String> extractAndKeepUniqueUrls(Set<String> discovered) {
        // filter already discovered urls
        Set<String> filtered = discovered.stream().filter((entry) -> !totalUniqueUrls.contains(entry))
                .collect(Collectors.toSet());

        totalUniqueUrls.addAll(filtered);
        return filtered;
    }

}
