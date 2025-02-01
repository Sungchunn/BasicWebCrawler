package io.muzoo.ssc;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * The CrawlerManager class is responsible for managing the setup and execution
 * of the web crawling process. It creates the necessary dependencies, handles
 * initialization, and logs the performance statistics of the crawling operation.
 */
public class CrawlerManager {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerManager.class);
    private final String baseUrl;
    private final String outputDir;

    /**
     * Constructor to initialize the CrawlerManager with the base URL and output directory.
     *
     * @param baseUrl the starting URL for the web crawler
     * @param outputDir the directory where downloaded content will be saved
     */
    public CrawlerManager(String baseUrl, String outputDir) {
        this.baseUrl = baseUrl;
        this.outputDir = outputDir;
    }

    /**
     * Runs the web crawler by setting up necessary dependencies, creating the output directory,
     * and measuring the performance of the crawling process.
     */
    public void runCrawler() {
        // Ensure the output directory exists
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            logger.error("Failed to create output directory: {}", outputDir);
            System.exit(1);
        }

        logger.info("Starting the web crawler...");
        long startTime = System.nanoTime();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDownloader downloader = new HttpDownloader(httpClient);
            ProgressTracker progressTracker = new ProgressTracker();
            UrlHandler urlHandler = new UrlHandler();
            WebCrawler crawler = new WebCrawler(
                    baseUrl,
                    outputDir,
                    downloader,
                    progressTracker,
                    urlHandler
            );

            crawler.startCrawling();
            logger.info("Crawling completed successfully.");
        } catch (IOException e) {
            logger.error("An error occurred during crawling: {}", e.getMessage(), e);
        } finally {
            long duration = System.nanoTime() - startTime;
            double durationSeconds = duration / 1_000_000_000.0; // Convert duration to seconds
            long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            logger.info("Duration (s): {} (s)", String.format("%.3f", durationSeconds));
            logger.info("Memory usage: {} MB", memoryUsage / (1024 * 1024));
        }
    }
}