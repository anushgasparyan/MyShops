package com.example.myshops;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CustomAdapter extends BaseAdapter {

    Context context;
    int[] products;
    int[] height = new int[]{150, 250};
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, int[] products) {
        this.context = applicationContext;
        this.products = products;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return products.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.gridview, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(products[i]);
        return view;
    }
}
