package io.muzoo.ssc;

/**
 * The ProgressTracker class is responsible for tracking and displaying
 * the progress of a web crawler. It keeps track of the total number of URLs,
 * the number of downloaded URLs, and the current URL being processed.
 */
public class ProgressTracker  {
    protected int totalUrls = 0;
    protected int downloadedUrls = 0;
    protected String currentUrl = "";

    public void setTotalUrls(int total) {
        this.totalUrls = total;
    }

    public void incrementDownloadedUrls(String url) {
        downloadedUrls++;
        currentUrl = url;
        printProgress();
    }

    /**
     * Prints the progress of the web crawler in terms of percentage and the count of downloaded URLs.
     * If the total number of URLs is not set, a message indicating this is displayed instead.
     */
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