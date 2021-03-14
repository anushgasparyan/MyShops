package com.example.myshops.ui.card;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.braintreepayments.cardform.view.CardForm;
import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.model.Order;
import com.example.myshops.model.Product;
import com.example.myshops.model.User;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class CardFragment extends Fragment {

    private CardViewModel cardViewModel;
    AlertDialog.Builder builder;
    CardForm cardForm;
    FirebaseAuth auth;
    User user;
    Product product;
    String userid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cardViewModel =
                ViewModelProviders.of(this).get(CardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_card, container, false);
        final TextView textView = root.findViewById(R.id.text);
        final Button pay = root.findViewById(R.id.pay);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String productid = prefs.getString("productid", "");

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("user");
        Query query2 = databaseReference2.orderByChild("email").equalTo(email);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
        Query query = databaseReference.orderByChild("id").equalTo(productid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        product = child.getValue(Product.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if (email.isEmpty()) {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            builder.setMessage("Please log in to continue")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Go to login page", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) requireActivity()).replaceFragments(LoginFragment.class);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            BottomNavigationView navigationView = (BottomNavigationView) getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
            ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
        }
        cardForm = root.findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .expirationRequired(true)
                .actionLabel("PAY WITH YOUR CARD")
                .setup((AppCompatActivity) getContext());
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        auth = FirebaseAuth.getInstance();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()) {
                    builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Card data")
                            .setMessage("Card number - " + cardForm.getCardNumber() + "\n" + "Owner - " + cardForm.getCardholderName() + "\n" + "CVV - " + cardForm.getCvv() + "\n" +
                                    "Expiration date - " + cardForm.getExpirationMonth() + "/" + cardForm.getExpirationYear())
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "Thanks you for purchase!", Toast.LENGTH_LONG).show();
                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("product");
                                    Query query1 = databaseReference1.orderByChild("id").equalTo(productid);
                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    Map<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("count", product.getCount()-1);
                                                    child.getRef().updateChildren(hashMap);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    DatabaseReference dataw = FirebaseDatabase.getInstance().getReference("wishlist");
                                    Query w = dataw.orderByChild("id_id").equalTo(productid + "-" + userid);
                                    w.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    child.getRef().removeValue();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    DatabaseReference datab = FirebaseDatabase.getInstance().getReference("card");
                                    Query c = datab.orderByChild("id_id").equalTo(productid + "-" + userid);
                                    c.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    child.getRef().removeValue();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("order").push();
                                    Order order = new Order();
                                    order.setProductID(productid);
                                    order.setUserID(user.getId());
                                    order.setId_id(productid + "-" + user.getId());
                                    databaseReference.setValue(order);
                                    ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getContext(), "All fields required", Toast.LENGTH_LONG).show();
                }
            }
        });
        cardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}