package com.example.myshops;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myshops.model.Product;
import com.example.myshops.ui.myproducts.MyProductsFragment;
import com.example.myshops.ui.myproductsshow.MyProductsShowFragment;
import com.example.myshops.ui.productshow.ProductShowFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MyCustomAdapter extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<Product> products;
    ArrayList<Product> productsFilter;
    String email;

    public MyCustomAdapter(Context c, ArrayList<Product> products, String email) {
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


        final Product p = (Product) this.getItem(i);

        TextView nameTxt = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.desc);
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
                SharedPreferences.Editor preferences = c.getSharedPreferences("MYPREF", Context.MODE_PRIVATE).edit();
                preferences.putString("productid", p.getId()).apply();
                preferences.putString("useremail", email).apply();
                preferences.putString("from", "home").apply();
                ((MainActivity)c).replaceFragments(MyProductsShowFragment.class);
            }
        });
        return view;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = productsFilter.size();
                    filterResults.values = productsFilter;
                } else {
                    String search = constraint.toString().toLowerCase();
                    List<Product> searchResults = new ArrayList<>();
                    for (Product product : productsFilter) {
                        if (product.getName().contains(search) || product.getDesc().contains(search)) {
                            searchResults.add(product);
                        }
                    }
                    filterResults.count = searchResults.size();
                    filterResults.values = searchResults;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                products = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
