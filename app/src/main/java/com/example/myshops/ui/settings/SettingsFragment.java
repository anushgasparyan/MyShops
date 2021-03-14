package com.example.myshops.ui.settings;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.model.User;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    View root;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    String email;
    User user;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.settings_fragment, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading your data");
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        email = prefs.getString("email", "");

        EditText name = root.findViewById(R.id.name);
        EditText surname = root.findViewById(R.id.surname);
        EditText password = root.findViewById(R.id.password);
        EditText password2 = root.findViewById(R.id.password2);
        Button change = root.findViewById(R.id.change);
        Button change2 = root.findViewById(R.id.change2);

        if (email.isEmpty()) {
            builder = new AlertDialog.Builder(getContext());
            builder.setTitle("You are not logged in!")
                    .setMessage("Please log in to continue")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Go to login page", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) requireActivity()).replaceFragments(LoginFragment.class);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            BottomNavigationView navigationView = (BottomNavigationView) getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
            ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
        } else {
            progressDialog.show();
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("user");
            Query query2 = databaseReference2.orderByChild("email").equalTo(email);
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            user = child.getValue(User.class);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    name.setText(user.getName());
                    surname.setText(user.getSurname());
                    progressDialog.dismiss();
                }
            }, 1500);

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("user");
                    Query query1 = databaseReference1.orderByChild("email").equalTo(user.getEmail());
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Map<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("name", String.valueOf(name.getText()));
                                    hashMap.put("surname", String.valueOf(surname.getText()));
                                    child.getRef().updateChildren(hashMap);
                                    Toast.makeText(getContext(), "Data is updated!", Toast.LENGTH_LONG).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
            change2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), String.valueOf(password2.getText()));
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser.updatePassword(String.valueOf(password.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Password is updated!", Toast.LENGTH_LONG).show();
                                                    password.setText("");
                                                    password2.setText("");
                                                } else {
                                                    Toast.makeText(getContext(), "Password not updated!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });
        }
        return root;
    }
}