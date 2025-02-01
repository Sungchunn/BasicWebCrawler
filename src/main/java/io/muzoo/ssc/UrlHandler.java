package io.muzoo.ssc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The UrlHandler class provides utility methods to process and manipulate URLs.
 * It is responsible for cleaning URLs (e.g., removing query parameters and fragments)
 * and generating file paths for saving content based on the URL structure.
 */
public class UrlHandler {

    /**
     * Cleans a given URL by removing query parameters and fragments.
     *
     * This method ensures that the resulting URL includes only the scheme,
     * host, and path components.
     *
     * @param url the URL to be cleaned
     * @return a cleaned URL string; returns an empty string if the URL is invalid
     */
    public String cleanUrl(String url) {
        try {
            URI uri = new URI(url.split("#")[0].split("\\?")[0]); // Strip query parameters and hashtags
            return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        } catch (URISyntaxException e) {
            return ""; // Invalid URL
        }
    }

    /**
     * Generates a file path for saving content based on the given URL and output directory.
     *
     * - Converts the URL into a file-friendly format (e.g., replacing slashes).
     * - Handles directory separators to ensure compatibility with the operating system.
     * - Appends "index.html" if the URL represents a directory.
     *
     * @param url the URL for which the file path is generated
     * @param outputDir the directory where the file should be saved
     * @return the generated file path as a string; returns an empty string if the URL is invalid
     */
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