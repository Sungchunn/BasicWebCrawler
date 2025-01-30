//package io.muzoo.ssc.crawler;
//
//import io.muzoo.ssc.downloader.Downloader;
//import io.muzoo.ssc.tracker.PTracker;
//import io.muzoo.ssc.utils.BasicURLHandler;
//import io.muzoo.ssc.utils.UrlHandler;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Set;
//
//public class WebCrawler implements Crawler {
//    private final String baseUrl;
//    private final String outputDir;
//    private final Downloader downloader;
//    private final UrlHandler urlHandler;
//    private final PTracker progressTracker;
//    private final Set<String> visitedUrls = new HashSet<>();
//
//    public WebCrawler(String baseUrl, String outputDir, Downloader downloader, BasicURLHandler urlHandler, PTracker progressTracker) {
//        this.baseUrl = baseUrl;
//        this.outputDir = outputDir;
//        this.downloader = downloader;
//        this.urlHandler = urlHandler;
//        this.progressTracker = progressTracker;
//    }
//
//    @Override
//    public void startCrawling() throws IOException {
//        try {
//            int totalFiles = countTotalLinks(baseUrl);
//            progressTracker.setTotalUrls(totalFiles);
//            crawl(baseUrl);
//        } catch (Exception e) {
//            System.err.println("Error during crawling: " + e.getMessage());
//        }
//    }
//
//    private void crawl(String url) throws IOException {
//        if (visitedUrls.contains(url) || !url.startsWith(baseUrl)) {
//            return;
//        }
//        visitedUrls.add(url);
//
//        System.out.println("Crawling: " + url);
//
//        // Download the file
//        String content;
//        try {
//            content = downloader.download(url);
//        } catch (IOException e) {
//            System.err.println("Failed to download: " + url);
//            return;
//        }
//
//        String filePath = urlHandler.getFilePath(url, outputDir);
//        downloader.saveToFile(content, filePath);
//
//        progressTracker.incrementDownloadedUrls(url);
//
//        Document doc = Jsoup.parse(content, url);
//        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
//        for (Element link : links) {
//            String nextUrl = link.absUrl("href");
//            if (nextUrl.isEmpty()) {
//                nextUrl = link.absUrl("src");
//            }
//            crawl(urlHandler.cleanUrl(nextUrl));
//        }
//    }
//
//    private int countTotalLinks(String url) throws IOException {
//        Set<String> allLinks = new HashSet<>();
//        String content = downloader.download(url);
//        Document doc = Jsoup.parse(content, url);
//        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
//        for (Element link : links) {
//            String nextUrl = link.absUrl("href");
//            if (nextUrl.isEmpty()) {
//                nextUrl = link.absUrl("src");
//            }
//            allLinks.add(urlHandler.cleanUrl(nextUrl));
//        }
//        return allLinks.size();
//    }
//}
//


package io.muzoo.ssc.crawler;

import io.muzoo.ssc.downloader.Downloader;
import io.muzoo.ssc.tracker.PTracker;
import io.muzoo.ssc.utils.UrlHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler implements Crawler {
    private final String baseUrl;
    private final String outputDir;
    private final Downloader downloader;
    private final UrlHandler urlHandler;
    private final PTracker progressTracker;
    private final Set<String> visitedUrls = new HashSet<>();

    public WebCrawler(String baseUrl, String outputDir, Downloader downloader, UrlHandler urlHandler, PTracker progressTracker) {
        this.baseUrl = baseUrl;
        this.outputDir = outputDir;
        this.downloader = downloader;
        this.urlHandler = urlHandler;
        this.progressTracker = progressTracker;
    }

    @Override
    public void startCrawling() throws IOException {
        try {
            // Step 1: Pre-count total unique links
            System.out.println("Counting total links...");
            int totalLinks = countTotalLinks(baseUrl);
            progressTracker.setTotalUrls(totalLinks);
            System.out.printf("Total links to crawl: %d%n", totalLinks);

            // Step 2: Start the crawling process
            crawl(baseUrl);
        } catch (Exception e) {
            System.err.println("Error during crawling: " + e.getMessage());
        }
    }

    private void crawl(String url) throws IOException {
        if (visitedUrls.contains(url) || !url.startsWith(baseUrl)) {
            return; // Skip already visited or out-of-domain URLs
        }
        visitedUrls.add(url);

        // Download the content
        String content;
        try {
            content = downloader.download(url);
        } catch (IOException e) {
            System.err.println("Failed to download: " + url);
            return;
        }

        // Save the content
        String filePath = urlHandler.getFilePath(url, outputDir);
        downloader.saveToFile(content, filePath);

        // Update progress
        progressTracker.incrementDownloadedUrls(url);

        // Parse and crawl additional links
        Document doc = Jsoup.parse(content, url);
        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            if (nextUrl.isEmpty()) {
                nextUrl = link.absUrl("src");
            }
            crawl(urlHandler.cleanUrl(nextUrl));
        }
    }

    private int countTotalLinks(String url) throws IOException {
        Set<String> allLinks = new HashSet<>();
        exploreLinks(url, allLinks);
        return allLinks.size();
    }

    private void exploreLinks(String url, Set<String> allLinks) throws IOException {
        if (allLinks.contains(url) || !url.startsWith(baseUrl)) {
            return;
        }
        allLinks.add(url);

        // Download the content
        String content;
        try {
            content = downloader.download(url);
        } catch (IOException e) {
            return; // Skip if unable to download
        }

        // Parse the content to find links
        Document doc = Jsoup.parse(content, url);
        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            if (nextUrl.isEmpty()) {
                nextUrl = link.absUrl("src");
            }
            exploreLinks(urlHandler.cleanUrl(nextUrl), allLinks);
        }
    }

}