package io.muzoo.ssc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class URLHandler {

    public List<String> extractLinks(String html, String baseUrl) {
        Document doc = Jsoup.parse(html, baseUrl);
        Elements links = doc.select("a[href]");
        List<String> urls = new ArrayList<>();

        for (Element link : links) {
            String absUrl = link.attr("abs:href");
            urls.add(absUrl);
        }

        return urls;
    }
}