package com.group17.comic.plugins.crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.SneakyThrows;

public abstract class WebCrawler {
    protected static final String ALTERNATE_IMAGE = "https://truyen.tangthuvien.net/images/default-book.png";

    @SneakyThrows
    protected Document getDocumentInstanceFromUrl(String link) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
        try {
            con.connect();
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to " + link);
        }
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return Jsoup.parse(sb.toString());
    }
}
