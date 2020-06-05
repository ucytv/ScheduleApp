package com.ucy.scheduleapp.Fragments.Database;

import android.app.ProgressDialog;
import android.os.Bundle;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotFragment extends Fragment implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private FirebaseAuth myAuth;
    private DatabaseReference database;
    private TinyDB tinyDB;
    private ImageView back;
    private Button confirm;
    private TextInputEditText mail;
    private ArrayList<String> mailList;

    public ForgotFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);

        typeCasting(view);
        getData();
        hideButtons();

        return view;
    }

    private void hideButtons() {
        ((MainActivity) getActivity()).hideButtons();
    }

    private void getData() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mailList.clear();
                for (DataSnapshot info : dataSnapshot.getChildren()) {
                    User user = info.getValue(User.class);
                    mailList.add(user.getMail());
                }

                tinyDB.putListString("mail", mailList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void typeCasting(View view) {
        back = view.findViewById(R.id.button_back_forgot);
        confirm = view.findViewById(R.id.button_confirm_forgot);
        mail = view.findViewById(R.id.edit_mail_forgot);

        back.setOnClickListener(this);
        confirm.setOnClickListener(this);

        tinyDB = new TinyDB(getActivity().getApplicationContext());
        database = FirebaseDatabase.getInstance().getReference().child("users");
        myAuth = FirebaseAuth.getInstance();

        mailList = new ArrayList<>();

    }

    private void getMail() {
        String eMail = mail.getText().toString().trim();
        ArrayList<String> checkList = tinyDB.getListString("mail");

        if (checkList.contains(eMail)) confirmRequest(eMail);
        else Toast.makeText(getActivity(), "E-Posta bulunamadı!", Toast.LENGTH_SHORT)
                .show();
    }

    private void confirmRequest(String mail) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("E-Posta gönderiliyor...");
        progressDialog.show();

        myAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "E-Posta gönderildi!",
                            Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).goToFragment("login");
                } else Toast.makeText(getContext(), "Başarısız oldu!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Başarısız oldu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_back_forgot:
                ((MainActivity) getActivity()).goToFragment("login");
                break;
            case R.id.button_confirm_forgot:
                getMail();
                break;
        }
    }
}
