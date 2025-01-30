//package io.muzoo.ssc.utils;
//
//import java.io.File;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//public class BasicURLHandler implements UrlHandler {
//
//    @Override
//    public String cleanUrl(String url) {
//        try {
//            URI uri = new URI(url);
//            return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
//        } catch (URISyntaxException e) {
//            return ""; // Invalid URL
//        }
//    }
//
//    @Override
//    public String getFilePath(String url, String outputDir) {
//        String cleanedUrl = cleanUrl(url);
//        if (cleanedUrl.isEmpty()) {
//            return ""; // Return empty for invalid URLs
//        }
//
//        if (!outputDir.endsWith("/") && !outputDir.endsWith("\\")) {
//            outputDir += "/";
//        }
//
//        if (cleanedUrl.endsWith("/")) {
//            return outputDir + cleanedUrl.replace("https://", "").replace("/", File.separator) + "index.html";
//        } else {
//            return outputDir + cleanedUrl.replace("https://", "").replace("/", File.separator);
//        }
//    }
//}


package io.muzoo.ssc.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BasicURLHandler implements UrlHandler {

    @Override
    public String cleanUrl(String url) {
        try {
            URI uri = new URI(url.split("#")[0].split("\\?")[0]); // Strip query parameters and hashtags
            return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        } catch (URISyntaxException e) {
            return ""; // Invalid URL
        }
    }

    @Override
    public String getFilePath(String url, String outputDir) {
        String cleanedUrl = cleanUrl(url);
        if (cleanedUrl.isEmpty()) {
            return "";
        }

        if (!outputDir.endsWith(File.separator)) {
            outputDir += File.separator;
        }

        if (cleanedUrl.endsWith("/")) {
            return outputDir + cleanedUrl.replace("https://", "").replace("/", File.separator) + "index.html";
        } else {
            return outputDir + cleanedUrl.replace("https://", "").replace("/", File.separator);
        }
    }
}