package io.muzoo.ssc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * The WebCrawler class is responsible for crawling a website starting from a base URL.
 * It downloads content, saves it to the output directory, and logs progress.
 * It supports both iterative and recursive approaches for counting and exploring links.
 */
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private final String baseUrl;
    private final String outputDir;
    private final HttpDownloader downloader;
    private final ProgressTracker progressTracker;
    private final UrlHandler urlHandler;
    private final Set<String> visitedUrls = new HashSet<>();


    /**
     * Constructor to initialize the WebCrawler with necessary dependencies.
     *
     * @param baseUrl the starting URL for the crawler
     * @param outputDir the directory to save downloaded content
     * @param downloader the downloader for handling HTTP requests
     * @param progressTracker the tracker for monitoring progress
     * @param urlHandler the handler for cleaning URLs and generating file paths
     */
    public WebCrawler(String baseUrl, String outputDir, HttpDownloader downloader, ProgressTracker progressTracker, UrlHandler urlHandler) {
        this.baseUrl = baseUrl;
        this.outputDir = outputDir;
        this.downloader = downloader;
        this.progressTracker = progressTracker;
        this.urlHandler = urlHandler;
    }

    /**
     * Starts the crawling process, including pre-counting total links and iterating through them.
     */
    public void startCrawling() throws IOException {
        logger.info("Starting the web crawler...");

        try {
            // Pre-count total unique links (optional, based on requirements)
            logger.info("Counting total links...");
            int totalLinks = countTotalLinks(baseUrl);
            progressTracker.setTotalUrls(totalLinks);
            logger.info("Total links to crawl: {}", totalLinks);

            // Start the crawling process
            iterativeCrawl(baseUrl);
        } catch (Exception e) {
            logger.error("Error during crawling: ", e);
        }
    }

    /**
     * Iteratively crawls links using a stack-based approach.
     *
     * @param startUrl the starting URL for the crawling process
     */
    private void iterativeCrawl(String startUrl) {
        Stack<String> stack = new Stack<>();
        stack.push(startUrl);

        while (!stack.isEmpty()) {
            String url = stack.pop();
            String cleanUrl = urlHandler.cleanUrl(url);

            if (cleanUrl.isEmpty() || visitedUrls.contains(cleanUrl) || !cleanUrl.startsWith(baseUrl)) {
                continue; // Skip invalid, already visited, or out-of-domain URLs
            }

            visitedUrls.add(cleanUrl);

            // Download the content
            String content;
            try {
                content = downloader.download(cleanUrl);
                progressTracker.incrementDownloadedUrls(cleanUrl);
            } catch (IOException e) {
                logger.error("Failed to download: {}", cleanUrl, e);
                continue;
            }

            // Save the content
            String filePath = urlHandler.getFilePath(cleanUrl, outputDir);
            if (!filePath.isEmpty()) {
                try {
                    downloader.saveToFile(content, filePath);
                } catch (IOException e) {
                    logger.error("Failed to save file: {}", filePath, e);
                    continue;
                }
            }

            // Parse and add additional links to the stack
            try {
                Document doc = Jsoup.parse(content, cleanUrl);
                Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
                for (Element link : links) {
                    String nextUrl = link.absUrl("href");
                    if (nextUrl.isEmpty()) {
                        nextUrl = link.absUrl("src");
                    }
                    if (!nextUrl.isEmpty() && isValidUrl(nextUrl)) {
                        stack.push(nextUrl); // Add to stack for iterative crawling
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to parse content from URL: {}", cleanUrl, e);
            }
        }
    }

    /**
     * Counts the total number of unique links accessible from the given starting URL.
     *
     * @param url The starting URL to begin counting links.
     * @return The total number of unique links discovered.
     * @throws IOException If there is an issue downloading or parsing the content.
     */
    private int countTotalLinks(String url) throws IOException {
        Set<String> allLinks = new HashSet<>();
        exploreLinks(url, allLinks);
        return allLinks.size();
    }

    /**
     * Recursively explores links from the given URL, adds unique links to the set,
     * and continues exploration for unvisited links.
     *
     * @param url The URL to explore.
     * @param allLinks A set containing all the unique links discovered so far.
     * @throws IOException If there is an issue downloading or parsing the content.
     */
    private void exploreLinks(String url, Set<String> allLinks) throws IOException {
        String cleanUrl = urlHandler.cleanUrl(url);
        if (cleanUrl.isEmpty() || allLinks.contains(cleanUrl) || !cleanUrl.startsWith(baseUrl)) {
            return;
        }
        allLinks.add(cleanUrl);

        // Download and parse the content
        try {
            String content = downloader.download(cleanUrl);
            Document doc = Jsoup.parse(content, cleanUrl);
            Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (nextUrl.isEmpty()) {
                    nextUrl = link.absUrl("src");
                }
                if (!nextUrl.isEmpty() && isValidUrl(nextUrl)) {
                    exploreLinks(nextUrl, allLinks);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to explore links from URL: {}", cleanUrl, e);
        }
    }


//    /**
//     * Counts the total number of unique links starting from the base URL.
//     *
//     * @param url the URL to start counting links from
//     * @return the total number of unique links
//     * @throws IOException if an error occurs during the counting process
//     */
//    private int countTotalLinks(String url) throws IOException {
//        Set<String> allLinks = Collections.synchronizedSet(new HashSet<>());
//        Queue<String> queue = new LinkedList<>();
//        queue.add(url);
//
//        while (!queue.isEmpty()) {
//            String currentUrl = queue.poll();
//            if (currentUrl == null || allLinks.contains(currentUrl)) {
//                continue; // Skip null or already visited links
//            }
//            allLinks.add(currentUrl);
//
//            // Explore links for the current URL
//            try {
//                exploreLinks(currentUrl, allLinks, queue);
//            } catch (IOException e) {
//                logger.error("Failed to explore links from URL: {}", currentUrl, e);
//            }
//        }
//        return allLinks.size();
//    }
//
//    /**
//     * Explores additional links from the given URL and adds them to the queue.
//     *
//     * @param url the URL to explore links from
//     * @param allLinks the set of all discovered links
//     * @param queue the queue to store links for further exploration
//     * @throws IOException if an error occurs while downloading or parsing the content
//     */
//    private void exploreLinks(String url, Set<String> allLinks, Queue<String> queue) throws IOException {
//        String cleanUrl = urlHandler.cleanUrl(url);
//        if (cleanUrl.isEmpty() || allLinks.contains(cleanUrl) || !cleanUrl.startsWith(baseUrl)) {
//            return;
//        }
//
//        String content;
//        try {
//            content = downloader.download(cleanUrl);
//        } catch (IOException e) {
//            logger.error("Failed to download URL: {}", cleanUrl, e);
//            return;
//        }
//
//        Document doc = Jsoup.parse(content, cleanUrl);
//        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
//        for (Element link : links) {
//            String nextUrl = link.absUrl("href");
//            if (nextUrl.isEmpty()) {
//                nextUrl = link.absUrl("src");
//            }
//            if (!nextUrl.isEmpty() && isValidUrl(nextUrl) && !allLinks.contains(nextUrl)) {
//                queue.add(nextUrl);
//            }
//        }
//    }

    /**
     * Validates whether a URL is valid and belongs to the allowed protocol and domain.
     *
     * @param url the URL to validate
     * @return true if the URL is valid; false otherwise
     */
    public boolean isValidUrl(String url) {
        try {
            new URI(url).parseServerAuthority();
            return url.startsWith("http") && !url.toLowerCase().startsWith("javascript:");
        } catch (URISyntaxException e) {
            logger.debug("Invalid URL: {}", url);
            return false;
        }
    }
}

