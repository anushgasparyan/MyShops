package com.example.myshops.ui.home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myshops.CustomAdapter;
import com.example.myshops.R;
import com.example.myshops.model.Category;
import com.example.myshops.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    GridView gridView;
    LinearLayout sets, sofa, armchair, table, chair, compchair, bed, wardrobe, sidetable, dressingtable, mirror, lamp;
    List<LinearLayout> categories;
    Category categoryyy;
    ArrayList<Product> in = new ArrayList<>();
    CustomAdapter customAdapter;
    String email, id;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        gridView = root.findViewById(R.id.simpleGridView);
        categories = new ArrayList<>();
        sets = root.findViewById(R.id.sets);
        sofa = root.findViewById(R.id.sofa);
        armchair = root.findViewById(R.id.armchair);
        table = root.findViewById(R.id.table);
        chair = root.findViewById(R.id.chair);
        compchair = root.findViewById(R.id.compchair);
        bed = root.findViewById(R.id.bed);
        wardrobe = root.findViewById(R.id.wardrobe);
        sidetable = root.findViewById(R.id.sidetable);
        dressingtable = root.findViewById(R.id.dressingtable);
        mirror = root.findViewById(R.id.mirror);
        lamp = root.findViewById(R.id.lamp);
        categories.add(sets);
        categories.add(sofa);
        categories.add(armchair);
        categories.add(table);
        categories.add(chair);
        categories.add(compchair);
        categories.add(bed);
        categories.add(wardrobe);
        categories.add(sidetable);
        categories.add(dressingtable);
        categories.add(mirror);
        categories.add(lamp);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        email = prefs.getString("email", "");
        id = prefs.getString("id", "");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                p();
            }
        }, 500);


        for (LinearLayout category : categories) {
            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        categoryyy = null;
                        p();
                    } else {
                        for (LinearLayout cat : categories) {
                            cat.setSelected(false);
                        }
                        v.setSelected(true);
                        TextView textView = (TextView) category.getChildAt(2);
                        categoryyy = Category.valueOf((String) textView.getText());
                        cp(categoryyy);
                    }
                }
            });
        }
        return root;
    }

    private void cp(Category category) {
        progressDialog.show();
        in.clear();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("product");
        Query query2 = databaseReference2.orderByChild("active").equalTo(1);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Product p = child.getValue(Product.class);
                        if (p.getCategory() == category) {
                            if (!p.getUserID().equals(id)) {
                                in.add(p);
                            }

                        }
                    }
                    customAdapter = new CustomAdapter(getContext(), in, email);
                    gridView.setAdapter(customAdapter);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void p() {
        progressDialog.show();
        in.clear();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("product");
        Query query2 = databaseReference2.orderByChild("active").equalTo(1);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Product p = child.getValue(Product.class);
                        if (!p.getUserID().equals(id)) {
                            in.add(p);
                        }
                    }
                }
                    customAdapter = new CustomAdapter(getContext(), in, email);
                    gridView.setAdapter(customAdapter);
                    progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}