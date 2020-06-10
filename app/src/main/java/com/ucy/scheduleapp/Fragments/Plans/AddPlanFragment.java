package com.ucy.scheduleapp.Fragments.Plans;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.Model.Plan;
import com.ucy.scheduleapp.R;

import java.util.Calendar;

import androidx.fragment.app.Fragment;


public class AddPlanFragment extends Fragment implements View.OnClickListener {

    private TextView textDate, textTime;
    private Button buttonDate, buttonTime, buttonConfirm;
    private ImageView buttonBack;
    private TextInputEditText title, description;
    private String hourCheck = null, minutCheck = null;
    private String mMonth = null, mDay = null, mYear = null;
    private DatabaseReference database;

    public AddPlanFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_plan, container, false);

        typeCasting(view);
        hideButtons();
        return view;
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void typeCasting(View view) {
        textDate = view.findViewById(R.id.text_date);
        textTime = view.findViewById(R.id.text_time);

        title = view.findViewById(R.id.edit_title);
        description = view.findViewById(R.id.edit_content);

        buttonBack = view.findViewById(R.id.button_back_add_plan);
        buttonDate = view.findViewById(R.id.button_date);
        buttonTime = view.findViewById(R.id.button_time);
        buttonConfirm = view.findViewById(R.id.button_confirm_plan);

        buttonBack.setOnClickListener(this);
        buttonDate.setOnClickListener(this);
        buttonTime.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();


    }

    private void getDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int years, int month, int dayOfMonth) {
                        month = month + 1;
                        calendar.set(years, month, dayOfMonth);

                        mMonth = String.valueOf(month);
                        mDay = String.valueOf(dayOfMonth);
                        mYear = String.valueOf(years);


                        //Zaman farkı alırken SimpleDateFormat olarak dd-MM-yyyy HH:mm:ss
                        //kullandığımdan dolayı tek haneli ay ve saatleri dönüştürmem gerekiyor
                        if (month < 10) {
                            mMonth = String.valueOf(month);
                            mMonth = "0" + mMonth;
                        }
                        if (dayOfMonth < 10) {
                            mDay = String.valueOf(dayOfMonth);
                            mDay = "0" + mDay;
                        }

                        String text = "" + mDay + "/" + mMonth + "/" + mYear;
                        textDate.setText(text);

                    }

                }, day, month, year);


        dialog.getDatePicker().setMinDate(System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dialog);
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dialog);
        dialog.show();

    }

    private void getTime() {

        final Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        hourCheck = String.valueOf(hourOfDay);
                        minutCheck = String.valueOf(minute);

                        if (hourOfDay < 10) {
                            hourCheck = String.valueOf(hourOfDay);
                            hourCheck = "0" + hourCheck;
                        }
                        if (minute < 10) {
                            minutCheck = String.valueOf(minute);
                            minutCheck = "0" + minutCheck;
                        }

                        String text = "" + hourCheck + ":" + minutCheck;
                        textTime.setText(text);

                    }
                }, hours, minutes, true);


        timePicker.setButton(TimePickerDialog.BUTTON_POSITIVE, "Seç", timePicker);
        timePicker.setButton(TimePickerDialog.BUTTON_NEGATIVE, "İptal", timePicker);
        timePicker.show();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_date:
                getDate();
                break;
            case R.id.button_time:
                getTime();
                break;
            case R.id.button_confirm_plan:
                getTextAndNext();
                break;
            case R.id.button_back_add_plan:
                ((MainActivity) getActivity()).goToFragment("home");
                break;
        }
    }

    private void getTextAndNext() {
        String header = title.getText().toString();
        String content = description.getText().toString();
        if (check(header, content, textDate, textTime)) {
            String firstDate = mDay + "-" + mMonth + "-" + mYear;

            if (checkTime(firstDate, hourCheck, minutCheck)) {
                String firstTime = hourCheck + ":" + minutCheck;
                String date = firstDate + " " + firstTime + ":00";

                int firstCount = ((MainActivity) getActivity()).getTinyCount() + 1;
                String count = String.valueOf(firstCount);

                int day = Integer.parseInt(mDay);
                int month = Integer.parseInt(mMonth);
                int year = Integer.parseInt(mYear);
                int hour = Integer.parseInt(hourCheck);
                int minute = Integer.parseInt(minutCheck);

                String nick = ((MainActivity) getActivity()).getTinyNick();

                Plan plan = new Plan(header, content, date, firstDate, firstTime
                        , day, month, year, hour, minute, firstCount, false);

                database.child("plans").child(nick).child(firstDate).child(count).setValue(plan);
                database.child("users").child(nick).child("count").setValue(firstCount);


                String code = getCalendar(firstDate, day, month);

                ((MainActivity) getActivity()).goToFragment(code);
                ((MainActivity) getActivity()).setHeaderText(code);
            } else {
                Toast.makeText(getActivity(), "Geçmiş zamana plan yapamazsınız!"
                        , Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Eksik bilgi var!", Toast.LENGTH_SHORT).show();
        }

    }

    private String getCalendar(String firstDate, int day, int month) {
        final Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int dayDifference = day - currentDay;
        int monthDifference = month - currentMonth;

        String date = currentDay + "-" + currentMonth + "-" + currentYear;
        if (date.equals(firstDate)) {
            return "daily";
        } else {
            if (monthDifference == 0) {
                if (dayDifference >= 0 && dayDifference <= 7) {
                    return "weekly";
                } else {
                    return "monthly";
                }
            } else if (monthDifference == 1) {
                int newDay = day + 30;
                if (newDay - currentDay <= 7) {
                    return "weekly";
                } else return "monthly";
            } else return "monthly";
        }

    }

    private boolean checkTime(String firstDate, String hour, String minute) {

        //Geçmiş zamana plan yapmayı engellemek amaçlı
        final Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int myHour = Integer.parseInt(hour);
        int myMinute = Integer.parseInt(minute);

        String lastDay = "", lastMonth = "";
        if (currentDay < 10) {
            lastDay = "0" + String.valueOf(currentDay);
        }
        if (currentMonth < 10) {
            lastMonth = "0" + String.valueOf(currentMonth);
        }
        String date = lastDay + "-" + lastMonth + "-" + currentYear;

        if (date.equals(firstDate)) {
            if (myHour < currentHour) {
                return false;
            } else if (myHour == currentHour) {
                return myMinute >= currentMinute;
            } else return true;
        } else return true;

    }

    private boolean check(String header, String content,
                          TextView textDate, TextView textTime) {

        String date = textDate.getText().toString();
        String time = textTime.getText().toString();

        return !TextUtils.isEmpty(header) && !TextUtils.isEmpty(content)
                && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(time);

    }
}
