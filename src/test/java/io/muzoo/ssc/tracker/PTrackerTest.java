package io.muzoo.ssc.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PTrackerTest {

    private PTracker pTracker;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        pTracker = new PTracker();
        System.setOut(new PrintStream(outputStreamCaptor)); // Capture console output
    }

    @Test
    void testSetTotalUrls() {
        pTracker.setTotalUrls(10);
        assertEquals(10, getTotalUrlsFromTracker(), "Total URLs should be set correctly.");
    }

    @Test
    void testIncrementDownloadedUrls() {
        pTracker.setTotalUrls(5);
        pTracker.incrementDownloadedUrls("http://example.com/page1");
        pTracker.incrementDownloadedUrls("http://example.com/page2");

        assertEquals(2, getDownloadedUrlsFromTracker(), "Downloaded URLs should increment correctly.");
    }

    @Test
    void testPrintProgress() {
        pTracker.setTotalUrls(5);
        pTracker.incrementDownloadedUrls("http://example.com/page1");

        String consoleOutput = outputStreamCaptor.toString().trim();
        assertTrue(consoleOutput.contains("20.00% (1/5 URLs are downloaded) - http://example.com/page1"),
                "Progress message should be printed correctly.");
    }

    @Test
    void testPrintProgressWithoutTotalUrls() {
        pTracker.incrementDownloadedUrls("http://example.com/page1");

        String consoleOutput = outputStreamCaptor.toString().trim();
        assertTrue(consoleOutput.contains("Total URLs not set yet."),
                "Should display message when total URLs are not set.");
    }

    // Helper methods to access private fields (Java Reflection)
    private int getTotalUrlsFromTracker() {
        try {
            var field = PTracker.class.getDeclaredField("totalUrls");
            field.setAccessible(true);
            return field.getInt(pTracker);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getDownloadedUrlsFromTracker() {
        try {
            var field = PTracker.class.getDeclaredField("downloadedUrls");
            field.setAccessible(true);
            return field.getInt(pTracker);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}