package com.example.vraja03.newsearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.model.Preference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by VRAJA03 on 2/12/2016.
 */
public class SettingActivity extends AppCompatActivity {

    EditText etDatepicker;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private String[] arraySpinner;
    Spinner spinner;
    private String myFormat = "MM/dd/yy";
    String[] newsDesk = new String[3];
    CheckBox chkArts;
    CheckBox chkFashion;
    CheckBox chkSports;
    Button btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        setPreference();
        addSpinner();
        chkArts = (CheckBox) findViewById(R.id.chkArts);
        chkFashion = (CheckBox) findViewById(R.id.chkFashion);
        chkSports = (CheckBox) findViewById(R.id.chkSports);
        etDatepicker = (EditText) findViewById(R.id.etDatepicker);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnSet = (Button) findViewById(R.id.btnSet);
    }

    public void setPreference() {
        etDatepicker = (EditText) findViewById(R.id.etDatepicker);
        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                etDatepicker.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etDatepicker = (EditText) findViewById(R.id.etDatepicker);

        etDatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SettingActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void addSpinner() {
        this.arraySpinner = new String[]{
                "Oldest", "Newest"
        };
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);
    }


    public void onPreferenceset(View view) {

        int i = 0;

        if (chkArts.isChecked()) {
            newsDesk[i++] = "Arts";
        }
        if (chkFashion.isChecked())
            newsDesk[i++] = "Fashion and Style";

        if (chkSports.isChecked())
            newsDesk[i++] = "Sports";


        Preference preference = new Preference();
        if (etDatepicker.getText().toString() != null)
            preference.setDate(etDatepicker.getText().toString());

        if (spinner.getSelectedItem().toString() != null)
            preference.setOrder(spinner.getSelectedItem().toString());

        if (i > 0)
            preference.setNewsDesk(newsDesk);

        Intent in = new Intent(this, SearchActivity.class);
        in.putExtra("preference", preference);
        startActivity(in);
    }
}
