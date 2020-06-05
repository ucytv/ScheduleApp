package com.ucy.scheduleapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ucy.scheduleapp.Common.Common;
import com.ucy.scheduleapp.Model.Plan;
import com.ucy.scheduleapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.DailyHolder> {

    private Context context;
    private List<Plan> planList;
    private LocalBroadcastManager broadcast;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
    private Long elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds;
    private Long difference;
    private OnItemClickListener itemClickListener;
    public static String goDate, goCount;
    public static Integer goColor;

    public PlanAdapter(Context context, List<Plan> planList) {
        this.context = context;
        this.planList = planList;

        broadcast = LocalBroadcastManager.getInstance(context);
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position, String date, String count);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public DailyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_common, parent, false);

        return new DailyHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyHolder holder, int position) {
        Plan plan = planList.get(position);

        goDate = plan.getNow();
        goCount = String.valueOf(plan.getCount());

        String remain = "";
        try {
            String myDate = plan.getDate();
            Date endDate = simpleDateFormat.parse(myDate);
            Date now = new Date();
            String start = simpleDateFormat.format(now);
            Date startDate = simpleDateFormat.parse(start);
            //Date date1 = simpleDateFormat.parse("14-05-2020 19:17:00");
            remain = getRemainTime(startDate, endDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (elapsedDays > 0 || elapsedHours > 0 || elapsedMinutes > 0 || elapsedSeconds > 0) {
            holder.fab.setBackgroundTintList(
                    ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
            holder.remainTime.setText(remain);
            Common.colorCode = R.color.green;
        } else {
            holder.fab.setBackgroundTintList(
                    ColorStateList.valueOf(context.getResources().getColor(R.color.Red)));
            holder.remainTime.setText(remain);
            Common.colorCode = R.color.red;
        }



        holder.header.setText(plan.getTitle());
        holder.title.setText(plan.getTitle());
        holder.content.setText(plan.getDescription());
        holder.date.setText(plan.getNow());
        holder.time.setText(plan.getTime());
        //holder.remainTime.setText(remain);

        Intent intent = new Intent(Common.REMAIN_INFO);
        long stat = 2 * 60 * 60 * 1000;

        if (difference >= stat) {
            intent.putExtra(Common.KEY_MILLISECONDS_NAME, difference);
        }

        broadcast.sendBroadcast(intent);
    }

    private String getRemainTime(Date startDate, Date endDate) {

        difference = endDate.getTime() - startDate.getTime();

        long seconds = 1000;
        long minutes = seconds * 60;
        long hours = minutes * 60;
        long days = hours * 24;

        elapsedDays = difference / days;
        difference = difference % days;

        elapsedHours = difference / hours;
        difference = difference % hours;

        elapsedMinutes = difference / minutes;
        difference = difference % minutes;

        elapsedSeconds = difference / seconds;
        difference = endDate.getTime() - startDate.getTime();
        //difference = endDate.getTime() - startDate.getTime();
        Log.d("difference","Day: "+elapsedDays+" Hour: "+elapsedHours+" Minute: "+elapsedMinutes);

        String remainInfo;
        if (elapsedDays <= 0) {
            if (elapsedHours <= 0) {
                if (elapsedMinutes <= 0) {
                    remainInfo = "Planınız geçti!";
                } else {
                    remainInfo = elapsedMinutes + " dk";
                }
            } else {
                if (elapsedMinutes <= 0) {
                    remainInfo = elapsedHours + " saat ";
                } else {
                    remainInfo = elapsedHours + " saat " + elapsedMinutes + " dk";
                }
            }
        } else {
            if (elapsedHours <= 0) {
                if (elapsedMinutes <= 0) {
                    remainInfo = elapsedDays + " gün kaldı";
                } else {
                    remainInfo = elapsedDays + " gün " + elapsedMinutes + " dk";
                }
            } else {
                if (elapsedMinutes <= 0) {
                    remainInfo = elapsedDays + " gün " + elapsedHours + " saat";
                } else {
                    remainInfo = elapsedDays + " gün " + elapsedHours + " saat"
                            + elapsedMinutes + " dk";
                }
            }
        }

        return remainInfo;
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class DailyHolder extends RecyclerView.ViewHolder implements CompoundButton
            .OnCheckedChangeListener {

        TextView title, content, date, time, remainTime, header;
        CardView cardPlan;

        ImageView delete;
        LinearLayout linearLayout;
        Switch mySwitch;
        FloatingActionButton fab;

        public DailyHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);
            typeCasting(itemView);
            initVisibilityGone();

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position,goDate,goCount);
                        }
                    }
                }
            });
        }

        private void typeCasting(View itemView) {
            header = itemView.findViewById(R.id.text_header_common);
            title = itemView.findViewById(R.id.text_title_common);
            content = itemView.findViewById(R.id.text_content_common);
            date = itemView.findViewById(R.id.text_date_common);
            time = itemView.findViewById(R.id.text_time_common);
            remainTime = itemView.findViewById(R.id.text_remain_time_common);

            cardPlan = itemView.findViewById(R.id.card_plan_common);
            delete = itemView.findViewById(R.id.button_delete_common);

            mySwitch = itemView.findViewById(R.id.switch_show_common);
            mySwitch.setOnCheckedChangeListener(this);

            fab = itemView.findViewById(R.id.fab_common);

            linearLayout = itemView.findViewById(R.id.layout_show_common);

        }


        private void initVisibilityGone() {
            linearLayout.setVisibility(View.GONE);
        }

        private void initVisibilityVisible() {
            linearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                initVisibilityVisible();
                mySwitch.setText("Gizle");
            } else {
                initVisibilityGone();
                mySwitch.setText("Göster");
            }
        }


    }
}
