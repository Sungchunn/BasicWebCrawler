package io.muzoo.ssc.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class BasicURLHandler implements UrlHandler {

    @Override
    public String cleanUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        } catch (URISyntaxException e) {
            return ""; // Invalid URL
        }
    }

    @Override
    public String getFilePath(String url, String outputDir) {
        String cleanedUrl = cleanUrl(url);
        if (cleanedUrl.endsWith("/")) {
            return outputDir + cleanedUrl.replace("https://", "").replace("/", "/") + "index.html";
        } else {
            return outputDir + cleanedUrl.replace("https://", "").replace("/", "/");
        }
    }
}