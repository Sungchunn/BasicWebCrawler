package io.muzoo.ssc;

import io.muzoo.ssc.crawler.WebCrawler;
import io.muzoo.ssc.downloader.HttpDownloader;
import io.muzoo.ssc.tracker.PTracker;
import io.muzoo.ssc.utils.BasicURLHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String baseUrl = "https://cs.muic.mahidol.ac.th/courses/ssc/docs/";
        String outputDir = "output";

        logger.info("Starting the web crawler...");
        CloseableHttpClient httpClient = HttpClients.createDefault();

        WebCrawler crawler = new WebCrawler(
                baseUrl,
                outputDir,
                new HttpDownloader(httpClient),
                new BasicURLHandler(),
                new PTracker()
        );

        try {
            crawler.startCrawling();
            logger.info("Crawling completed successfully.");
        } catch (Exception e) {
            logger.error("An error occurred during crawling: ", e);
        }
    }
}