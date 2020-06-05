package com.ucy.scheduleapp.Fragments.Profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.Helper.TinyDB;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class AvatarFragment extends Fragment implements View.OnClickListener {
    private CardView firstMan, secondMan, firstWoman, secondWoman;
    private ImageView buttonBack, checkOne, checkTwo, checkThree, checkFour;
    private Button buttonConfirm;
    private TinyDB tinyDB;

    private List<ImageView> checkList;
    private ArrayList<Integer> sourceList;
    private Boolean cardClick;
    private Integer sourceKey;

    public AvatarFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_avatar, container, false);

        typeCasting(view);
        clickZone();
        hideButtons();
        return view;
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void typeCasting(View view) {
        firstWoman = view.findViewById(R.id.card_woman_1);
        secondWoman = view.findViewById(R.id.card_woman_2);
        firstMan = view.findViewById(R.id.card_man_1);
        secondMan = view.findViewById(R.id.card_man_2);

        checkOne = view.findViewById(R.id.image_check_woman_1);
        checkTwo = view.findViewById(R.id.image_check_woman_2);
        checkThree = view.findViewById(R.id.image_check_man_1);
        checkFour = view.findViewById(R.id.image_check_man_2);

        buttonBack = view.findViewById(R.id.button_back_avatar);
        buttonConfirm = view.findViewById(R.id.button_confirm_avatar);

        createLists();

    }

    private void clickZone() {
        buttonConfirm.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        firstMan.setOnClickListener(this);
        secondMan.setOnClickListener(this);
        firstWoman.setOnClickListener(this);
        secondWoman.setOnClickListener(this);
    }

    private void createLists() {
        checkList = new ArrayList<>();
        checkList.add(checkOne);
        checkList.add(checkTwo);
        checkList.add(checkThree);
        checkList.add(checkFour);

        sourceList = new ArrayList<>();
        sourceList.add(R.drawable.woman);
        sourceList.add(R.drawable.woman_2);
        sourceList.add(R.drawable.man);
        sourceList.add(R.drawable.chef);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.card_woman_1:
                setVisible(0);
                break;
            case R.id.card_woman_2:
                setVisible(1);
                break;
            case R.id.card_man_1:
                setVisible(2);
                break;
            case R.id.card_man_2:
                setVisible(3);
                break;
            case R.id.button_confirm_avatar:
                Log.wtf("card", "Card: " + cardClick);
                if (cardClick) {
                    //Avatarı hafızada tutmak için
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(
                            "sharedPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("source", sourceKey);
                    editor.commit();

                }
                ((MainActivity) getActivity()).goToFragment("profile");
                break;
            case R.id.button_back_avatar:
                ((MainActivity) getActivity()).goToFragment("profile");
                break;
        }
    }

    private void setVisible(int position) {
        //Yeşil tik görünürlüğü
        ImageView imageView;
        for (int i = 0; i < 4; i++) {
            imageView = checkList.get(i);

            if (i == position) {
                imageView.setVisibility(View.VISIBLE);
                cardClick = true;
                sourceKey = sourceList.get(i);

            } else {
                imageView.setVisibility(View.GONE);

            }
        }

    }
}
