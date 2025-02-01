package io.muzoo.ssc;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class UrlHandlerTest {

    @Test
    public void testCleanUrlValidUrl() {
        UrlHandler handler = new UrlHandler();
        String url = "https://example.com/path?query=123#fragment";
        String expected = "https://example.com/path";
        assertEquals(expected, handler.cleanUrl(url));
    }

    @Test
    public void testCleanUrlInvalidUrl() {
        UrlHandler handler = new UrlHandler();
        String url = "invalid-url";
        assertEquals("", handler.cleanUrl(url));
    }

    @Test
    public void testGetFilePathForDirectoryUrl() {
        UrlHandler handler = new UrlHandler();
        String url = "https://example.com/directory/";
        String outputDir = "output";
        String expected = "output" + File.separator + "example.com" + File.separator + "directory" + File.separator + "index.html";
        assertEquals(expected, handler.getFilePath(url, outputDir));
    }

    @Test
    public void testGetFilePathForFileUrl() {
        UrlHandler handler = new UrlHandler();
        String url = "https://example.com/file.txt";
        String outputDir = "output";
        String expected = "output" + File.separator + "example.com" + File.separator + "file.txt";
        assertEquals(expected, handler.getFilePath(url, outputDir));
    }

    @Test
    public void testGetFilePathInvalidUrl() {
        UrlHandler handler = new UrlHandler();
        String url = "invalid-url";
        String outputDir = "output";
        assertEquals("", handler.getFilePath(url, outputDir));
    }

    @Test
    public void testGetFilePathOutputDirWithoutSeparator() {
        UrlHandler handler = new UrlHandler();
        String url = "https://example.com/file.txt";
        String outputDir = "output";
        String expected = "output" + File.separator + "example.com" + File.separator + "file.txt";
        assertEquals(expected, handler.getFilePath(url, outputDir));
    }
}