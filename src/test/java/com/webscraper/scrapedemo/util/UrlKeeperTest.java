package com.webscraper.scrapedemo.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class UrlKeeperTest {

    // test object
    private UrlKeeper urlKeeper;

    private Set<String> tempUrls;


    @BeforeEach
    void init() {
        urlKeeper = new UrlKeeper();
        tempUrls = populateNewSet(1, 10);
    }

    @Test
    void testThatOnlyUniqueUrlsAreSavedAll() {
        Set<String> savedSet;

        savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);
        Assertions.assertThat(savedSet.size()).isEqualTo(10);

        savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);
        Assertions.assertThat(savedSet.size()).isEqualTo(0);

        tempUrls = populateNewSet(11, 20);
        savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);
        Assertions.assertThat(savedSet.size()).isEqualTo(10);

        //Assertions.fail("test failure");
    }

    @Test
    void testThatFirstUniqueUrlsAreSaved() {
        Set<String> savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);

        Assertions.assertThat(savedSet.size()).isEqualTo(10);
        Assertions.assertThat(urlKeeper.getTotalUniqueUrls()).isEqualTo(10);
    }

    @Test
    void testThatNoDuplicateUrlsAreSaved() {
        urlKeeper.extractAndKeepUniqueUrls(tempUrls);

        Set<String> savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);

        Assertions.assertThat(savedSet.size()).isEqualTo(0);
        Assertions.assertThat(urlKeeper.getTotalUniqueUrls()).isEqualTo(10);
    }

    @Test
    void testThatAllUniqueUrlsAreSaved() {
        Set<String> savedSet;
        urlKeeper.extractAndKeepUniqueUrls(tempUrls);
        tempUrls = populateNewSet(11, 20);

        savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);

        Assertions.assertThat(savedSet.size()).isEqualTo(10);
        Assertions.assertThat(urlKeeper.getTotalUniqueUrls()).isEqualTo(20);
    }

    @Test
    void testThatOnlyUniqueUrlsAreSaved() {
        Set<String> savedSet;
        urlKeeper.extractAndKeepUniqueUrls(tempUrls);
        tempUrls = populateNewSet(5, 15);

        savedSet = urlKeeper.extractAndKeepUniqueUrls(tempUrls);

        Assertions.assertThat(savedSet.size()).isEqualTo(5);
        Assertions.assertThat(urlKeeper.getTotalUniqueUrls()).isEqualTo(15);
    }


    private Set<String> populateNewSet(int startIndex, int endIndex) {
        Set<String> tempUrls = new HashSet<>();
        for(int index = startIndex; index <= endIndex; index++) {
            tempUrls.add("url" + index);
        }
        return tempUrls;
    }

}