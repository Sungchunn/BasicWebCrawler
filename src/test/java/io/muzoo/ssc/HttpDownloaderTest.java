package io.muzoo.ssc;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HttpDownloaderTest {

    @Test
    public void testDownload() throws IOException {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        String testUrl = "http://example.com";
        String expectedContent = "Hello, World!";

        when(mockResponse.getEntity()).thenReturn(new StringEntity(expectedContent));
        when(mockHttpClient.execute(any(), any(HttpContext.class))).thenReturn(mockResponse);

        HttpDownloader downloader = new HttpDownloader(mockHttpClient);

        String actualContent = downloader.download(testUrl);

        assertEquals(expectedContent, actualContent);

        verify(mockHttpClient, times(1)).execute(any(), any(HttpContext.class));
        verify(mockResponse, times(1)).getEntity();
    }

    @Test
    public void testSaveToFile() throws IOException {
        String testContent = "Sample Content";
        Path tempFile = Files.createTempFile("testSaveToFile", ".txt");

        try {
            HttpDownloader downloader = new HttpDownloader(HttpClients.createDefault());
            downloader.saveToFile(testContent, tempFile.toString());
            String fileContent = Files.readString(tempFile);
            assertEquals(testContent, fileContent);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testSaveToFileWithMissingDirectories() throws IOException {
        String testContent = "Content with missing directories";
        Path tempDir = Files.createTempDirectory("testSaveToFileDirs");
        Path tempFile = tempDir.resolve("subdir/testFile.txt");

        try {
            HttpDownloader downloader = new HttpDownloader(HttpClients.createDefault());
            downloader.saveToFile(testContent, tempFile.toString());
            String fileContent = Files.readString(tempFile);
            assertEquals(testContent, fileContent);
        } finally {
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir.resolve("subdir"));
            Files.deleteIfExists(tempDir);
        }
    }
}