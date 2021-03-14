package com.example.myshops;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myshops.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CustomAdapterOrder extends BaseAdapter {

    Context c;
    ArrayList<Product> products;
    ArrayList<Product> productsFilter;
    String email;

    public CustomAdapterOrder(Context c, ArrayList<Product> products, String email) {
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
            view = LayoutInflater.from(c).inflate(R.layout.gridviewmy, viewGroup, false);
        }
        ProgressDialog mProgressDialog = new ProgressDialog(c, ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


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

        return view;
    }
}
