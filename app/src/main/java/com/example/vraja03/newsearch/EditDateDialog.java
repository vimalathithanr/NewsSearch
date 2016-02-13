package com.example.vraja03.newsearch;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by VRAJA03 on 2/12/2016.
 */
/*
public class EditDateDialog extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText etDatepicker;

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //etDatepicker = (EditText) findViewById(R.id.etDatepicker);
        etDatepicker.setOnEditorActionListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
            listener.onFinishEditDialog(etDatepicker.getText().toString());
            dismiss();
            return true;
        }
        return false;

        return false;
    }
}*/
