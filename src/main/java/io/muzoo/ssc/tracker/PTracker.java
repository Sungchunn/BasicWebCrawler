package io.muzoo.ssc.tracker;

public class PTracker {
    private int totalUrls = 0;
    private int downloadedUrls = 0;
    private String currentUrl = "";

    public void setTotalUrls(int total) {
        this.totalUrls = total;
    }

    public void incrementDownloadedUrls(String url) {
        downloadedUrls++;
        currentUrl = url;
        printProgress();
    }

    public void printProgress() {
        if (totalUrls == 0) {
            System.out.println("Total URLs not set yet.");
            return;
        }
        double progressPercentage = (double) downloadedUrls / totalUrls * 100;
        System.out.printf("%.2f%% (%d/%d URLs are downloaded) - %s%n",
                progressPercentage, downloadedUrls, totalUrls, currentUrl);
    }
}