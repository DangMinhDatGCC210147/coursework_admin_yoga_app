package com.example.yoga_app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerClassesFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private String dateFormat;

    public DatePickerClassesFragment(EditText editText, String dateFormat) {
        this.editText = editText;
        this.dateFormat = dateFormat;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String formattedDate = sdf.format(selectedCalendar.getTime());
        editText.setText(formattedDate);
    }
}
