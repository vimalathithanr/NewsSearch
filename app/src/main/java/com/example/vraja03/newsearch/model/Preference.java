package com.example.vraja03.newsearch.model;

import java.io.Serializable;

/**
 * Created by VRAJA03 on 2/13/2016.
 */
public class Preference implements Serializable {

    private static final long serialVersionUID = 5177222050535318633L;
    private String date;
    private String order;
    private String[] newsDesk;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String[] getNewsDesk() {
        return newsDesk;
    }

    public void setNewsDesk(String[] newsDesk) {
        this.newsDesk = newsDesk;
    }

}
