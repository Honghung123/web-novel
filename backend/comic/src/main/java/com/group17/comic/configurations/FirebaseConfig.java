package com.group17.comic.configurations;

import java.io.File;
import java.io.FileInputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.group17.comic.enums.AuthConfigProperties;
import com.group17.comic.utils.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The FirebaseConfig class is responsible for configuring and initializing
 * Firebase services within the application.
 * - It sets up the Firebase App using authentication details from the configuration properties,
 * ensures the Firebase Messaging service is available, and provides
 * access to the Google Cloud Storage Bucket.
 * - Additionally, it manages IAM policies for the storage bucket to allow public access to its
 * contents, facilitating seamless interaction with Firebase and Google
 * Cloud services.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FirebaseConfig {
    private final AuthConfigProperties configProperties;

    @Bean
    GoogleCredentials googleCredentials() throws Exception {
        var authentication = configProperties.getFirebase().getAuthentication();
        File googleCredentials = FileUtils.loadFile(authentication.getGoogleCredentials());
        return GoogleCredentials.fromStream(new FileInputStream(googleCredentials));
    }

    private FirebaseOptions createFirebaseOptions(GoogleCredentials googleCredentials) {
        var authentication = configProperties.getFirebase().getAuthentication();
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setProjectId(authentication.getProjectId())
                // .setStorageBucket(configProperties.getFirebase().getBucket().getName())
                .build();
        return firebaseOptions;
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials googleCredentials) throws Exception {
        var firebase = configProperties.getFirebase();
        try {
            return FirebaseApp.getApps().stream()
                    .filter(app ->
                            app.getName().equals(firebase.getAuthentication().getAppName()))
                    .findFirst()
                    .get();
        } catch (Exception e) {
            log.info(
                    "Firebase app named {} does not exist. Creating new one.",
                    firebase.getAuthentication().getAppName());
        }
        return FirebaseApp.initializeApp(
                this.createFirebaseOptions(googleCredentials),
                firebase.getAuthentication().getAppName());
    }

    @Bean
    @Primary
    FirebaseApp defaultFirebaseApp(GoogleCredentials googleCredentials) throws Exception {
        try {
            log.info("Try to delete default firebase app if it exists.");
            FirebaseApp defaultApp = FirebaseApp.getApps().stream()
                    .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                    .findFirst()
                    .get();
            defaultApp.delete();
            log.info("Default firebase app has been deleted.");
        } catch (Exception e) {
            log.info("Firebase app named {} does not exist. Creating new one.", FirebaseApp.DEFAULT_APP_NAME);
        }
        return FirebaseApp.initializeApp(this.createFirebaseOptions(googleCredentials));
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    // @Bean
    // Bucket bucket(FirebaseApp firebaseApp) throws IOException {
    //     // return StorageClient.getInstance().bucket("Your bucket name");
    //     Bucket defaultBucket = StorageClient.getInstance().bucket(); // Default is <fisebase project>.appspot.com
    //     // initialize(defaultBucket.getName());
    //     return defaultBucket;
    // }

    // void initialize(String bucketName) throws IOException {
    //     var authentication = configProperties.getFirebase().getAuthentication();
    //     Storage storage = StorageOptions.newBuilder()
    //             .setProjectId(authentication.getProjectId())
    //             .build()
    //             .getService();
    //     Policy originalPolicy = storage.getIamPolicy(bucketName);
    //     storage.setIamPolicy(
    //             bucketName,
    //             originalPolicy.toBuilder()
    //                     .addIdentity(StorageRoles.objectViewer(), Identity.allUsers()) // All users can view
    //                     .build());
    // }
}
