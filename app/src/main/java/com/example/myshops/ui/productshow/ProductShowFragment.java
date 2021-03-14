package com.example.myshops.ui.productshow;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myshops.CustomAdapterFeedback;
import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.ViewPagerAdapter;
import com.example.myshops.model.Basket;
import com.example.myshops.model.Product;
import com.example.myshops.model.Rating;
import com.example.myshops.model.User;
import com.example.myshops.model.Wishlist;
import com.example.myshops.ui.basket.BasketFragment;
import com.example.myshops.ui.card.CardFragment;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.wishlist.WishlistFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProductShowFragment extends Fragment {
    Context context;
    View root;
    User user;
    Product p;
    ViewPagerAdapter mViewPagerAdapter;
    String key, userid, productid, useremail, from;
    AlertDialog.Builder builder, alert, rate;
    Rating r, rating;
    ArrayList<Rating> in = new ArrayList<>();
    Wishlist wishlist, wishlistt;
    Basket basket, baskett;
    CustomAdapterFeedback customAdapterFeedback;
    GridView gridView;
    ProgressDialog mProgressDialog;
    boolean b = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = getContext();
        root = inflater.inflate(R.layout.product_show_fragment, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.titlebar2);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        productid = getActivity().getIntent().getStringExtra("productid");
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        useremail = prefs.getString("email", "");
        productid = prefs.getString("productid", "");
        from = prefs.getString("from", "");

        alert = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);

        alert.setMessage("Product is out of stock!")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alert.create();

        rate = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        View view = inflater.inflate(R.layout.alertrate, null);
        rate.setView(view);
        AlertDialog dialog1 = rate.create();

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
        if (useremail.isEmpty()) {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            builder.setMessage("Please log in to continue")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        RatingBar ratingBar = root.findViewById(R.id.rating);
        TextView myrate = root.findViewById(R.id.myrate);
        gridView = root.findViewById(R.id.feedback);
        TextView feedback = view.findViewById(R.id.message);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("rating");
                Query query3 = databaseReference3.orderByChild("userID").equalTo(user.getId());
                query3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                rating = child.getValue(Rating.class);
                                if (rating.getProductID().equals(p.getId())) {
                                    ratingBar.setRating((float) rating.getRating());
                                    myrate.setText("Your rating` " + rating.getRating());
                                    key = child.getKey();
                                    rating.setId(key);
                                    b = true;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, 1500);

        ViewPager viewPager = root.findViewById(R.id.viewPage);
        ImageView basketicon = root.findViewById(R.id.basket);
        ImageView wish = root.findViewById(R.id.wish);
        TextView product_name = root.findViewById(R.id.product_name);
        TextView product_desc = root.findViewById(R.id.product_desc);
        TextView product_price = root.findViewById(R.id.product_price);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView send = view.findViewById(R.id.send);
        Button buy = root.findViewById(R.id.buy);
        RatingBar ratingbar = view.findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dialog1.show();
                }
                return true;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                if (!b) {
                    String f = "" + feedback.getText();
                    float d = ratingbar.getRating();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rating").push();
                    r = new Rating();
                    r.setUserID(user.getId());
                    r.setProductID(p.getId());
                    r.setId_id(user.getId() + "" + p.getId());
                    r.setRating(d);
                    r.setFeedback(f);
                    ratingBar.setRating(d);
                    Toast.makeText(getContext(), "Thanks for rating!", Toast.LENGTH_LONG).show();
                    myrate.setText("Your rating` " + d);
                    databaseReference.setValue(r);
                } else {
                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("rating");
                    bringRating();
                    databaseReference3.child(rating.getId()).removeValue();
                    String f = "" + feedback.getText();
                    float d = ratingbar.getRating();
                    r = new Rating();
                    r.setUserID(user.getId());
                    r.setProductID(p.getId());
                    r.setId_id(user.getId() + "" + p.getId());
                    r.setRating(d);
                    r.setFeedback(f);
                    ratingBar.setRating(d);
                    Toast.makeText(getContext(), "Rating updated!", Toast.LENGTH_LONG).show();
                    myrate.setText("Your rating` " + d);
                    databaseReference3.push().setValue(r);
                    rating();
                }
            }
        });
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
                        mViewPagerAdapter = new ViewPagerAdapter(context, images);
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

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("wishlist");
                Query query1 = databaseReference1.orderByChild("id_id").equalTo(productid + "-" + userid);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                wishlist = childSnapshot.getValue(Wishlist.class);
                                wishlist.setId(childSnapshot.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (wishlist != null) {
                    wish.setImageResource(R.drawable.heart_filled);
                    wish.setTag(R.drawable.heart_filled);
                } else {
                    wish.setImageResource(R.drawable.heart_not_filled);
                    wish.setTag(R.drawable.heart_not_filled);
                }
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("card");
                Query query1 = databaseReference1.orderByChild("id_id").equalTo(productid + "-" + userid);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                basket = childSnapshot.getValue(Basket.class);
                                basket.setId(childSnapshot.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (basket != null) {
                    basketicon.setImageResource(R.drawable.cart__filled);
                    basketicon.setTag(R.drawable.cart__filled);
                } else {
                    basketicon.setImageResource(R.drawable.cart_not_filled);
                    basketicon.setTag(R.drawable.cart_not_filled);
                }
            }
        }, 1000);
        mProgressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        basketicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketicon.getTag().equals(R.drawable.cart_not_filled)) {
                    if (p.getCount() != 0) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("card").push();
                        baskett = new Basket();
                        baskett.setProductID(p.getId());
                        baskett.setUserID(user.getId());
                        baskett.setId_id(p.getId() + "-" + user.getId());
                        databaseReference.setValue(baskett);
                        mProgressDialog.show();
                        databaseReference.setValue(baskett);
                        basketicon.setTag(R.drawable.cart__filled);
                        basketicon.setImageResource(R.drawable.cart__filled);
                        Toast.makeText(getContext(), "Product added to the basket!", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    } else {
                        dialog.show();
                    }
                } else if (basketicon.getTag().equals(R.drawable.cart__filled)) {
                    mProgressDialog.show();
                    bringCard();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("card");
                            databaseReference.child(basket.getId()).removeValue();
                            basketicon.setTag(R.drawable.cart_not_filled);
                            basketicon.setImageResource(R.drawable.cart_not_filled);
                            Toast.makeText(getContext(), "Product removed from the basket!", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }, 1000);
                }
            }
        });


        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wish.getTag().equals(R.drawable.heart_not_filled)) {
                    if (p.getCount() != 0) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("wishlist").push();
                        wishlistt = new Wishlist();
                        wishlistt.setProductID(p.getId());
                        wishlistt.setUserID(user.getId());
                        wishlistt.setId_id(p.getId() + "-" + user.getId());
                        databaseReference.setValue(wishlistt);
                        mProgressDialog.show();
                        databaseReference.setValue(wishlistt);
                        wish.setTag(R.drawable.heart_filled);
                        wish.setImageResource(R.drawable.heart_filled);
                        Toast.makeText(getContext(), "Product added to the wishlist!", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    } else {
                        dialog.show();
                    }
                } else if (wish.getTag().equals(R.drawable.heart_filled)) {
                    mProgressDialog.show();
                    bringWishlist();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("wishlist");
                            databaseReference.child(wishlist.getId()).removeValue();
                            wish.setTag(R.drawable.heart_not_filled);
                            wish.setImageResource(R.drawable.heart_not_filled);
                            Toast.makeText(getContext(), "Product removed from the wishlist!", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }, 1000);
                }
            }
        });

        rating();

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).replaceFragments(CardFragment.class);
            }
        });
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (from.equals("home")) {
                ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
            } else if (from.equals("basket")) {
                ((MainActivity) requireActivity()).replaceFragments(BasketFragment.class);
            } else if (from.equals("wishlist")) {
                ((MainActivity) requireActivity()).replaceFragments(WishlistFragment.class);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void bringWishlist() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("wishlist");
        Query query1 = databaseReference1.orderByChild("id_id").equalTo(productid + "-" + userid);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        wishlist = childSnapshot.getValue(Wishlist.class);
                        wishlist.setId(childSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void bringCard() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("card");
        Query query1 = databaseReference1.orderByChild("id_id").equalTo(productid + "-" + userid);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        basket = childSnapshot.getValue(Basket.class);
                        basket.setId(childSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void bringRating() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("rating");
        Query query1 = databaseReference1.orderByChild("id_id").equalTo(user.getId() + "" + p.getId());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        rating = childSnapshot.getValue(Rating.class);
                        rating.setId(childSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void rating(){
        in.clear();
        DatabaseReference databasefeedback = FirebaseDatabase.getInstance().getReference("rating");
        Query queryfeedback = databasefeedback.orderByChild("productID").equalTo(productid);
        queryfeedback.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mProgressDialog.show();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Rating r = child.getValue(Rating.class);
                        in.add(r);
                    }
                    customAdapterFeedback = new CustomAdapterFeedback(getContext(), in, useremail);
                    gridView.setAdapter(customAdapterFeedback);
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}