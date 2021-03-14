package com.example.myshops;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myshops.model.Product;
import com.example.myshops.ui.notifications.NotificationsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class AdminAdapter extends BaseAdapter {

    Context c;
    ArrayList<Product> products;
    ArrayList<Product> productsFilter;
    String email;

    public AdminAdapter(Context c, ArrayList<Product> products, String email) {
        this.c = c;
        this.products = products;
        this.productsFilter = products;
        this.email = email;
    }

    @Override
    public int getCount() {
        return products == null ? 0 : products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(c).inflate(R.layout.admingridview, viewGroup, false);
        }

        ProgressDialog mProgressDialog = new ProgressDialog(c, ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final Product p = (Product) this.getItem(i);

        TextView nameTxt = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        ImageView img = view.findViewById(R.id.icon);
        Button accept = view.findViewById(R.id.accept);
        Button decline = view.findViewById(R.id.decline);

        nameTxt.setText(p.getName());
        price.setText(p.getPrice() + " " + p.getCurrency());
        Picasso.get().load(p.getProductImgs().get(0))
                .resize(180, 180)
                .transform(new RoundedCornersTransformation(10, 1))
                .centerCrop()
                .into(img);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("product");
                Query query1 = databaseReference1.orderByChild("id").equalTo(p.getId());
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put("active", 1);
                                child.getRef().updateChildren(hashMap);
                                Toast.makeText(c, "Product is accepted!", Toast.LENGTH_LONG).show();
                                products.clear();
                                notifyDataSetChanged();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @SuppressLint("IntentReset")
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                    }
                                }, 500);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("product").orderByChild("id").equalTo(p.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            products.clear();
                            notifyDataSetChanged();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(c, "Product is declined!", Toast.LENGTH_LONG).show();
                                }
                            }, 500);

                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor preferences = c.getSharedPreferences("MYPREF", Context.MODE_PRIVATE).edit();
                preferences.putString("productid", p.getId()).apply();
                preferences.putString("useremail", email).apply();
                ((MainActivity) c).replaceFragments(NotificationsFragment.class);
            }
        });
        return view;
    }


}
