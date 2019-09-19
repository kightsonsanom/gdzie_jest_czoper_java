package pl.tolichwer.gdziejestczoper.ui.search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;


import pl.tolichwer.gdziejestczoper.utils.Constants;

import java.util.Calendar;

import timber.log.Timber;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private SearchFragmentViewModelCallback searchFragmentViewModelCallback;
    private String date;

    void setDatePickerCallback(SearchFragmentViewModelCallback searchFragmentViewModelCallback) {
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
        Calendar calendar = Calendar.getInstance();

        if (date.equals(Constants.START_DATE)) {
            calendar.set(year, month, day, 0, 0, 0);
            searchFragmentViewModelCallback.onStartDateSet(calendar.getTimeInMillis());
        } else if (date.equals(Constants.END_DATE)) {
            calendar.set(year, month, day, 23, 59, 59);
            searchFragmentViewModelCallback.onEndDateSet(calendar.getTimeInMillis());
        }
    }

    void updateListener(SearchFragmentViewModel searchFragmentViewModel) {
        setDatePickerCallback(searchFragmentViewModel);
    }
}

