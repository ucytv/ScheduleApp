package com.ucy.scheduleapp.Fragments.Plans;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ucy.scheduleapp.Model.Plan;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyFragment extends Fragment implements View.OnClickListener {

    private String nick, childDate, childCount;
    private DatabaseReference database;
    private PlanAdapter adapter;
    private RecyclerView recyclerView;
    private List<Plan> infoList;
    private List<Integer> weekList;
    private LinearLayout layoutWeekly;
    private Button buttonAddPlan;

    public WeeklyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        typeCasting(view);
        showButtons();
        initRecycler();


        return view;
    }

    private void typeCasting(View view) {
        nick = ((MainActivity) getActivity()).getTinyNick();
        layoutWeekly = view.findViewById(R.id.layout_null_weekly);
        buttonAddPlan = view.findViewById(R.id.button_null_add_plan_weekly);
        buttonAddPlan.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_plan_weekly);

    }

    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectDatabase();
    }

    private void connectDatabase() {
        final Calendar calendar = Calendar.getInstance();
        final int currentMonth = calendar.get(Calendar.MONTH) + 1;

        infoList = new ArrayList<>();
        weekList = new ArrayList<>();

        database.child("plans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoList.clear();
                weekList.clear();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(nick)) {
                        for (DataSnapshot date : user.getChildren()) {
                            for (DataSnapshot count : date.getChildren()) {
                                Plan plan = count.getValue(Plan.class);
                                if (plan.getCount() > 0) {
                                    if (weekControl(plan.getMonth()
                                            , plan.getDay()
                                            , plan.getYear())) {

                                        infoList.add(plan);
                                    }
                                }
                            }
                        }
                    }
                }

                if (infoList.isEmpty()) {
                    layoutWeekly.setVisibility(View.VISIBLE);
                } else {
                    layoutWeekly.setVisibility(View.GONE);
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

    private boolean weekControl(Integer month, Integer day, Integer year) {
        final Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int dayDifference = day - currentDay;
        int monthDifference = month - currentMonth;
        int yearDifference = year - currentYear;


        //Planı haftalık kapsamda değerlendirmek için
        if (yearDifference != 0) {
            return false;
        } else {
            if (monthDifference > 1) {
                return false;
            } else if (monthDifference == 0) {
                return dayDifference >= 0 && dayDifference <= 7;
            } else if (monthDifference == 1) {
                if (dayDifference > 0) {
                    return false;
                } else {
                    int newDay = day + 30;
                    return newDay - currentDay <= 7;
                }
            } else {
                return false;
            }
        }

    }

    private void showButtons() {
        ((MainActivity) getActivity()).showButtons();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_null_add_plan_weekly:
                ((MainActivity) getActivity()).goToFragment("plan");
                break;
        }
    }


}