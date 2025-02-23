package com.group17.comic.enums;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "app.config")
public final class AuthConfigProperties {
    private final Firebase firebase = new Firebase();
    private final Cors cors = new Cors();

    @Getter
    @Setter
    public static final class Firebase {
        private Authentication authentication;
        private Bucket bucket;

        @Getter
        @Setter
        public static final class Authentication {
            private String projectId;
            private String projectName;
            private String appName;
            private String apiKey;
            private String authenticationUrl;
            private String googleCredentials;
        }

        @Getter
        @Setter
        public static final class Bucket {
            private String name;
            private String bucketFileurl;
        }
    }

    @Setter
    @Getter
    public static final class Cors {
        private String pathPattern;
        private String allowedOrigins;
        private boolean allowCredentials;
        private String[] allowedMethods;
        private String[] allowedHeaders;
        private String[] allowedPublicApis;
        private String exposedHeaders;
        private int maxAge;
    }
}
