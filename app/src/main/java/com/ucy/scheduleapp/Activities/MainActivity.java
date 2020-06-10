package com.ucy.scheduleapp.Activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucy.scheduleapp.Fragments.Database.ForgotFragment;
import com.ucy.scheduleapp.Fragments.Database.LoginFragment;
import com.ucy.scheduleapp.Fragments.Database.RegisterFragment;
import com.ucy.scheduleapp.Fragments.Home.HomeFragment;
import com.ucy.scheduleapp.Fragments.Plans.AddPlanFragment;
import com.ucy.scheduleapp.Fragments.Plans.DailyFragment;
import com.ucy.scheduleapp.Fragments.Plans.MonthlyFragment;
import com.ucy.scheduleapp.Fragments.Plans.WeeklyFragment;
import com.ucy.scheduleapp.Fragments.Profile.AvatarFragment;
import com.ucy.scheduleapp.Fragments.Profile.ProfileFragment;
import com.ucy.scheduleapp.Helper.TinyDB;
import com.ucy.scheduleapp.R;
import com.ucy.scheduleapp.Receiver.NotificationReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TinyDB tinyDB;
    private FirebaseAuth myAuth;
    private NotificationCompat.Builder builder;
    private DatabaseReference database;
    private Button buttonDaily, buttonWeekly, buttonMonthly;
    private LinearLayout layoutButton;
    private ImageView buttonBack;
    private TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        typeCasting();
        //Açılış için fragment seçimi
        goToFragment("login");

        getSupportActionBar().hide();
        setHeaderText("daily");
    }

    private void typeCasting() {
        //SharedPreferences tabanlı bir sınıf, kullanımı kolay
        //Veri taşırken tercih ediyorum
        tinyDB = new TinyDB(this);

        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        layoutButton = findViewById(R.id.layout_buttons);
        buttonBack = findViewById(R.id.button_back_my_plans);
        buttonDaily = findViewById(R.id.button_daily);
        buttonWeekly = findViewById(R.id.button_weekly);
        buttonMonthly = findViewById(R.id.button_monthly);

        buttonBack.setOnClickListener(this);
        buttonDaily.setOnClickListener(this);
        buttonWeekly.setOnClickListener(this);
        buttonMonthly.setOnClickListener(this);

        header = findViewById(R.id.text_plan_header);
    }

    public String initCalendar() {
        final Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        String mDay = String.valueOf(currentDay);
        String mMonth = String.valueOf(currentMonth);

        if (currentMonth < 10) {
            mMonth = String.valueOf(currentMonth);
            mMonth = "0" + mMonth;
        }
        if (currentDay < 10) {
            mDay = String.valueOf(currentDay);
            mDay = "0" + mDay;
        }

        String mYear = String.valueOf(currentYear);
        String currentDate = "" + mDay + "-" + mMonth + "-" + mYear;

        return currentDate;
    }

    public void goToFragment(String key) {
        Fragment fragment;
        if (key != null) {
            switch (key) {
                case "login":
                    fragment = new LoginFragment();
                    break;
                case "register":
                    fragment = new RegisterFragment();
                    break;
                case "forgot":
                    fragment = new ForgotFragment();
                    break;
                case "avatar":
                    fragment = new AvatarFragment();
                    break;
                case "profile":
                    fragment = new ProfileFragment();
                    break;
                case "home":
                    fragment = new HomeFragment();
                    break;
                case "plan":
                    fragment = new AddPlanFragment();
                    break;
                case "my_plans":
                    fragment = new DailyFragment();
                    break;
                case "daily":
                    fragment = new DailyFragment();
                    break;
                case "weekly":
                    fragment = new WeeklyFragment();
                    break;
                case "monthly":
                    fragment = new MonthlyFragment();
                    break;
                default:
                    fragment = new LoginFragment();
                    break;
            }
            allOfFragment(fragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void allOfFragment(Fragment fragment) {
        if (fragment != null) {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void pushNotification(long difference, boolean notifyStatus, String date, String count) {
        ////users,nick,date,count
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent goToIntent = PendingIntent.getActivity(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "channel_id";
            String channelName = "channel_name";
            String channelInfo = "channel_info";
            int channelPriority = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, channelPriority);
                channel.setDescription(channelInfo);
                notificationManager.createNotificationChannel(channel);

            }

            builder = new NotificationCompat.Builder(this, channelId);
            builder.setContentTitle("Plan Bildirimi");
            builder.setContentText("Planınız yaklaşıyor!");
            builder.setSmallIcon(R.drawable.wizard);
            builder.setAutoCancel(true);
            builder.setContentIntent(goToIntent);
            builder.setPriority(channelPriority);

        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Plan Bildirimi");
            builder.setContentText("Planınız yaklaşıyor!");
            builder.setSmallIcon(R.drawable.wizard);
            builder.setAutoCancel(true);
            builder.setContentIntent(goToIntent);
        }

        Intent broadcastIntent = new Intent(
                MainActivity.this,
                NotificationReceiver.class);
        broadcastIntent.putExtra("builder", builder.build());

        PendingIntent goToBroadcast = PendingIntent.getBroadcast(
                this,
                0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        long statTwoHours = 2 * 60 * 60 * 1000;
        long statFourHours = 4 * 60 * 60 * 1000;
        long statEightHours = 8 * 60 * 60 * 1000;
        long stateTenHours = 10 * 60 * 60 * 1000;
        long stateTwelveHours = 12  * 60 * 60 * 1000;
        long stateTwenty = 20 * 60 * 60 * 1000;

        //X saat kala bildirim göndermek için
        if (difference > stateTwenty) {
            sendNotify(difference, stateTwenty, goToBroadcast);
        } else if (difference > stateTwelveHours) {
            sendNotify(difference, stateTwelveHours, goToBroadcast);
        } else if (difference > stateTenHours) {
            sendNotify(difference, stateTenHours, goToBroadcast);
        } else if (difference > statEightHours) {
            sendNotify(difference, statEightHours, goToBroadcast);
        } else if (difference > statFourHours) {
            sendNotify(difference, statFourHours, goToBroadcast);
        } else if (difference > statTwoHours) {
            if (notifyStatus) {
                connectDatabase(date, count, notifyStatus);
                sendNotify(difference, statTwoHours, goToBroadcast);
            }
        }
    }

    private void sendNotify(long difference, long statHours, PendingIntent goToBroadcast) {
        long delay;

        delay = difference - statHours;
        delay = SystemClock.elapsedRealtime() + delay;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, goToBroadcast);
    }

    public void connectDatabase(String date, String count, Boolean status) {

        //Bildirim gönderim kontrolü için
        database
                .child("plans")
                .child(getTinyNick())
                .child(date)
                .child(count)
                .child("notify")
                .setValue(status);
    }

    public void setTinyData(String nick, String pass, int code, int count) {
        if (code == 1) {
            tinyDB.putString("nick", nick);
        } else {
            tinyDB.putString("nick", nick);
            tinyDB.putString("pass", pass);
        }
        tinyDB.putInt("count", count);

    }

    public void setTinyList(ArrayList<String> nickList, ArrayList<String> mailList
            , ArrayList<String> phoneList) {
        tinyDB.putListString("nickList", nickList);
        tinyDB.putListString("mailList", mailList);
        tinyDB.putListString("phoneList", phoneList);
    }

    public ArrayList<String> getTinyNickList() {
        return tinyDB.getListString("nickList");
    }

    public ArrayList<String> getTinyMailList() {
        return tinyDB.getListString("mailList");
    }

    public ArrayList<String> getTinyPhoneList() {
        return tinyDB.getListString("phoneList");
    }

    public String getTinyNick() {
        return tinyDB.getString("nick");
    }

    public String getTinyPass() {
        return tinyDB.getString("pass");
    }

    public Integer getTinyCount() {
        return tinyDB.getInt("count");
    }

    public void clearTinyData() {
        tinyDB.clear();
        myAuth.signOut();
    }

    public void showButtons() {
        layoutButton.setVisibility(View.VISIBLE);
    }

    public void hideButtons() {
        layoutButton.setVisibility(View.GONE);
    }

    public void setHeaderText(String key){
        if(key.equals("daily")){
            header.setText("Günlük Planlarım");
        }else if(key.equals("weekly")){
            header.setText("Haftalık Planlarım");
        }else {
            header.setText("Aylık Planlarım");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_back_my_plans:
                goToFragment("home");
                break;
            case R.id.button_daily:
                buttonDaily.setBackgroundResource(R.drawable.button_style_4);
                buttonWeekly.setBackgroundResource(R.drawable.button_style_3);
                buttonMonthly.setBackgroundResource(R.drawable.button_style_3);
                setHeaderText("daily");
                goToFragment("daily");
                break;
            case R.id.button_weekly:
                buttonWeekly.setBackgroundResource(R.drawable.button_style_4);
                buttonMonthly.setBackgroundResource(R.drawable.button_style_3);
                buttonDaily.setBackgroundResource(R.drawable.button_style_3);
                setHeaderText("weekly");
                goToFragment("weekly");
                break;
            case R.id.button_monthly:
                buttonMonthly.setBackgroundResource(R.drawable.button_style_4);
                buttonWeekly.setBackgroundResource(R.drawable.button_style_3);
                buttonDaily.setBackgroundResource(R.drawable.button_style_3);
                setHeaderText("monthly");
                goToFragment("monthly");
                break;

        }
    }
}
