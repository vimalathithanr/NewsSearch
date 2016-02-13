package com.example.vraja03.newsearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.adapter.ArticleArrayAdapter;
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

    EditText etQuery;
    Button btnSearch;
    GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Config.context = this;
        if(!NetworkConnection.checkInternetConn(Config.context))
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        setSupportActionBar(toolbar);
        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onArticleSearch(View view) {

        NetworkConnection connection = new NetworkConnection();

        articles.clear();
        String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        Intent intent = getIntent();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "5c80b636d25bb07696584d982687673a:5:74377365");
        params.put("page", 0);
        params.put("q", query);

        Preference preference = (Preference) getIntent().getSerializableExtra("preference");

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
