package com.example.myshops.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.myshops.ui.forget.ForgetFragment;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.register.RegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final TextView textView = root.findViewById(R.id.text);
        final EditText login = root.findViewById(R.id.login);
        final EditText password = root.findViewById(R.id.password);
        final Button signin = root.findViewById(R.id.change);
        final Button signup = root.findViewById(R.id.signin);
        final TextView forget = root.findViewById(R.id.forgot);
        auth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).replaceFragments(RegisterFragment.class);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    auth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("CommitPrefEdits")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Wrong email or password", Toast.LENGTH_LONG).show();
                            } else {
                                if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
                                    SharedPreferences.Editor preferences = getActivity().getSharedPreferences("MYPREF", Context.MODE_PRIVATE).edit();
                                    preferences.putString("email", auth.getCurrentUser().getEmail()).apply();
                                    ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
                                } else {
                                    Toast.makeText(getContext(), "Not verified", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }

            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).replaceFragments(ForgetFragment.class);
            }
        });
        loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}