package com.example.asinit_user.gdziejestczoper.ui.search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


import com.example.asinit_user.gdziejestczoper.utils.Constants;

import java.util.Calendar;

import timber.log.Timber;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static String ACTIVITY = "DATE_PICKER_FRAGMENT";
    private SearchFragmentViewModelCallback searchFragmentViewModelCallback;
    private String date;

    public void setDatePickerCallback(SearchFragmentViewModelCallback searchFragmentViewModelCallback) {
        this.searchFragmentViewModelCallback = searchFragmentViewModelCallback;
    }

    public static DatePickerFragment newInstance(String date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        date = getArguments().getString("date");
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert, this, year, month, day);
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if (date.equals(Constants.START_DATE)) {
            c.set(year, month, day, 0, 0, 0);
            searchFragmentViewModelCallback.onStartDateSet(c.getTimeInMillis());
        } else if (date.equals(Constants.END_DATE)) {
            c.set(year, month, day, 23, 59, 59);
            searchFragmentViewModelCallback.onEndDateSet(c.getTimeInMillis());
        }

        Timber.d("year = " + year + ", month = " + month + ", day = " + day + ", milis = " + c.getTimeInMillis());
    }

    public void updateListener(SearchFragmentViewModel searchFragmentViewModel) {
        setDatePickerCallback(searchFragmentViewModel);
    }
}

