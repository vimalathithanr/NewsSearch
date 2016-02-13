package com.example.vraja03.newsearch.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;

import com.example.vraja03.newsearch.Article;
import com.example.vraja03.newsearch.ArticleArrayAdapter;
import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.model.Preference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    Button btnSearch;
    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    private View formElementsView;
    private EditText etDatepicker;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

            /*LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            formElementsView = inflater.inflate(R.layout.setting_activity,
                    null, false);

            etDatepicker = (EditText) findViewById(R.id.etDatepicker);
            myCalendar = Calendar.getInstance();

            date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "MM/dd/yy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    etDatepicker.setText(sdf.format(myCalendar.getTime()));
                }

            };

            etDatepicker = (EditText) findViewById(R.id.etDatepicker);

            etDatepicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(SearchActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);

            new AlertDialog.Builder(SearchActivity.this).setView(formElementsView)
                    .setTitle("Set Preference")
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }

                    }).setNegativeButton("Cancel", null).show();
        }*/

            //return super.onOptionsItemSelected(item);
            Intent in = new Intent(this, SettingActivity.class);
            startActivity(in);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();

        Intent intent = getIntent();

        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "5c80b636d25bb07696584d982687673a:5:74377365");
        params.put("page", 0);
        params.put("q", query);
        params.put("begin_date", intent.getStringExtra("date"));
        params.put("sort", intent.getStringExtra("order"));

        String[] desk = intent.getStringArrayExtra("desk");

        List<String> list = new ArrayList<String>();
        for(String s : desk) {
            if(s != null && s.length() > 0) {
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
