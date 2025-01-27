package io.muzoo.ssc.tracker;

public class PTracker implements ProgressTracker {
    private int totalUrls = 0;
    private int downloadedUrls = 0;
    private String currentUrl = "";

    @Override
    public void incrementTotalUrls() {
        totalUrls++;
    }

    @Override
    public void incrementDownloadUrls() {
        downloadedUrls++;
        printProgress();
    }

    @Override
    public void printProgress() {
        if (totalUrls == 0) {
            System.out.println("No URLs to download yet.");
            return;
        }
        // Calculate progress percentage
        double progressPercentage = (double) downloadedUrls / totalUrls * 100;

        // Print the formatted progress
        System.out.printf("%.2f%% (%d/%d URLs are downloaded) - %s%n",
                progressPercentage, downloadedUrls, totalUrls, currentUrl);
    }
}