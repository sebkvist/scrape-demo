package com.webscraper.scrapedemo.service.file;

import com.webscraper.scrapedemo.exception.ScrapeException;

import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * A file service for saving files to a local folder.
 */
public class LocalFileService {

    private Logger logger = Logger.getLogger(LocalFileService.class.getName());

    private final String workingDir;

    public LocalFileService(String workingDir) {
        this.workingDir = workingDir;
    }

    public void savePage(String path, String text) {
        try {
            logger.fine("saving text page from path:" + path);
            File file = createNewFile(path);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter((file)))) {
                writer.write(text);
            }

        } catch (Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    public void saveFileData(String path, byte[] img) {
        try {
            logger.fine("saving file data to path:" + path);
            File file = createNewFile(path);

            try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream((file)))) {
                writer.write(img);
            }
        } catch (Exception ex) {
            throw new ScrapeException("error saving path:" + path, ex);
        }
    }

    private String createPathFromUrl(String urlPath)  {
        try {
            String path = workingDir + File.separator + Paths.get(urlPath);
            return path;
        } catch (Exception ex) {
            throw new ScrapeException("error creating path from url:" + urlPath, ex);
        }
    }

    private File createNewFile(String path) throws IOException {
        String pathFromUrl = createPathFromUrl(path);
        logger.fine("creating new file with path:" + path + ", to: " + pathFromUrl);
        File file = new File(pathFromUrl);

        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            logger.fine("making parent dirs, result:" + mkdirs + ", path:" + parentFile.getPath());
        }
        if ( !file.createNewFile() ) {
            throw new ScrapeException(("File already exists:" + file.getPath()));
        }
        return file;
    }

}
