package com.example.myshops;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.myshops.model.Product;
import com.example.myshops.model.Type;
import com.example.myshops.model.User;
import com.example.myshops.model.Wishlist;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductShow extends AppCompatActivity {
    User user;
    Product p;
    String userid;
    String productid;
    AlertDialog.Builder builder;
    Wishlist wishlist;
    Wishlist wishlistt;
    final String[] key = {null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        productid = getIntent().getStringExtra("productid");
        String useremail = getIntent().getStringExtra("useremail");

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("user");
        Query query2 = databaseReference2.orderByChild("email").equalTo(useremail);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        userid = user.getId();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if (useremail.isEmpty()) {
            builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("You are not logged in!")
                    .setMessage("Please log in to continue")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        ViewPager viewPager = findViewById(R.id.viewPage);
        ImageView basket = findViewById(R.id.basket);
        ImageView wish = findViewById(R.id.wish);
        TextView product_name = findViewById(R.id.product_name);
        TextView product_desc = findViewById(R.id.product_desc);
        TextView product_price = findViewById(R.id.product_price);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
        Query query = databaseReference.orderByChild("id").equalTo(productid);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        p = child.getValue(Product.class);
                        List<String> images = p.getProductImgs();
                        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(ProductShow.this, images);
                        viewPager.setAdapter(mViewPagerAdapter);
                        product_name.setText(p.getName());
                        product_desc.setText(p.getDesc());
                        product_price.setText(p.getPrice() + " " + p.getCurrency());
                        break;
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
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("wishlist");
                Query query1 = databaseReference1.orderByChild("id_id").equalTo(productid + "-" + userid);
                final boolean[] b = {false};
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
//                            b[0] = true;
                            wishlist = snapshot.getValue(Wishlist.class);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }, 1000);

        if (wishlist != null) {
            wish.setImageResource(R.drawable.heart_filled);
            wish.setTag(R.drawable.heart_filled);
        } else {
            wish.setImageResource(R.drawable.heart_not_filled);
            wish.setTag(R.drawable.heart_not_filled);
        }
        ProgressDialog progressDoalog = new ProgressDialog(ProductShow.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setTitle("ProgressDialog bar example");
        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wish.getTag().equals(R.drawable.heart_not_filled)) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("wishlist").push();
                    key[0] = databaseReference.push().getKey();
                    Log.e("tag", "key " + key[0]);
                    wishlistt = new Wishlist();
                    wishlistt.setProductID(p.getId());
                    wishlistt.setUserID(user.getId());
                    wishlistt.setId_id(p.getId() + "-" + user.getId());
                    wishlist = wishlistt;
                    databaseReference.setValue(wishlistt);
                    progressDoalog.show();
                    Log.e("tag", "aaa" + key[0]);
                    databaseReference.setValue(wishlistt);
                    wish.setTag(R.drawable.heart_filled);
                    wish.setImageResource(R.drawable.heart_filled);
                    progressDoalog.dismiss();
                } else if (wish.getTag().equals(R.drawable.heart_filled)) {
                            progressDoalog.show();
                            Log.e("tag", String.valueOf(wishlist));
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("wishlist");
                            databaseReference.child(wishlist.getId()).removeValue();
                            wish.setTag(R.drawable.heart_not_filled);
                            wish.setImageResource(R.drawable.heart_not_filled);
                            progressDoalog.dismiss();
                }
            }
        });


        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basket.getTag().equals(R.drawable.cart_not_filled)) {
                    basket.setTag(R.drawable.cart__filled);
                    basket.setImageResource(R.drawable.cart__filled);
                } else if (basket.getTag().equals(R.drawable.cart__filled)) {
                    basket.setTag(R.drawable.cart_not_filled);
                    basket.setImageResource(R.drawable.cart_not_filled);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}