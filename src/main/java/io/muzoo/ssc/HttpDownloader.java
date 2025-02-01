package io.muzoo.ssc;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * The HttpDownloader class provides functionality to download content
 * from a specified URL and save it to a file. It uses Apache HttpClient
 * for making HTTP requests and handling responses.
 */
public class HttpDownloader  {
    private final CloseableHttpClient httpClient;

    /**
     * Constructor to initialize the HttpDownloader with a given HTTP client.
     *
     * @param httpClient the HTTP client used for making requests
     */
    public HttpDownloader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Downloads the content from the specified URL and returns it as a string.
     *
     * @param url the URL to download content from
     * @return the content of the URL as a string
     * @throws IOException if an error occurs during the HTTP request
     */
    public String download(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the given content to a specified file path.
     * Creates parent directories if they do not exist.
     *
     * @param content the content to save to the file
     * @param filePath the file path to save the content to
     * @throws IOException if an error occurs while writing to the file
     */
    public void saveToFile(String content, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes());
    }
}