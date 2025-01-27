package io.muzoo.ssc.tracker;

public interface ProgressTracker {
    void incrementTotalUrls();
    void incrementDownloadUrls();
    void printProgress();
}


