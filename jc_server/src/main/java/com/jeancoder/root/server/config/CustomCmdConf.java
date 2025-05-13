package com.jeancoder.root.server.config;

public class CustomCmdConf {

    private static final CustomCmdConf instance = new CustomCmdConf();

    private CustomCmdConf() {}

    public static CustomCmdConf getInstance() {
        return instance;
    }

    private String manageDomain;

    private String fetchDomain;

    public String getManageDomain() {
        return manageDomain;
    }

    public void setManageDomain(String manageDomain) {
        this.manageDomain = manageDomain;
    }

    public String getFetchDomain() {
        return fetchDomain;
    }

    public void setFetchDomain(String fetchDomain) {
        this.fetchDomain = fetchDomain;
    }
}
