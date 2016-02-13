package com.example.vraja03.newsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.adapter.ArticleArrayAdapter;
import com.example.vraja03.newsearch.helper.EndlessScrollListener;
import com.example.vraja03.newsearch.model.Article;
import com.example.vraja03.newsearch.model.Preference;
import com.example.vraja03.newsearch.util.Config;
import com.example.vraja03.newsearch.util.NetworkConnection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    //EditText etQuery;
    //Button btnSearch;
    GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    String query = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    public void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                in.putExtra("url", article.getWebUrl());
                startActivity(in);
            }
        });
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        //query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "5c80b636d25bb07696584d982687673a:5:74377365");
        params.put("page", offset);
        params.put("q", query);

        Preference preference = (Preference) getIntent().getSerializableExtra("preference");

        try {
            String prefDate = preference.getDate();
            String prefOrder = preference.getOrder();
            String[] desk = preference.getNewsDesk();


            if (!prefDate.equals(null) && !prefDate.isEmpty())
                params.put("begin_date", prefDate);

            if (!prefOrder.equals(null) && !prefDate.isEmpty())
                params.put("sort", prefOrder);

            if (desk != null) {
                List<String> list = new ArrayList<String>();
                for (String s : desk) {
                    if (s != null && s.length() > 0) {
                        list.add(s);
                    }
                }
                desk = list.toArray(new String[list.size()]);

                if (desk.length > 0) {
                    String deskFormat = Arrays.toString(desk);
                    String deskFormatPre = deskFormat.replace("[", "(\"");
                    String deskFormatpost = deskFormatPre.replace("]", "\")");
                    String deskFormatfinal = deskFormatpost.replace(",", "\",\"");
                    params.put("fq", deskFormatfinal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                onArticleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(this, SettingActivity.class);
            startActivity(in);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onArticleSearch(String inQuery) {

        Config.context = this;
        if (!NetworkConnection.checkInternetConn(Config.context))
            Toast.makeText(SearchActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();

        articles.clear();
        query = inQuery;

        if (query == null || query.isEmpty())
            Toast.makeText(SearchActivity.this, "Please enter a search query!", Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "5c80b636d25bb07696584d982687673a:5:74377365");
        params.put("page", 0);
        params.put("q", query);

        Preference preference = (Preference) getIntent().getSerializableExtra("preference");

        try {
            String prefDate = preference.getDate();
            String prefOrder = preference.getOrder();
            String[] desk = preference.getNewsDesk();


            if (!prefDate.equals(null) && !prefDate.isEmpty())
                params.put("begin_date", prefDate);

            if (!prefOrder.equals(null) && !prefDate.isEmpty())
                params.put("sort", prefOrder);

            if (desk != null) {
                List<String> list = new ArrayList<String>();
                for (String s : desk) {
                    if (s != null && s.length() > 0) {
                        list.add(s);
                    }
                }
                desk = list.toArray(new String[list.size()]);

                if (desk.length > 0) {
                    String deskFormat = Arrays.toString(desk);
                    String deskFormatPre = deskFormat.replace("[", "(\"");
                    String deskFormatpost = deskFormatPre.replace("]", "\")");
                    String deskFormatfinal = deskFormatpost.replace(",", "\",\"");
                    params.put("fq", deskFormatfinal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
