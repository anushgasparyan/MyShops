package com.example.myshops;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myshops.model.Product;
import com.example.myshops.model.Rating;
import com.example.myshops.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CustomAdapterFeedback extends BaseAdapter {

    Context c;
    ArrayList<Rating> ratings;
    String email;
    User user;

    public CustomAdapterFeedback(Context c, ArrayList<Rating> ratings, String email) {
        this.c = c;
        this.ratings = ratings;
        this.email = email;
    }

    @Override
    public int getCount() {
        return ratings.size();
    }

    @Override
    public Object getItem(int i) {
        return ratings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(c).inflate(R.layout.gridviewfeedback, viewGroup, false);
        }

        final Rating r = (Rating) this.getItem(i);
        ProgressDialog mProgressDialog = new ProgressDialog(c, ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("user");
        Query query2 = databaseReference2.orderByChild("id").equalTo(r.getUserID());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        TextView nameTxt = view.findViewById(R.id.name);
        TextView feedback = view.findViewById(R.id.feedback);
        TextView rating = view.findViewById(R.id.rating);
        ImageView img = view.findViewById(R.id.profile_image);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nameTxt.setText(user.getName() + " " + user.getSurname());
                feedback.setText(r.getFeedback());
                rating.setText(String.valueOf(r.getRating()));
                Picasso.get().load(user.getUrl())
                        .resize(60, 60)
                        .centerCrop()
                        .into(img);
            }
        }, 1000);


        return view;
    }
}
