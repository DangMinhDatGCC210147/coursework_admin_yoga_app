package com.example.yoga_app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.yoga_app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private String dateFormat;
    private String courseDay;
    private String classDate;

    public DatePickerFragment(EditText editText, String dateFormat, String courseDay) {
        this.editText = editText;
        this.dateFormat = dateFormat;
        this.courseDay = courseDay;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.CustomDatePicker, this, year, month, day);

        datePickerDialog.setOnDateSetListener((view, year1, month1, day1) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, day1);
            String selectedDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate.getTime());

            if (!selectedDay.equalsIgnoreCase(courseDay)) {
                Toast.makeText(getActivity(), "Please select a valid day for the selected course!", Toast.LENGTH_SHORT).show();
                return;
            }

            editText.setText(new SimpleDateFormat(dateFormat, Locale.getDefault()).format(selectedDate.getTime()));
        });

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String selectedDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
        if (!selectedDay.equalsIgnoreCase(courseDay)) {
            Toast.makeText(getActivity(), "Please select a valid day for the selected course!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDate = sdf.format(calendar.getTime());
        editText.setText(formattedDate);
    }
}