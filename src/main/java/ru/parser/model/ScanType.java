package ru.parser.model;

public enum ScanType {
    FULL(""),
    ONLY_CHINESE("cnt=cn");

    private final String urlFilter;

    ScanType(String urlFilter) {
        this.urlFilter = urlFilter;
    }

    public String getUrlFilter() {
        return urlFilter;
    }
}
