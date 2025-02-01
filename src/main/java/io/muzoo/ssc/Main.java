package io.muzoo.ssc;

import io.muzoo.ssc.assignment.tracker.SscAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends SscAssignment {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String baseUrl = "https://cs.muic.mahidol.ac.th/courses/ssc/docs/";
        String outputDir = "output";

        CrawlerManager crawlerManager = new CrawlerManager(baseUrl, outputDir);
        crawlerManager.runCrawler();
    }
}