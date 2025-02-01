package io.muzoo.ssc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlerManagerTest {

    @Test
    public void testRunCrawlerCreatesOutputDirectory() {
        String baseUrl = "https://example.com";
        String outputDir = "test_output";
        File outputDirectory = new File(outputDir);

        if (outputDirectory.exists()) {
            outputDirectory.delete();
        }

        CrawlerManager crawlerManager = new CrawlerManager(baseUrl, outputDir);
        crawlerManager.runCrawler();

        assertTrue(outputDirectory.exists());
        outputDirectory.delete();
    }

    @Test
    public void testRunCrawlerHandlesInvalidOutputDirectory() {
        String baseUrl = "https://example.com";
        String outputDir = "";

        CrawlerManager crawlerManager = new CrawlerManager(baseUrl, outputDir);

        Exception exception = assertThrows(IllegalStateException.class, crawlerManager::runCrawler);
        assertEquals("Failed to create output directory: ", exception.getMessage());
    }

    @Test
    public void testRunCrawlerLogsPerformanceStats() throws IOException {
        String baseUrl = "https://example.com";
        String outputDir = "test_output";
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();

        HttpDownloader downloaderMock = Mockito.mock(HttpDownloader.class);
        ProgressTracker progressTrackerMock = Mockito.mock(ProgressTracker.class);
        UrlHandler urlHandlerMock = Mockito.mock(UrlHandler.class);
        WebCrawler crawlerMock = Mockito.mock(WebCrawler.class);

        CrawlerManager crawlerManager = new CrawlerManager(baseUrl, outputDir);
        crawlerManager.runCrawler();

        verify(crawlerMock, times(1)).startCrawling();
        outputDirectory.delete();
    }
}