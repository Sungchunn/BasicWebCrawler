package io.muzoo.ssc.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class BasicURLHandlerTest {

    private BasicURLHandler urlHandler;

    @BeforeEach
    void setUp() {
        urlHandler = new BasicURLHandler();
    }

    @Test
    void testCleanUrl_ValidUrl() {
        String result = urlHandler.cleanUrl("https://example.com/page");
        assertEquals("https://example.com/page", result, "Valid URL should remain unchanged.");
    }

    @Test
    void testCleanUrl_WithQueryParameters() {
        String result = urlHandler.cleanUrl("https://example.com/page?query=value");
        assertEquals("https://example.com/page", result, "Query parameters should be removed.");
    }

    @Test
    void testCleanUrl_WithFragment() {
        String result = urlHandler.cleanUrl("https://example.com/page#section");
        assertEquals("https://example.com/page", result, "Fragment identifiers should be removed.");
    }

    @Test
    void testCleanUrl_InvalidUrl() {
        String result = urlHandler.cleanUrl("invalid-url");
        assertEquals("", result, "Invalid URL should return an empty string.");
    }

    @Test
    void testGetFilePath_BasicUrl() {
        String outputDir = "output";
        String result = urlHandler.getFilePath("https://example.com/page", outputDir);
        assertEquals("output" + File.separator + "example.com" + File.separator + "page", result, "File path should be correctly formatted.");
    }

    @Test
    void testGetFilePath_WithTrailingSlash() {
        String outputDir = "output";
        String result = urlHandler.getFilePath("https://example.com/folder/", outputDir);
        assertEquals("output" + File.separator + "example.com" + File.separator + "folder" + File.separator + "index.html",
                result, "Trailing slashes should resolve to 'index.html'.");
    }

    @Test
    void testGetFilePath_InvalidUrl() {
        String outputDir = "output";
        String result = urlHandler.getFilePath("invalid-url", outputDir);
        assertEquals("", result, "Invalid URLs should return an empty file path.");
    }

    @Test
    void testGetFilePath_OutputDirWithoutTrailingSlash() {
        String outputDir = "outputDir";
        String result = urlHandler.getFilePath("https://example.com/page", outputDir);
        assertEquals("outputDir" + File.separator + "example.com" + File.separator + "page",
                result, "Output directory should handle missing trailing slashes.");
    }

    @Test
    void testGetFilePath_OutputDirWithTrailingSlash() {
        String outputDir = "outputDir" + File.separator;
        String result = urlHandler.getFilePath("https://example.com/page", outputDir);
        assertEquals("outputDir" + File.separator + "example.com" + File.separator + "page",
                result, "Output directory should correctly handle trailing slashes.");
    }
}