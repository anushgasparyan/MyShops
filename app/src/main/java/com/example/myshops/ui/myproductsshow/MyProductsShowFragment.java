package com.example.myshops.ui.myproductsshow;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.ViewPagerAdapter;
import com.example.myshops.model.Product;
import com.example.myshops.model.User;
import com.example.myshops.ui.myproducts.MyProductsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyProductsShowFragment extends Fragment {
    View root;
    User user;
    Product p;
    String userid, productid, useremail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.my_products_show, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.titlebar2);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        productid = getActivity().getIntent().getStringExtra("productid");
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        useremail = prefs.getString("email", "");
        productid = prefs.getString("productid", "");

        ProgressDialog mProgressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Deleting...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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

        ViewPager viewPager = root.findViewById(R.id.viewPage);
        TextView product_name = root.findViewById(R.id.product_name);
        TextView product_desc = root.findViewById(R.id.product_desc);
        TextView product_price = root.findViewById(R.id.product_price);
        Button delete = root.findViewById(R.id.delete);
        TextView count = root.findViewById(R.id.count);
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
                        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getContext(), images);
                        viewPager.setAdapter(mViewPagerAdapter);
                        product_name.setText(p.getName());
                        product_desc.setText(p.getDesc());
                        product_price.setText(p.getPrice() + " " + p.getCurrency());
                        if (p.getCount() != 0) {
                            count.setText(p.getCount() + " products in stock");
                        } else {
                            count.setTextColor(Color.RED);
                            count.setText("OUT OF STOCK!");
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("product").child(p.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getContext(), "Product deleted!", Toast.LENGTH_LONG).show();
                                    ((MainActivity) requireActivity()).replaceFragments(MyProductsFragment.class);
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

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((MainActivity) requireActivity()).replaceFragments(MyProductsFragment.class);
        }

        return super.onOptionsItemSelected(item);
    }
}