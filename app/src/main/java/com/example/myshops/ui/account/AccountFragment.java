package com.example.myshops.ui.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.CustomAdapter;
import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.model.Product;
import com.example.myshops.model.User;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {
    View root;
    AlertDialog.Builder builder;
    FirebaseAuth auth;
    User user;
    ImageView profile_img;
    ProgressDialog progressDialog;
    String email;

    private static final int PICK_IMG = 1;

    private AccountViewModel accountViewModel;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        root = inflater.inflate(R.layout.fragment_account, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading your data");
        progressDialog.show();
        final TextView textView = root.findViewById(R.id.text);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        email = prefs.getString("email", "");
        Log.e("email", email);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("user");
        Query query2 = databaseReference2.orderByChild("email").equalTo(email);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("email", String.valueOf(snapshot) + " esia");
                if (snapshot.exists()) {
                    Log.e("email", String.valueOf(snapshot));
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        Log.e("email", "user " + String.valueOf(user));
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final TextView name_surname = root.findViewById(R.id.namesurname);
                final TextView mail = root.findViewById(R.id.email);
                profile_img = root.findViewById(R.id.profile_image);
                final TextView changephoto = root.findViewById(R.id.changephoto);
                final Button del = root.findViewById(R.id.deletephoto);
                final Button logout = root.findViewById(R.id.logout);
                if (user != null) {
                    name_surname.setText(user.getName() + " " + user.getSurname());
                    mail.setText(user.getEmail());
                    if (user.getUrl() != null) {
                        Picasso.get().load(user.getUrl())
                                .resize(120, 120)
                                .centerCrop()
                                .into(profile_img);
                    } else {
                        profile_img.setImageResource(R.mipmap.userlogooo_foreground);
                    }
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            profile_img.setImageResource(R.mipmap.userlogooo_foreground);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference().child("user");
                            reference.child(user.getId()).child("url").removeValue();
                        }
                    });
                    changephoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setType("image/*");
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(i, "Select picture"), PICK_IMG);
                        }
                    });

                    auth = FirebaseAuth.getInstance();
                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            auth.signOut();
                            SharedPreferences.Editor preferences = requireActivity().getSharedPreferences("MYPREF", Context.MODE_PRIVATE).edit();
                            preferences.putString("email", "").apply();
                            ((MainActivity) requireActivity()).replaceFragments(LoginFragment.class);
                        }
                    });
                }
            }
        }, 1000);


        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMG && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            profile_img.setImageBitmap(bitmap);
            final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("UserImageFolder");
            final StorageReference imagename = ImageFolder.child("image/" + UUID.randomUUID() + System.currentTimeMillis());
            imagename.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            user.setUrl(url);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference().child("user");
                            reference.child(user.getId()).child("url").setValue(url);
                        }
                    });

                }
            });
        }

    }

}
