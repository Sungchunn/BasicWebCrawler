package io.muzoo.ssc.crawler;

import io.muzoo.ssc.downloader.Downloader;
import io.muzoo.ssc.tracker.ProgressTracker;
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
    private final ProgressTracker progressTracker;
    private final Set<String> visitedUrls = new HashSet<>();

    // Constructor
    public WebCrawler(
            String baseUrl,
            String outputDir,
            Downloader downloader,
            UrlHandler urlHandler,
            ProgressTracker progressTracker) {
        this.baseUrl = baseUrl;
        this.outputDir = outputDir;
        this.downloader = downloader;
        this.urlHandler = urlHandler;
        this.progressTracker = progressTracker;
    }

    @Override
    public void startCrawling() throws IOException {
        crawl(baseUrl);
    }

    private void crawl(String url) throws IOException {
        if (visitedUrls.contains(url) || !url.startsWith(baseUrl)) {
            return;
        }
        visitedUrls.add(url);
        progressTracker.incrementTotalUrls();

        System.out.println("Crawling: " + url);

        // Download the file
        String content = downloader.download(url);

        // Save to the appropriate file path
        String filePath = urlHandler.getFilePath(url, outputDir);
        downloader.saveToFile(content, filePath);

        progressTracker.incrementDownloadUrls();

        // Parse the content for more links
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
}