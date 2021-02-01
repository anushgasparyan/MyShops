package com.example.myshops;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myshops.model.Product;
import com.example.myshops.model.ProductImg;
import com.example.myshops.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.content.Context.MODE_PRIVATE;

public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<Product> products;
    String email;

    public CustomAdapter(Context c, ArrayList<Product> products, String email) {
        this.c = c;
        this.products = products;
        this.email = email;
        Log.d("prod", this.products.toString());
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
            view = LayoutInflater.from(c).inflate(R.layout.gridview, viewGroup, false);
        }


        final Product p = (Product) this.getItem(i);

        TextView nameTxt = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        ImageView img = view.findViewById(R.id.icon);

        nameTxt.setText(p.getName());
        price.setText(p.getPrice() + " " + p.getCurrency());
        Picasso.get().load(p.getProductImgs().get(0))
                .resize(180, 180)
                .transform(new RoundedCornersTransformation(10, 1))
                .centerCrop()
                .into(img);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProductShow.class);
                intent.putExtra("productid", p.getId());
                intent.putExtra("useremail", email);
                Log.e("ids", p.getName());
                c.startActivity(intent);
            }
        });
        return view;
    }

}
