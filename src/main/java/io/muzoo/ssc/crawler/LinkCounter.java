package io.muzoo.ssc.crawler;

import io.muzoo.ssc.downloader.Downloader;
import io.muzoo.ssc.utils.UrlHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class LinkCounter {
    private static String baseUrl;
    private static Downloader downloader;
    private static UrlHandler urlHandler;

    public LinkCounter(String baseUrl, Downloader downloader, UrlHandler urlHandler) {
        this.baseUrl = baseUrl;
        this.downloader = downloader;
        this.urlHandler = urlHandler;
    }

    /**
     * Counts the total number of links from a given URL using BFS for efficiency.
     */
    public static int countTotalLinks(String url) throws IOException {
        Set<String> allLinks = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(url);
        allLinks.add(url);

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            String content;

            try {
                content = downloader.download(currentUrl);
            } catch (IOException e) {
                continue; // Skip if unable to download
            }

            Document doc = Jsoup.parse(content, currentUrl);
            Elements links = doc.select("a[href], img[src], link[href], script[src], iframe[src]");

            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (nextUrl.isEmpty()) {
                    nextUrl = link.absUrl("src");
                }
                nextUrl = urlHandler.cleanUrl(nextUrl);

                if (!allLinks.contains(nextUrl) && nextUrl.startsWith(baseUrl)) {
                    allLinks.add(nextUrl);
                    queue.add(nextUrl);
                }
            }
        }
        return allLinks.size();
    }
}
