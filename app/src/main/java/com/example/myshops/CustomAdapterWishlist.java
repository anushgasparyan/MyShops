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

import com.example.myshops.model.Product;
import com.example.myshops.ui.productshow.ProductShowFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CustomAdapterWishlist extends BaseAdapter {

    Context c;
    ArrayList<Product> products;
    ArrayList<Product> productsFilter;
    String email;

    public CustomAdapterWishlist(Context c, ArrayList<Product> products, String email) {
        this.c = c;
        this.products = products;
        this.productsFilter = products;
        this.email = email;
    }

    @Override
    public int getCount() {
        return products.size();
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
            view = LayoutInflater.from(c).inflate(R.layout.gridviewcard, viewGroup, false);
        }
        ProgressDialog mProgressDialog = new ProgressDialog(c, ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        final Product p = (Product) this.getItem(i);

        TextView nameTxt = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        TextView desc = view.findViewById(R.id.feedback);
        Button del = view.findViewById(R.id.deletefrombasket);
        ImageView img = view.findViewById(R.id.icon);

        nameTxt.setText(p.getName());
        price.setText(p.getPrice() + " " + p.getCurrency());
        desc.setText(p.getDesc());
        Picasso.get().load(p.getProductImgs().get(0))
                .resize(180, 180)
                .transform(new RoundedCornersTransformation(10, 1))
                .centerCrop()
                .into(img);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("wishlist").orderByChild("productID").equalTo(p.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            products.clear();
                            notifyDataSetChanged();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
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
                preferences.putString("from", "wishlist").apply();
                ((MainActivity)c).replaceFragments(ProductShowFragment.class);

            }
        });
        return view;
    }
}
