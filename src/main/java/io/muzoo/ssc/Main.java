package io.muzoo.ssc;


import io.muzoo.ssc.WebCrawler;
import io.muzoo.ssc.URLHandler;
import io.muzoo.ssc.assignment.tracker.SscAssignment;

import java.io.IOException;
import java.util.List;

public class Main extends SscAssignment {

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        URLHandler urlHandler = new URLHandler();
        String baseUrl = "https://cs.muic.mahidol.ac.th/courses/ssc/docs/";

        try {
            // Step 1: Fetch the base URL content
            String content = crawler.fetchURL(baseUrl);

            // Step 2: Parse links from the content
            List<String> links = urlHandler.extractLinks(content, baseUrl);

            // Step 3: Save the base page
            crawler.saveToFile(content, "output/docs/index.html");

            // Print links (for debugging)
            for (String link : links) {
                System.out.println(link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}