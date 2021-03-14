package com.example.myshops.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.model.Type;
import com.example.myshops.model.User;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registerViewModel =
                ViewModelProviders.of(this).get(RegisterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        final TextView textView = root.findViewById(R.id.text);
        final EditText name = root.findViewById(R.id.name);
        final EditText surname = root.findViewById(R.id.surname);
        final EditText email = root.findViewById(R.id.email);
        final EditText password = root.findViewById(R.id.surname);
        final Button signup = root.findViewById(R.id.signin);
        final TextView signin = root.findViewById(R.id.change);
        auth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).replaceFragments(LoginFragment.class);
            }
        });
        registerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "There is a problem", Toast.LENGTH_LONG).show();
                            } else {
                                Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_LONG).show();
                                            databaseReference = FirebaseDatabase.getInstance().getReference("user").push();
                                            User user = new User();
                                            user.setId(databaseReference.getKey());
                                            user.setName(name.getText().toString());
                                            user.setSurname(surname.getText().toString());
                                            user.setEmail(email.getText().toString());
                                            user.setType(Type.USER);
                                            databaseReference.setValue(user);
                                            name.setText("");
                                            surname.setText("");
                                            email.setText("");
                                            password.setText("");
                                        } else {
                                            Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        return root;
    }

}