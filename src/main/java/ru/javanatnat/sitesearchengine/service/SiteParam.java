package ru.javanatnat.sitesearchengine.service;

public class SiteParam {
    private String url;
    private String name;

    public SiteParam() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SiteParam{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
