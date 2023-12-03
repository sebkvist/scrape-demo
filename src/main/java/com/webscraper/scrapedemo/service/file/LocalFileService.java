package com.webscraper.scrapedemo.service.file;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LocalFileService {

    static Logger logger = Logger.getLogger(LocalFileService.class.getName());

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") +  "ScrapeResult";

    public void savePage(String path, String text) {

        try {
            logger.info("saving text page from path:" + path);
            File file = createNewFile(path);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter((file)))) {
                writer.write(text);
            }

        } catch (Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public void saveImage(String path, byte[] img) {
        try {
            logger.info("saving image to path:" + path);
            File file = createNewFile(path);

            try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream((file)))) {
                writer.write(img);
            }
        } catch (Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public void saveStyle(String path, byte[] data) {
        try {
            logger.info("saving style to path:" + path);
            File file = createNewFile(path);

            try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream((file)))) {
                writer.write(data);
            }
        } catch (Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public static String createPathFromUrl(String urlPath)  {
        try {
            String path = TEMP_DIR + File.separator + Paths.get(urlPath);
            logger.info("path created:" + path);
            return path;
        } catch (Exception ex) {
            throw new ScrapeException("error creating path from url:" + urlPath, ex);
        }
    }

    private static File createNewFile(String path) throws IOException {
        String pathFromUrl = createPathFromUrl(path);
        logger.info("creating new file with path:" + path + ", to: " + pathFromUrl);
        File file = new File(pathFromUrl);

        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            logger.info("making dirs, result:" + mkdirs + ", path:" + parentFile.getPath());
        }
        if ( !file.createNewFile() ) {
            throw new ScrapeException(("File already exists:" + file.getPath()));
        }
        return file;
    }

}
