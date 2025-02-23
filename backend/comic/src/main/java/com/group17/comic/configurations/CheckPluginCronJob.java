package com.group17.comic.configurations;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.group17.comic.enums.GlobalStorage;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.exporter.IFileExporter;
import com.group17.comic.utils.PluginUtility;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CheckPluginCronJob {

    @Value("${comic.base_dir}")
    String projectDirectory;

    @Value("${comic.plugin.crawler.crawler_package_name}")
    String crawlerPackageName;

    @Value("${comic.plugin.crawler.crawler_directory}")
    String crawlerDirectory;

    @Value("${comic.plugin.converter.converter_package_name}")
    String exporterPackageName;

    @Value("${comic.plugin.converter.converter_directory}")
    String exporterDirectory;

    private final GlobalStorage globalStorage;
    String baseDir = System.getProperty("user.dir");
    private int totalCrawlerPlugin = 0;
    private int totalExporterPlugin = 0;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void checkCrawlerPluginList() {
        try {
            baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
            Path converterAbsolutePath = Paths.get(baseDir, projectDirectory, crawlerDirectory);
            var crawlerClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                    converterAbsolutePath.toString(), crawlerPackageName, IDataCrawler.class);
            if (totalCrawlerPlugin == 0) {
                totalCrawlerPlugin = crawlerClasses.size();
            } else if (totalCrawlerPlugin != crawlerClasses.size()) {
                totalCrawlerPlugin = crawlerClasses.size();
                Notification notification = Notification.builder()
                        .setBody("Please refresh page to update new releases")
                        .setTitle("System has just updated")
                        .build();
                this.pushNotification("UPDATE_PLUGIN", notification);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void checkExporterPluginList() {
        try {
            baseDir = PluginUtility.resolveAbsolutePath(System.getProperty("user.dir"));
            Path converterAbsolutePath = Paths.get(baseDir, projectDirectory, exporterDirectory);
            var exporterClasses = PluginUtility.getAllPluginsFromFolderWithoutInstantiation(
                    converterAbsolutePath.toString(), exporterPackageName, IFileExporter.class);
            // System.out.println(LocalTime.now() + "  -->  total old/new exporter plugins: " + totalExporterPlugin +
            // "/"
            //         + exporterClasses.size());
            if (totalExporterPlugin == 0) {
                totalExporterPlugin = exporterClasses.size();
            } else if (totalExporterPlugin != exporterClasses.size()) {
                totalExporterPlugin = exporterClasses.size();
                Notification notification = Notification.builder()
                        .setBody("Please refresh page to update new releases")
                        .setTitle("System has just updated")
                        .build();
                this.pushNotification("UPDATE_PLUGIN", notification);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    void pushNotification(String type, Notification notification) {
        String clientToken = (String) globalStorage.get("clientToken");
        if (clientToken == null) {
            return;
        }
        Message msg = Message.builder()
                .setToken(clientToken)
                .setNotification(notification)
                .putData("type", type)
                .build();
        try {
            FirebaseMessaging.getInstance().send(msg);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
