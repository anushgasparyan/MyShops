package com.example.myshops.ui.basket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myshops.CustomAdapterCard;
import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.model.Basket;
import com.example.myshops.model.Product;
import com.example.myshops.model.User;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BasketFragment extends Fragment {

    AlertDialog.Builder builder;
    ArrayList<Product> in = new ArrayList<>();
    CustomAdapterCard customAdapter;
    User user;
    String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);
        GridView gridView = root.findViewById(R.id.simpleGridView);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        if (email.isEmpty()) {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            builder.setMessage("Please log in to continue")
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
        }else {
            ProgressDialog mProgressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
            Query query = databaseReference.orderByChild("email").equalTo(email);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            user = child.getValue(User.class);
                            userId = user.getId();
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
                    in.clear();
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("card");
                    Query query2 = databaseReference2.orderByChild("userID").equalTo(userId);
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Basket basket = child.getValue(Basket.class);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("product");
                                            Query query1 = databaseReference1.orderByChild("id").equalTo(basket.getProductID());
                                            query1.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        for (DataSnapshot snapshotChild : snapshot.getChildren()) {
                                                            Product p = snapshotChild.getValue(Product.class);
                                                            in.add(p);
                                                        }
                                                    }
                                                    customAdapter = new CustomAdapterCard(getContext(), in, email);
                                                    gridView.setAdapter(customAdapter);
                                                    mProgressDialog.dismiss();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }, 50);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }, 50);
            mProgressDialog.dismiss();
        }
        return root;
    }
}