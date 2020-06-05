package com.ucy.scheduleapp.Fragments.Database;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
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

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button login, forgot;
    private TextView signUp;
    private CheckBox remember;
    private TextInputEditText nick, pass;
    private TinyDB tinyDB;
    private FirebaseAuth myAuth;
    private DatabaseReference database;
    private String mail, mNick, mPass;
    private ArrayList<String> mailList;
    private ProgressDialog progressDialog;
    private Integer count;

    public LoginFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        typeCasting(view);
        getData();
        hideButtons();
        return (view);
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void getData() {
        String myNick = ((MainActivity) getActivity()).getTinyNick();  //tinyDB.getString("nick");
        String myPass = ((MainActivity) getActivity()).getTinyPass();  //tinyDB.getString("pass");
        if (check(myNick, myPass)) {
            nick.setText(myNick);
            pass.setText(myPass);
            progressDialog.setMessage("Yükleniyor...");
            progressDialog.show();
            getTextAndNext();

        }
    }

    private boolean check(String myNick, String myPass) {
        return !TextUtils.isEmpty(myNick) && !TextUtils.isEmpty(myPass);
    }

    private void typeCasting(View view) {

        remember = view.findViewById(R.id.checkbox_remember);
        nick = view.findViewById(R.id.edit_nick);
        pass = view.findViewById(R.id.edit_pass);
        signUp = view.findViewById(R.id.button_signup);
        login = view.findViewById(R.id.button_login);
        forgot = view.findViewById(R.id.button_forgot);

        signUp.setOnClickListener(this);
        login.setOnClickListener(this);
        forgot.setOnClickListener(this);

        tinyDB = new TinyDB(getActivity().getApplicationContext());
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");

        mailList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_login:
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                getTextAndNext();
                break;
            case R.id.button_signup:
                ((MainActivity) getActivity()).goToFragment("register");
                break;
            case R.id.button_forgot:
                ((MainActivity) getActivity()).goToFragment("forgot");
                break;
        }
    }

    private void getTextAndNext() {
        mNick = nick.getText().toString().trim();
        mPass = pass.getText().toString().trim();

        if (check(mNick, mPass)) {
            //users,nickname
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(mNick)) {

                        for (DataSnapshot info : dataSnapshot.getChildren()) {
                            User user = info.getValue(User.class);
                            //Kullanıcı adı ve şifre ile giriş tasarladığımdan dolayı
                            //veritabanından e-posta verisinin çekilmesine ihtiyaç doğuyor
                            if (info.getKey().equals(mNick)) {
                                mail = user.getMail().trim();
                                count = user.getCount();
                            }
                        }

                        if (mail != null) {
                            myAuth.signInWithEmailAndPassword(mail, mPass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                if (remember.isChecked()) {
                                                    ((MainActivity) getActivity())
                                                            .setTinyData(mNick, mPass, 2, count);
                                                } else {
                                                    ((MainActivity) getActivity())
                                                            .setTinyData(mNick, "", 1, count);
                                                }
                                                ((MainActivity) getActivity())
                                                        .goToFragment("home");
                                               /* ((MainActivity) getActivity())
                                                        .goToHomeActivity(
                                                                true,
                                                                "nick"
                                                                , mNick
                                                                , "mail"
                                                                , mail
                                                        );*/
                                                progressDialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Başarısız oldu!",
                                                        Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Başarısız oldu!"
                                            , Toast.LENGTH_SHORT)
                                            .show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Hata oluştu!", Toast.LENGTH_SHORT)
                                    .show();
                            progressDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Hata oluştu!", Toast.LENGTH_SHORT).show();
        }
    }


}
