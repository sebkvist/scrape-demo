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

        } catch(Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public void saveImage(String path, byte[] img) {
        //TODO: implement
        try {
            logger.info("saving image to path:" + path);
            File file = createNewFile(path);
            try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream((file)))) {
                writer.write(img);
            }
        } catch(Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public static String createPathFromUrl(String urlPath)  {

        try {
            String path = TEMP_DIR + File.separator + Paths.get(urlPath);
            logger.info("path created:" + path);
            return path;
        }catch (Exception ex) {
            throw new ScrapeException("error creating path from url:" + urlPath, ex);

        }
    }

    private static File createNewFile(String path) throws IOException {
        File file = new File(createPathFromUrl(path));
        if ( !file.createNewFile() ) {
            throw new ScrapeException(("File already exists:" + file.getPath()));
        }
        return file;
    }

}
