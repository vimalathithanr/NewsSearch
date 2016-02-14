package com.example.vraja03.newsearch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by VRAJA03 on 2/11/2016.
 */
public class Article {
    String webUrl;
    String headline;
    String thumbNail;
    int height;
    int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public Article(JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length()>0){
                JSONObject multimediaJson = multimedia.getJSONObject(new Random().nextInt(multimedia.length()));
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
                height = multimediaJson.getInt("height");
                width = multimediaJson.getInt("width");
            } else{
                this.thumbNail = "";
            }



        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<Article> fromJsonArray(JSONArray array){
        ArrayList<Article> results = new ArrayList<Article>();

        for(int i=0; i<array.length();i++){
            try{
                results.add(new Article(array.getJSONObject(i)));


            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        return results;

    }

}
