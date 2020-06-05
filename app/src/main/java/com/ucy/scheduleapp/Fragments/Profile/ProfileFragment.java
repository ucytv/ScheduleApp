package com.ucy.scheduleapp.Fragments.Profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.Model.User;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView exit, back, avatar;
    private TextInputEditText name, nick, mail, pass, phone;
    private String mName, mNick, mMail, mPass, mPhone;
    private DatabaseReference database;
    private Button update, cancel, chooseAvatar;
    private TextInputLayout layout;
    private FirebaseAuth myAuth;
    private ArrayList<String> nickList, mailList, phoneList;
    private ArrayList<Integer> sourceList;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        typeCasting(view);
        setDisabled();
        getAndSetData();
        createLists();

        return view;
    }

    private void createLists() {
        nickList = new ArrayList<>();
        mailList = new ArrayList<>();
        phoneList = new ArrayList<>();
        sourceList = new ArrayList<>();

        nickList = ((MainActivity) getActivity()).getTinyNickList();
        mailList = ((MainActivity) getActivity()).getTinyMailList();
        phoneList = ((MainActivity) getActivity()).getTinyPhoneList();

        sourceList.add(R.drawable.woman);
        sourceList.add(R.drawable.woman_2);
        sourceList.add(R.drawable.man);
        sourceList.add(R.drawable.chef);
    }

    private void setDisabled() {
        name.setEnabled(false);
        nick.setEnabled(false);
        mail.setEnabled(false);
        pass.setEnabled(false);
        phone.setEnabled(false);
    }

    private void setEnabled() {
        name.setEnabled(true);
        mail.setEnabled(true);
        pass.setEnabled(true);
        phone.setEnabled(true);
    }

    private void getAndSetData() {
        final String mNick = ((MainActivity) getActivity()).getTinyNick();

        //Avatar gösterimi
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        int source = sharedPref.getInt("source", 0);
        if (source < 0) {
            avatar.setImageResource(R.drawable.person_icon_black_24);
        } else {
            avatar.setImageResource(source);
            avatar.setBackgroundColor(getResources().getColor(R.color.green));
        }

        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot info : dataSnapshot.getChildren()) {
                    if (mNick.equals(info.getKey())) {
                        User user = info.getValue(User.class);
                        name.setText(user.getName());
                        nick.setText(mNick);
                        mail.setText(user.getMail());
                        pass.setText(user.getPass());
                        phone.setText(user.getPhone());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void typeCasting(View view) {
        name = view.findViewById(R.id.show_name);
        nick = view.findViewById(R.id.show_nick);
        mail = view.findViewById(R.id.show_mail);
        pass = view.findViewById(R.id.show_pass);
        phone = view.findViewById(R.id.show_phone);
        avatar = view.findViewById(R.id.image_avatar);

        database = FirebaseDatabase.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();

        chooseAvatar = view.findViewById(R.id.button_choose_avatar);
        cancel = view.findViewById(R.id.button_cancel);
        update = view.findViewById(R.id.button_update);
        back = view.findViewById(R.id.button_back_profile);
        chooseAvatar.setOnClickListener(this);
        cancel.setOnClickListener(this);
        back.setOnClickListener(this);
        update.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_back_profile:
                ((MainActivity) getActivity()).goToFragment("home");
                break;
            case R.id.button_update:
                String buttonText = update.getText().toString();
                if (buttonText.equals("Güncelle")) {
                    setEnabled();
                    update.setText("Onay");
                    cancel.setVisibility(View.VISIBLE);
                } else if (buttonText.equals("Onay")) {
                    if (checkData()) {
                        setDisabled();
                        update.setText("Güncelle");
                        updateData();
                        cancel.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity(), "Hata!", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.button_cancel:
                cancel.setVisibility(View.GONE);
                update.setText("Güncelle");
                setDisabled();
                ((MainActivity) getActivity()).goToFragment("profile");
                break;

            case R.id.button_choose_avatar:
                ((MainActivity) getActivity()).goToFragment("avatar");
                break;
        }
    }

    private void updateData() {
        getInfo();
        Toast.makeText(getActivity(), "" + mName, Toast.LENGTH_SHORT).show();
        myAuth.getCurrentUser().updateEmail(mMail).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "E-posta güncellenemedi!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        myAuth.getCurrentUser().updatePassword(mPass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Şifre güncellenemedi!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        String currentNick = ((MainActivity) getActivity()).getTinyNick();
        int currentCount = ((MainActivity) getActivity()).getTinyCount();

        //Hashmap kullanmadım, çünkü aynı "map" üzerinde hem Integer hem String değişkeni
        //gönderemiyorum
        database.child("users").child(currentNick).child("mail").setValue(mMail);
        database.child("users").child(currentNick).child("name").setValue(mName);
        database.child("users").child(currentNick).child("nick").setValue(mNick);
        database.child("users").child(currentNick).child("pass").setValue(mPass);
        database.child("users").child(currentNick).child("phone").setValue(mPhone);
        database.child("users").child(currentNick).child("count").setValue(currentCount);


    }

    private void getInfo() {
        mName = name.getText().toString();
        mNick = nick.getText().toString();
        mMail = mail.getText().toString();
        mPass = pass.getText().toString();
        mPhone = phone.getText().toString();
    }

    private boolean checkData() {
        boolean result = false;

        getInfo();
        Toast.makeText(getActivity(), "" + mName, Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(mName) && TextUtils.isEmpty(mNick) && TextUtils.isEmpty(mPass) &&
                TextUtils.isEmpty(mMail) && TextUtils.isEmpty(mPhone)) {
            Toast.makeText(getActivity(), "Informations can not be missing!",
                    Toast.LENGTH_SHORT).show();
        } else if (mName.length() < 2) {
            Toast.makeText(getActivity(), "Invalid name!", Toast.LENGTH_SHORT).show();
        } else if (mNick.length() < 3) {
            Toast.makeText(getActivity(), "Invalid nick!", Toast.LENGTH_SHORT).show();
        } else if (mPass.length() < 6) {
            Toast.makeText(getActivity(), "Invalid nick!", Toast.LENGTH_SHORT).show();
        } else if (mMail.length() < 10 || !mMail.contains("@") || !mMail.contains(".com")) {
            Toast.makeText(getActivity(), "Invalid e-mail!", Toast.LENGTH_SHORT).show();
        } else if (mPhone.length() != 12) {
            Toast.makeText(getActivity(), "Invalid country code!", Toast.LENGTH_SHORT).show();
        } else if (mailList.contains(mMail)) {
            Toast.makeText(getActivity(), "E-Mail already registered!", Toast.LENGTH_SHORT)
                    .show();
        } else if (phoneList.contains(mPhone)) {
            Toast.makeText(getActivity(), "Phone already registered!", Toast.LENGTH_SHORT)
                    .show();
        } else if (nickList.contains(mNick)) {
            Toast.makeText(getActivity(), "Username already registered!", Toast.LENGTH_SHORT)
                    .show();
        } else result = true;

        return result;
    }
}
