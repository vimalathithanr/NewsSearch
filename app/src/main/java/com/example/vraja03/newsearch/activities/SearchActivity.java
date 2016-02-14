package com.example.vraja03.newsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.adapter.ArticleAdapter;
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

    RecyclerView rvResults;
    ArrayList<Article> articles;
    ArticleAdapter adapter;
    String query = null;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(articles);
        rvResults = (RecyclerView) findViewById(R.id.rvResults);

        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(adapter);
        // Set layout manager to position the items
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(0);

        rvResults.setLayoutManager(gridLayoutManager);

        rvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public int getLastVisibleItem(int[] lastVisibleItemPositions) {
                int maxSize = 0;
                for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                    if (i == 0) {
                        maxSize = lastVisibleItemPositions[i];
                    } else if (lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i];
                    }
                }
                return maxSize;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int[] lastVisibleItemPositions = gridLayoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
                int totalItemCount = gridLayoutManager.getItemCount();

                if (totalItemCount > 0 && totalItemCount - lastVisibleItemPosition < 10) {
                    page++;
                    customLoadMoreDataFromApi(page);
                }
            }
        });
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
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
                JSONArray articleJsonResults;
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
