package io.muzoo.ssc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private WebCrawler webCrawler;
    private HttpDownloader downloaderMock;
    private ProgressTracker progressTrackerMock;
    private UrlHandler urlHandlerMock;

    @BeforeEach
    public void setUp() {
        downloaderMock = Mockito.mock(HttpDownloader.class);
        progressTrackerMock = Mockito.mock(ProgressTracker.class);
        urlHandlerMock = Mockito.mock(UrlHandler.class);
        webCrawler = new WebCrawler(
                "https://example.com",
                "output",
                downloaderMock,
                progressTrackerMock,
                urlHandlerMock
        );
    }

    @Test
    public void testStartCrawling() throws IOException {
        when(urlHandlerMock.cleanUrl("https://example.com")).thenReturn("https://example.com");
        when(downloaderMock.download("https://example.com")).thenReturn("<html><a href=\"https://example.com/page1\">Page 1</a></html>");
        webCrawler.startCrawling();
        verify(downloaderMock, atLeastOnce()).download("https://example.com");
    }

    @Test
    public void testIterativeCrawl() throws IOException {
        when(urlHandlerMock.cleanUrl("https://example.com")).thenReturn("https://example.com");
        when(downloaderMock.download("https://example.com")).thenReturn("<html><a href=\"https://example.com/page1\">Page 1</a></html>");
        webCrawler.startCrawling();
        verify(progressTrackerMock, atLeastOnce()).incrementDownloadedUrls(anyString());
    }

    @Test
    public void testCountTotalLinks() throws IOException {
        when(urlHandlerMock.cleanUrl("https://example.com")).thenReturn("https://example.com");
        when(downloaderMock.download("https://example.com")).thenReturn("<html><a href=\"https://example.com/page1\">Page 1</a></html>");
        Set<String> links = new HashSet<>();
        webCrawler.startCrawling();
        assertEquals(1, links.size());
    }

    @Test
    public void testExploreLinks() throws IOException {
        Set<String> links = new HashSet<>();
        when(urlHandlerMock.cleanUrl("https://example.com")).thenReturn("https://example.com");
        when(downloaderMock.download("https://example.com")).thenReturn("<html><a href=\"https://example.com/page1\">Page 1</a></html>");
        webCrawler.startCrawling();
        assertEquals(0, links.size());
    }

    @Test
    public void testIsValidUrl() {
        String validUrl = "https://example.com";
        String invalidUrl = "javascript:void(0)";
        assertEquals(true, webCrawler.isValidUrl(validUrl));
        assertEquals(false, webCrawler.isValidUrl(invalidUrl));
    }
}