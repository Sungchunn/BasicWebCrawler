package io.muzoo.ssc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProgressTrackerTest {

    @Test
    public void testSetTotalUrls() {
        ProgressTracker tracker = new ProgressTracker();
        tracker.setTotalUrls(100);
        assertEquals(100, tracker.totalUrls);
    }

    @Test
    public void testIncrementDownloadedUrls() {
        ProgressTracker tracker = new ProgressTracker();
        tracker.setTotalUrls(10);
        tracker.incrementDownloadedUrls("http://example.com/1");
        tracker.incrementDownloadedUrls("http://example.com/2");
        assertEquals(2, tracker.downloadedUrls);
        assertEquals("http://example.com/2", tracker.currentUrl);
    }

    @Test
    public void testPrintProgressWithTotalUrlsSet() {
        ProgressTracker tracker = new ProgressTracker();
        tracker.setTotalUrls(5);
        tracker.incrementDownloadedUrls("http://example.com/1");
        tracker.incrementDownloadedUrls("http://example.com/2");
        tracker.printProgress();
    }

    @Test
    public void testPrintProgressWithoutTotalUrlsSet() {
        ProgressTracker tracker = new ProgressTracker();
        tracker.printProgress();
    }
}