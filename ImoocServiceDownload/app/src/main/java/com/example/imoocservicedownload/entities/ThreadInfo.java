package com.example.imoocservicedownload.entities;

/**
 * Created by archermind on 11/29/19.
 * Wzj
 * content
 * 线程信息
 */
public class ThreadInfo {
    private int id;
    private String url;
    private int start;
    private int ends;
    private int finished;

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", ends=" + ends +
                ", finished=" + finished +
                '}';
    }

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String url, int start, int ends, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.ends = ends;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnds() {
        return ends;
    }

    public void setEnds(int ends) {
        this.ends = ends;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}
