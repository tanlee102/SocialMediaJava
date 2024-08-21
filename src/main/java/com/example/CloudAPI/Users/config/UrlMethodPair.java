package com.example.CloudAPI.Users.config;

import org.springframework.http.HttpMethod;

public class UrlMethodPair {
    private final String url;
    private final HttpMethod method;

    public UrlMethodPair(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
