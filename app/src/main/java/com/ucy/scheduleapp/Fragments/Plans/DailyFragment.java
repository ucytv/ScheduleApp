package com.ucy.scheduleapp.Fragments.Plans;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.Adapters.PlanAdapter;
import com.ucy.scheduleapp.Common.Common;
import com.ucy.scheduleapp.Model.Plan;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DailyFragment extends Fragment implements View.OnClickListener {

    private String nick, childDate, childCount;
    private Boolean status;
    private Long diff;
    private int currentDay, currentMonth, currentYear;
    private String currentDate;
    private DatabaseReference database;
    private PlanAdapter adapter;
    private RecyclerView recyclerView;
    private List<Plan> infoList;
    private List<String> dayList;
    private LinearLayout layoutDaily;
    private Button buttonAddPlan;

    private LocalBroadcastManager broadcast;
    private BroadcastReceiver millisecondsReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                diff = intent.getLongExtra(Common.KEY_MILLISECONDS_NAME, 0);
                if (diff > 0) {
                    toNotification(diff);
                }
            }
        }
    };

    public DailyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        typeCasting(view);
        showButtons();
        initBroadcast();
        initRecycler();
        return view;
    }

    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectDatabase();
    }

    private void toNotification(final Long timeDifference) {
        //plans,nickname,date,count
        database.child("plans").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    if (users.getKey().equals(nick)) {
                        for (DataSnapshot date : users.getChildren()) {
                            for (DataSnapshot count : date.getChildren()) {
                                Plan plan = count.getValue(Plan.class);
                                childDate = plan.getNow();
                                childCount = count.getKey();
                                status = plan.getNotify();
                            }
                        }
                    }
                }
                if (!status) {
                    ((MainActivity) getActivity()).pushNotification(timeDifference,
                            !status, childDate, childCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void connectDatabase() {
        final String dateInfo = ((MainActivity) getActivity()).initCalendar();

        infoList = new ArrayList<>();
        dayList = new ArrayList<>();

        database.child("plans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoList.clear();
                dayList.clear();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(nick)) {
                        for (DataSnapshot date : user.getChildren()) {
                            for (DataSnapshot count : date.getChildren()) {
                                Plan plan = count.getValue(Plan.class);
                                if (plan.getCount() > 0 && date.getKey().equals(dateInfo)) {
                                    infoList.add(plan);
                                }
                            }
                        }
                    }
                }

                if (infoList.isEmpty()) {
                    layoutDaily.setVisibility(View.VISIBLE);
                } else {
                    layoutDaily.setVisibility(View.GONE);
                    adapter = new PlanAdapter(getActivity(), infoList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new PlanAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(int position, final String date, final String count) {
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("Uyarı");
                            dialog.setMessage("Planı silmek istediğinize emin misiniz?");

                            dialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    database.child("plans").child(nick).child(date).child(count).removeValue();
                                    dialogInterface.dismiss();
                                }
                            });

                            dialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            dialog.show();

                        }

                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initBroadcast() {
        broadcast = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        broadcast.registerReceiver(millisecondsReciever, new IntentFilter(Common.REMAIN_INFO));
    }

    @Override
    public void onDestroy() {
        broadcast.unregisterReceiver(millisecondsReciever);
        super.onDestroy();

    }

    private void typeCasting(View view) {
        nick = ((MainActivity) getActivity()).getTinyNick();
        layoutDaily = view.findViewById(R.id.layout_null_daily);
        buttonAddPlan = view.findViewById(R.id.button_null_add_plan_daily);
        buttonAddPlan.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_plan_daily);

    }

    private void showButtons() {
        ((MainActivity) getActivity()).showButtons();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_null_add_plan_daily:
                ((MainActivity) getActivity()).goToFragment("plan");
                break;
        }
    }
}