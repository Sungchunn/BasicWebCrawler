package io.muzoo.ssc.utils;

public interface UrlHandler {
    String cleanUrl(String url);
    String getFilePath(String url, String outputDir);
}