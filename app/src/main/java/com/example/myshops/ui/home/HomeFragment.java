package com.example.myshops.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.CustomAdapter;
import com.example.myshops.MainActivity;
import com.example.myshops.ProductShow;
import com.example.myshops.R;
import com.example.myshops.model.Product;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ImageView imageView;
    Button button, button2;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    GridView gridView;
    LinearLayout sets, sofa, armchair, table, chair, compchair, bed, wardrobe, sidetable, dressingtable, mirror, lamp;
    List<LinearLayout> categories, selected_categories;
    ArrayList<Product> in=new ArrayList<>();
    CustomAdapter customAdapter;
    String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = root.findViewById(R.id.simpleGridView);
        categories = new ArrayList<>();
        selected_categories = new ArrayList<>();
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
        for (LinearLayout category : categories) {
            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected()){
                        selected_categories.remove(category);
                        Toast.makeText(getContext(), String.valueOf(v.isSelected()), Toast.LENGTH_LONG).show();
                    }else {
                        for (LinearLayout cat : categories) {
                            cat.setSelected(false);
                        }
                        v.setSelected(true);
                        selected_categories.clear();
                        selected_categories.add(category);
                        Toast.makeText(getContext(), String.valueOf(v.isSelected()), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }



        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("product");
        Query query2 = databaseReference2.orderByChild("active").equalTo(0);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Product p = child.getValue(Product.class);
                        Log.d("prod", p.toString());
                        in.add(p);
                    }
                    customAdapter = new CustomAdapter(getContext(), in, email);
                    gridView.setAdapter(customAdapter);
                    Log.d("prod", in.toString());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return root;
    }

}