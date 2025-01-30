package io.muzoo.ssc.crawler;

import io.muzoo.ssc.downloader.Downloader;
import io.muzoo.ssc.tracker.PTracker;
import io.muzoo.ssc.utils.UrlHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebCrawlerTest {

    private static final String BASE_URL = "http://example.com";
    private static final String OUTPUT_DIR = "output";

    @Mock
    private Downloader downloader;

    @Mock
    private UrlHandler urlHandler;

    @Mock
    private PTracker progressTracker;

    private WebCrawler webCrawler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webCrawler = new WebCrawler(BASE_URL, OUTPUT_DIR, downloader, urlHandler, progressTracker);
    }

    @Test
    void testStartCrawlingWithSinglePage() throws IOException {
        // Mock the HTML content of the main page
        String html = "<html><body>" +
                "<a href='http://example.com/page1'>Page 1</a>" +
                "<img src='http://example.com/image.jpg'>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(html);
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));
        when(urlHandler.getFilePath(anyString(), anyString())).thenReturn("output.html");

        // Execute the crawl
        webCrawler.startCrawling();

        // Verify
        verify(downloader, times(1)).download(BASE_URL);
        verify(downloader, times(1)).download("http://example.com/page1");
        verify(downloader, times(1)).saveToFile(anyString(), anyString());
        verify(progressTracker, times(2)).incrementDownloadedUrls(anyString()); // Main page + page1
    }

    @Test
    void testCrawlingWithMultiplePages() throws IOException {
        // Prepare test data for main page
        String mainHtml = "<html><body>" +
                "<a href='http://example.com/page1'>Page 1</a>" +
                "<a href='http://example.com/page2'>Page 2</a>" +
                "</body></html>";

        String page1Html = "<html><body>" +
                "<img src='http://example.com/image1.jpg'>" +
                "</body></html>";

        String page2Html = "<html><body>" +
                "<script src='http://example.com/script.js'></script>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(mainHtml);
        when(downloader.download("http://example.com/page1")).thenReturn(page1Html);
        when(downloader.download("http://example.com/page2")).thenReturn(page2Html);
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));
        when(urlHandler.getFilePath(anyString(), anyString())).thenReturn("output.html");

        // Execute
        webCrawler.startCrawling();

        // Verify
        verify(downloader, atLeast(3)).download(anyString()); // Main + page1 + page2
        verify(progressTracker, atLeast(3)).incrementDownloadedUrls(anyString());
    }

    @Test
    void testCrawlingWithExternalLinks() throws IOException {
        // Mock page with internal and external links
        String html = "<html><body>" +
                "<a href='http://example.com/internal'>Internal Link</a>" +
                "<a href='http://external.com/page'>External Link</a>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(html);
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));

        // Execute
        webCrawler.startCrawling();

        // Verify only internal links are crawled
        verify(downloader, never()).download("http://external.com/page");
        verify(downloader).download("http://example.com/internal");
    }

    @Test
    void testCrawlingWithDownloadErrors() throws IOException {
        // Mock page with one good and one bad link
        String html = "<html><body>" +
                "<a href='http://example.com/good'>Good Link</a>" +
                "<a href='http://example.com/bad'>Bad Link</a>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(html);
        when(downloader.download("http://example.com/good")).thenReturn("<html></html>");
        when(downloader.download("http://example.com/bad")).thenThrow(new IOException("Download failed"));
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));

        // Execute
        webCrawler.startCrawling();

        // Verify that crawling continued after an error
        verify(downloader).download("http://example.com/good");
        verify(downloader, never()).saveToFile(anyString(), eq("http://example.com/bad"));
    }

    @Test
    void testCrawlingWithCyclicLinks() throws IOException {
        // Mock cyclic links
        String html = "<html><body>" +
                "<a href='http://example.com'>Self Link</a>" +
                "<a href='http://example.com/page1'>Page 1</a>" +
                "</body></html>";

        String page1Html = "<html><body>" +
                "<a href='http://example.com'>Back to Main</a>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(html);
        when(downloader.download("http://example.com/page1")).thenReturn(page1Html);
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));

        // Execute
        webCrawler.startCrawling();

        // Verify each URL is crawled only once
        verify(downloader, times(1)).download(BASE_URL);
        verify(downloader, times(1)).download("http://example.com/page1");
    }

    @Test
    void testCrawlingWithEmptyUrls() throws IOException {
        // Mock a page with empty/invalid URLs
        String html = "<html><body>" +
                "<a href=''>Empty Link</a>" +
                "<img src=''>" +
                "<a href='http://example.com/valid'>Valid Link</a>" +
                "</body></html>";

        // Setup mocks
        when(downloader.download(BASE_URL)).thenReturn(html);
        when(urlHandler.cleanUrl(anyString())).thenAnswer(i -> i.getArgument(0));

        // Execute
        webCrawler.startCrawling();

        // Verify only valid URLs are processed
        verify(downloader).download("http://example.com/valid");
        verify(progressTracker, atLeastOnce()).incrementDownloadedUrls(anyString());
    }
}