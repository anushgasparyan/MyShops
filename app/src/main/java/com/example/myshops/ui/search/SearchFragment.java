package com.example.myshops.ui.search;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.CustomAdapter;
import com.example.myshops.R;
import com.example.myshops.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {

    GridView gridView;
    SearchView search;
    ImageView filter;
    String email, id;
    CustomAdapter customAdapter;
    ArrayList<Product> in;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        gridView = root.findViewById(R.id.simpleGridView);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        email = prefs.getString("email", "");
        id = prefs.getString("id", "");
        in = new ArrayList<>();
        search = root.findViewById(R.id.search);
        filter = root.findViewById(R.id.filter);
        search.setQueryHint("What are you looking for?");
        search.onActionViewExpanded();
        View view = getLayoutInflater().inflate(R.layout.filter, null);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        TextView seekbarprogress = view.findViewById(R.id.progress);
        TextView ok = view.findViewById(R.id.OK);
        TextView cancel = view.findViewById(R.id.cancel);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        alert.setView(view);
        AlertDialog alertDialog = alert.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p(seekBar.getProgress());
                alertDialog.dismiss();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekbarprogress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        if(!search.isFocused()) {
            search.clearFocus();
        }
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
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO Auto-generated method stub

                customAdapter.getFilter().filter(query);


                return false;
            }
        });
        return root;
    }

    private void p(int progress) {
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
                        if (p.getPrice() <= progress) {
                            if (!p.getUserID().equals(id)) {
                                in.add(p);
                            }
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