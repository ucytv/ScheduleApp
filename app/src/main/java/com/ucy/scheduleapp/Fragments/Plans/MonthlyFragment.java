package com.ucy.scheduleapp.Fragments.Plans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import com.ucy.scheduleapp.Model.Plan;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment implements View.OnClickListener {
    private String nick, childDate, childCount;
    private DatabaseReference database;
    private PlanAdapter adapter;
    private RecyclerView recyclerView;
    private List<Plan> infoList;
    private List<Integer> monthList;
    private LinearLayout layoutMonthly;
    private Button buttonAddPlan;

    public MonthlyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);

        typeCasting(view);
        showButtons();
        initRecycler();
        return view;
    }

    private void typeCasting(View view) {
        nick = ((MainActivity) getActivity()).getTinyNick();
        layoutMonthly = view.findViewById(R.id.layout_null_monthly);
        buttonAddPlan = view.findViewById(R.id.button_null_add_plan_monthly);
        buttonAddPlan.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_plan_monthly);

    }

    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectDatabase();
    }

    private void connectDatabase() {
        final Calendar calendar = Calendar.getInstance();
        final int currentMonth = calendar.get(Calendar.MONTH) + 1;
        Log.d("month", "Month:" + currentMonth);

        infoList = new ArrayList<>();
        monthList = new ArrayList<>();

        //plans,nickname,date,count
        database.child("plans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoList.clear();
                monthList.clear();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(nick)) {
                        for (DataSnapshot date : user.getChildren()) {
                            for (DataSnapshot count : date.getChildren()) {
                                Plan plan = count.getValue(Plan.class);
                                if (plan.getCount() > 0) {
                                    if (currentMonth == plan.getMonth()) {
                                        if (monthControl(
                                                currentMonth
                                                , plan.getMonth()
                                                , plan.getDay()
                                                , plan.getYear())) {

                                            infoList.add(plan);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (infoList.isEmpty()) {
                    layoutMonthly.setVisibility(View.VISIBLE);
                } else {
                    layoutMonthly.setVisibility(View.GONE);
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

    private boolean monthControl(int currentMonth, int dataMonth, int dataDay, int dataYear) {
        //Planı aylık kapsamda değerlendirmek için
        final Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int yearDifference = dataYear - currentYear;
        int monthDifference = dataMonth - currentMonth;
        int dayDifference = dataDay - currentDay;

        if (yearDifference != 0) {
            return false;
        } else {
            if (monthDifference == 1) {
                return dayDifference < 1;
            } else return monthDifference == 0;
        }
    }

    private void showButtons() {
        ((MainActivity) getActivity()).showButtons();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_null_add_plan_monthly:
                ((MainActivity) getActivity()).goToFragment("plan");
                break;
        }
    }
}