package com.ucy.scheduleapp.Fragments.Database;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucy.scheduleapp.Activities.MainActivity;
import com.ucy.scheduleapp.Helper.TinyDB;
import com.ucy.scheduleapp.Model.User;
import com.ucy.scheduleapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment implements View.OnClickListener {


    private ImageView back;
    private Button signUp;
    private TextInputEditText name, nick, mail, pass, country, gsm, number;
    private String myName, myNick, myMail, myPass, myCountry, myGSM, myNumber, phone;
    private TinyDB tinyDB;
    private DatabaseReference database;
    private FirebaseAuth myAuth;
    private ArrayList<String> phoneList, nickList, mailList;

    public RegisterFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        typeCasting(view);
        connectDatabase();
        hideButtons();

        return view;
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void connectDatabase() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneList.clear();
                nickList.clear();
                mailList.clear();
                for (DataSnapshot info : dataSnapshot.getChildren()) {
                    User user = info.getValue(User.class);
                    phoneList.add(user.getPhone());
                    nickList.add(user.getNick());
                    mailList.add(user.getMail());
                    if (!phoneList.isEmpty() && !nickList.isEmpty()) {
                        ((MainActivity) getActivity()).setTinyList(nickList, mailList, phoneList);
                        tinyDB.putListString("phoneList", phoneList);
                        tinyDB.putListString("nickList", nickList);
                        tinyDB.putListString("mailList", mailList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void typeCasting(View view) {
        name = view.findViewById(R.id.edit_name);
        nick = view.findViewById(R.id.edit_nickname);
        pass = view.findViewById(R.id.edit_password);
        mail = view.findViewById(R.id.edit_mail);
        country = view.findViewById(R.id.edit_country);
        gsm = view.findViewById(R.id.edit_gsm);
        number = view.findViewById(R.id.edit_number);

        back = view.findViewById(R.id.button_back);
        signUp = view.findViewById(R.id.button_register);
        back.setOnClickListener(this);
        signUp.setOnClickListener(this);

        tinyDB = new TinyDB(getActivity().getApplicationContext());
        database = FirebaseDatabase.getInstance().getReference().child("users");
        myAuth = FirebaseAuth.getInstance();

        phoneList = new ArrayList<>();
        nickList = new ArrayList<>();
        mailList = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_back:
                ((MainActivity) getActivity()).goToFragment("login");
                break;
            case R.id.button_register:
                getTextAndNext();
                break;
        }
    }

    private void getTextAndNext() {
        myName = name.getText().toString().trim();
        myNick = nick.getText().toString().trim();
        myPass = pass.getText().toString().trim();
        myMail = mail.getText().toString().trim();
        myCountry = country.getText().toString().trim();
        myGSM = gsm.getText().toString().trim();
        myNumber = number.getText().toString().trim();
        phone = "" + myCountry + myGSM + myNumber;

        if (check(myName, myNick, myPass, myMail, myCountry, myGSM, myNumber, phone)) {

            myAuth.createUserWithEmailAndPassword(myMail, myPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(myNick, myPass, myMail, myName, phone, 0);
                                database.child(myNick).setValue(user);
                                Toast.makeText(getActivity(), "Kayıt başarılı!", Toast.LENGTH_SHORT)
                                        .show();
                                ((MainActivity) getActivity()).goToFragment("login");
                            } else
                                Toast.makeText(getActivity(), "Başarısız oldu!", Toast.LENGTH_SHORT)
                                        .show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Başarısız oldu!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean check(String myName, String myNick, String myPass, String myMail,
                          String myCountry, String myGSM, String myNumber, String phone) {

        boolean result = false;
        ArrayList<String> checkNick = tinyDB.getListString("nickList");
        ArrayList<String> checkPhone = tinyDB.getListString("phoneList");
        ArrayList<String> checkMail = tinyDB.getListString("mailList");

        if (TextUtils.isEmpty(myName) && TextUtils.isEmpty(myNick) && TextUtils.isEmpty(myPass) &&
                TextUtils.isEmpty(myMail) && TextUtils.isEmpty(myCountry) &&
                TextUtils.isEmpty(myGSM) && TextUtils.isEmpty(myNumber)) {
            Toast.makeText(getActivity(), "Eksik bilgi var!",
                    Toast.LENGTH_SHORT).show();
        } else if (myName.length() < 2) {
            Toast.makeText(getActivity(), "Geçersiz isim!", Toast.LENGTH_SHORT).show();
        } else if (myNick.length() < 3) {
            Toast.makeText(getActivity(), "Geçersiz kullanıcı adı!", Toast.LENGTH_SHORT).show();
        } else if (myPass.length() < 6) {
            Toast.makeText(getActivity(), "Geçersiz şifre!", Toast.LENGTH_SHORT).show();
        } else if (myMail.length() < 10 || !myMail.contains("@") || !myMail.contains(".com")) {
            Toast.makeText(getActivity(), "Geçersiz e-posta!", Toast.LENGTH_SHORT).show();
        } else if (myCountry.length() != 2) {
            Toast.makeText(getActivity(), "Geçersiz ülke kodu!", Toast.LENGTH_SHORT).show();
        } else if (myGSM.length() != 3) {
            Toast.makeText(getActivity(), "Geçersiz GSM numarası", Toast.LENGTH_SHORT).show();
        } else if (myNumber.length() != 7) {
            Toast.makeText(getActivity(), "Geçersiz telefon numarası!", Toast.LENGTH_SHORT).show();
        } else if (checkNick.contains(myNick)) {
            Toast.makeText(getActivity(), "Kullanıcı adı kullanımda!", Toast.LENGTH_SHORT)
                    .show();
        } else if (checkMail.contains(myMail)) {
            Toast.makeText(getActivity(), "E-posta zaten kayıtlı!", Toast.LENGTH_SHORT)
                    .show();
        } else if (checkPhone.contains(phone)) {
            Toast.makeText(getActivity(), "Telefon zaten kayıtlı!", Toast.LENGTH_SHORT)
                    .show();
        } else result = true;


        return result;
    }
}
