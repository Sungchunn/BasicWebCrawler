package io.muzoo.ssc.downloader;

import io.muzoo.ssc.assignment.tracker.SscAssignment;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpDownloader extends SscAssignment implements Downloader  {
    private final CloseableHttpClient httpClient;

    public HttpDownloader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String download(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveToFile(String content, String filePath) throws IOException {
        // Ensure the file is always saved in the Downloads directory
        Path downloadPath = Paths.get("/Users/chromatrical/Downloads", new File(filePath).getName());
//        Path path = Paths.get(filePath);
        Files.createDirectories(downloadPath.getParent());
        Files.write(downloadPath, content.getBytes());
    }
}