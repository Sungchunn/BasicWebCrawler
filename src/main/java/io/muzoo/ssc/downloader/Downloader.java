package io.muzoo.ssc.downloader;

import java.io.IOException;

public interface Downloader {
    String download(String url) throws IOException;

    void saveToFile(String fileName, String content) throws IOException;
}

