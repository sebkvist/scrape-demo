package com.webscraper.scrapedemo.service.web.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UrlKeeper {
    static Logger logger = Logger.getLogger(UrlKeeper.class.getName());

    private Set<String> totalUniqueUrls = new HashSet<>();

    public Set<String> getTotalUniqueUrls() {
        return totalUniqueUrls;
    }

    public Set<String> extractAndSaveUniqueUrls(Set<String> discovered) {
        // filter alredy discovered urls
        Set<String> filtered = discovered.stream().filter((entry) -> !totalUniqueUrls.contains(entry))
                .collect(Collectors.toSet());

//        logger.info("new urls filtered:");
//        logger.info(Arrays.toString(filtered.toArray()));
        // filter relative, to start with
        filtered.forEach((entry) -> {
            if (!entry.contains("http")) {
                logger.info("new relative url:" + entry);
            }
        });

        totalUniqueUrls.addAll(filtered);
        return filtered;
    }

}
