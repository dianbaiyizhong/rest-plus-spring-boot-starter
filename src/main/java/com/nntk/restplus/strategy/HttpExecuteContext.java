package com.nntk.restplus.strategy;

import com.nntk.restplus.abs.AbsHttpFactory;

import java.io.File;
import java.util.Map;

public class HttpExecuteContext {

    private String url;
    private Map<String, String> headerMap;
    private Map<String, Object> bodyMap;
    private AbsHttpFactory httpFactory;


    public void setDownloadFilePath(File downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public File getDownloadFilePath() {
        return downloadFilePath;
    }

    private File downloadFilePath;

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    private boolean download = false;


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, Object> getBodyMap() {
        return bodyMap;
    }

    public void setBodyMap(Map<String, Object> bodyMap) {
        this.bodyMap = bodyMap;
    }

    public AbsHttpFactory getHttpFactory() {
        return httpFactory;
    }

    public void setHttpFactory(AbsHttpFactory httpFactory) {
        this.httpFactory = httpFactory;
    }


}
