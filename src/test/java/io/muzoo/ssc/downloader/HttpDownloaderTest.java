package io.muzoo.ssc.downloader;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpDownloaderTest {

    private HttpDownloader httpDownloader;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockResponse;

    @TempDir
    Path tempDir; // Temporary directory for file testing

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(CloseableHttpResponse.class);
        httpDownloader = new HttpDownloader(mockHttpClient);
    }

    @Test
    void testSuccessfulDownload() throws Exception {
        // Mock HTTP response
        HttpEntity entity = new StringEntity("Mock response content");
        when(mockResponse.getEntity()).thenReturn(entity);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        // Test download
        String content = httpDownloader.download("https://example.com");
        assertEquals("Mock response content", content, "Downloaded content does not match expected result.");
    }

    @Test
    void testDownloadWithHttpError() throws Exception {
        // Mock HTTP response with an error
        when(mockResponse.getCode()).thenReturn(404);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        // Expect an IOException due to HTTP error
        IOException exception = assertThrows(IOException.class, () -> {
            httpDownloader.download("https://example.com");
        });

        assertTrue(exception.getMessage().contains("Failed to download"), "Expected HTTP error handling.");
    }

    @Test
    void testDownloadWithNullResponse() throws Exception {
        // Mock HTTP response with null entity
        when(mockResponse.getEntity()).thenReturn(null);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        // Expect IOException due to null response
        IOException exception = assertThrows(IOException.class, () -> {
            httpDownloader.download("https://example.com");
        });

        assertTrue(exception.getMessage().contains("Empty response"), "Expected empty response handling.");
    }

    @Test
    void testSaveToFile() throws IOException {
        // Create a temporary file
        Path testFile = tempDir.resolve("test_output.txt");
        String content = "Test file content";

        // Execute saveToFile method
        httpDownloader.saveToFile(content, testFile.toString());

        // Verify file exists and content is correct
        assertTrue(Files.exists(testFile), "File should exist after saving.");
        assertEquals(content, Files.readString(testFile), "File content does not match expected.");
    }

    @Test
    void testSaveToFileWithInvalidPath() {
        // Expect an IOException when attempting to save to an invalid path
        IOException exception = assertThrows(IOException.class, () -> {
            httpDownloader.saveToFile("Test content", "/invalid/path/test.txt");
        });

        assertTrue(exception.getMessage().contains("Invalid"), "Expected IOException for invalid path.");
    }
}