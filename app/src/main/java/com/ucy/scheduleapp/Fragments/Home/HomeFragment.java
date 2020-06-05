package com.ucy.scheduleapp.Fragments.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.R;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {

    private ImageView buttonMenu, buttonProfile, buttonPlan, buttonAddPlan, buttonExit;
    private LinearLayout profile, addPlan, myPlans;
    private Dialog dialog;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        typeCasting(view);
        hideButtons();
        return view;
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void typeCasting(View view) {
        buttonMenu = view.findViewById(R.id.button_home_menu);
        buttonProfile = view.findViewById(R.id.button_my_profile);
        buttonPlan = view.findViewById(R.id.button_my_plans);
        buttonAddPlan = view.findViewById(R.id.button_add_plan);
        buttonExit = view.findViewById(R.id.button_exit_home);

        profile = view.findViewById(R.id.layout_my_profile);
        addPlan = view.findViewById(R.id.layout_add_plan);
        myPlans = view.findViewById(R.id.layout_my_plans);

        profile.setOnClickListener(this);
        addPlan.setOnClickListener(this);
        myPlans.setOnClickListener(this);

        buttonMenu.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonPlan.setOnClickListener(this);
        buttonAddPlan.setOnClickListener(this);
        buttonExit.setOnClickListener(this);

        dialog = new Dialog(getContext());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_home_menu:
                PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.button_my_profile:
                ((MainActivity) getActivity()).goToFragment("profile");
                break;
            case R.id.button_my_plans:
                ((MainActivity) getActivity()).goToFragment("my_plans");
                break;
            case R.id.button_add_plan:
                ((MainActivity) getActivity()).goToFragment("plan");
                break;
            case R.id.button_exit_home:
                ((MainActivity) getActivity()).clearTinyData();
                ((MainActivity) getActivity()).goToFragment("login");
                break;
            case R.id.layout_my_profile:
                ((MainActivity) getActivity()).goToFragment("profile");
                break;
            case R.id.layout_add_plan:
                ((MainActivity) getActivity()).goToFragment("plan");
                break;
            case R.id.layout_my_plans:
                ((MainActivity) getActivity()).goToFragment("my_plans");
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_profile:
                ((MainActivity) getActivity()).goToFragment("profile");
                return true;
            case R.id.menu_plan:
                ((MainActivity) getActivity()).goToFragment("my_plans");
                return true;
            case R.id.menu_exit:
                ((MainActivity) getActivity()).clearTinyData();
                ((MainActivity) getActivity()).goToFragment("login");
                return true;
            default:
                return false;
        }

    }
}
