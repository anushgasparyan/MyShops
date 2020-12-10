package com.example.myshops.ui.forget;

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
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgetFragment extends Fragment {

    private ForgetViewModel forgetViewModel;
    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        forgetViewModel =
                ViewModelProviders.of(this).get(ForgetViewModel.class);
        View root = inflater.inflate(R.layout.fragment_forget, container, false);
        final TextView textView = root.findViewById(R.id.text);
        final EditText login = root.findViewById(R.id.login);
        final Button change = root.findViewById(R.id.change);
        final Button signin = root.findViewById(R.id.signin);
        auth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).replaceFragments(LoginFragment.class);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login.getText().toString();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "We've sent a message to your email", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), "Wrong email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
//                SendMail sendMail = new SendMail(getContext(), email, "Forget password", "Change your password here");
//                sendMail.execute();
            }
        });
        forgetViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}