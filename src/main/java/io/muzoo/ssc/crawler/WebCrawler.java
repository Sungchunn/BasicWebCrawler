package io.muzoo.ssc.crawler;

import io.muzoo.ssc.assignment.tracker.SscAssignment;
import io.muzoo.ssc.downloader.Downloader;
import io.muzoo.ssc.tracker.PTracker;
import io.muzoo.ssc.utils.UrlHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WebCrawler extends SscAssignment implements Crawler {
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
            System.out.println("Counting total links...");
            int totalLinks = LinkCounter.countTotalLinks(baseUrl);
            progressTracker.setTotalUrls(totalLinks);
            System.out.printf("Total links to crawl: %d%n", totalLinks);
            crawlBFS(baseUrl);
        } catch (Exception e) {
            System.err.println("Error during crawling: " + e.getMessage());
        }
    }

    /**
     * Original Crawl Version
     * @param url
     * @throws IOException
     */
    private void crawl(String url) throws IOException {
        if (visitedUrls.contains(url) || !url.startsWith(baseUrl)) {
            return; // Skip already visited or out-of-domain URLs
        }
        visitedUrls.add(url);

        String content;
        try {
            content = downloader.download(url);
        } catch (IOException e) {
            System.err.println("Failed to download: " + url);
            return;
        }

        String filePath = urlHandler.getFilePath(url, outputDir);
        downloader.saveToFile(content, filePath);
        progressTracker.incrementDownloadedUrls(url);

        Document doc = Jsoup.parse(content, url);
        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            if (nextUrl.isEmpty()) {
                nextUrl = link.absUrl("src");
            }
            crawlBFS(urlHandler.cleanUrl(nextUrl));
        }
    }

    /**
     * BFS Crawl
     * @param startUrl
     */
    private void crawlBFS(String startUrl) {
        Queue<String> queue = new LinkedList<>();
        queue.add(startUrl);
        visitedUrls.add(startUrl);

        while (!queue.isEmpty()) {
            String url = queue.poll();
            String content;
            try {
                content = downloader.download(url);
                String filePath = urlHandler.getFilePath(url, outputDir);
                downloader.saveToFile(content, filePath);
                progressTracker.incrementDownloadedUrls(url);
            } catch (IOException e) {
                System.err.println("Failed to download: " + url);
                continue;
            }

            Document doc = Jsoup.parse(content, url);
            Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (nextUrl.isEmpty()) {
                    nextUrl = link.absUrl("src");
                }
                nextUrl = urlHandler.cleanUrl(nextUrl);

                if (!visitedUrls.contains(nextUrl) && nextUrl.startsWith(baseUrl)) {
                    queue.add(nextUrl);
                    visitedUrls.add(nextUrl);
                }
            }
        }
    }

//    private int countTotalLinks(String url) throws IOException {
//        Set<String> allLinks = new HashSet<>();
//        exploreLinks(url, allLinks);
//        return allLinks.size();
//    }
//
//    private void exploreLinks(String url, Set<String> allLinks) throws IOException {
//        if (allLinks.contains(url) || !url.startsWith(baseUrl)) {
//            return;
//        }
//        allLinks.add(url);
//        String content;
//        try {
//            content = downloader.download(url);
//        } catch (IOException e) {
//            return; // Skip if unable to download
//        }
//
//        // Parse the content to find links
//        Document doc = Jsoup.parse(content, url);
//        Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");
//        for (Element link : links) {
//            String nextUrl = link.absUrl("href");
//            if (nextUrl.isEmpty()) {
//                nextUrl = link.absUrl("src");
//            }
//            exploreLinks(urlHandler.cleanUrl(nextUrl), allLinks);
//        }
//    }

}





