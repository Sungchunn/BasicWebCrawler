package io.muzoo.ssc;

import io.muzoo.ssc.crawler.WebCrawler;
import io.muzoo.ssc.downloader.HttpDownloader;
import io.muzoo.ssc.tracker.PTracker;
import io.muzoo.ssc.utils.BasicURLHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class Main {

    public static void main(String[] args) {
        String baseUrl = "https://cs.muic.mahidol.ac.th/courses/ssc/docs/";
        String outputDir = "output";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        WebCrawler crawler = new WebCrawler(
                baseUrl,
                outputDir,
                new HttpDownloader(httpClient),
                new BasicURLHandler(),
                new PTracker()
        );

        try {
            crawler.startCrawling();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}