package com.example.myshops.ui.addProduct;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.Item;
import com.example.myshops.MainActivity;
import com.example.myshops.R;
import com.example.myshops.Token;
import com.example.myshops.model.Category;
import com.example.myshops.model.Currency;
import com.example.myshops.model.Product;
import com.example.myshops.model.ProductImg;
import com.example.myshops.model.Type;
import com.example.myshops.model.User;
import com.example.myshops.service.ApiService;
import com.example.myshops.ui.home.HomeFragment;
import com.example.myshops.ui.login.LoginFragment;
import com.example.myshops.ui.notifications.Notification;
import com.example.myshops.ui.notifications.Response;
import com.example.myshops.ui.notifications.Sender;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class AddProductFragment extends Fragment {

    private AddProductViewModel addProductViewModel;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    Button add;
    private static final int PICK_IMG = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int uploads = 0;
    private ProgressDialog progressDialog;
    private LinearLayout layout;
    int CurrentImageSelect;
    TextView textView1;
    Spinner categories, currency;
    NumberPicker numberPicker;
    FirebaseAuth auth;
    User user;
    ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addProductViewModel =
                ViewModelProviders.of(this).get(AddProductViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addproduct, container, false);
        TextView textView = root.findViewById(R.id.text);
        TextView name = root.findViewById(R.id.name);
        TextView desc = root.findViewById(R.id.desc);
        TextView price = root.findViewById(R.id.price);
        categories = root.findViewById(R.id.spinner);
        currency = root.findViewById(R.id.spinner2);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding product ...");
        textView1 = root.findViewById(R.id.uploadtext);
        layout = root.findViewById(R.id.imglayout);
        SharedPreferences prefs = getActivity().getSharedPreferences("MYPREF", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        if (email.isEmpty()) {
            builder = new AlertDialog.Builder(getContext());
            builder.setTitle("You are not logged in!")
                    .setMessage("Please log in to continue")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
            BottomNavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
            ((MainActivity) requireActivity()).replaceFragments(HomeFragment.class);
        }

        apiService = Token.getFCMClient();
        auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("user");
        if (auth.getCurrentUser() != null) {
            Query query = databaseReference2.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
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

        }


        add = root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                textView.setText("Please Wait ... ");
                progressDialog.show();
                if (!name.getText().toString().isEmpty() && !desc.getText().toString().isEmpty() && !price.getText().toString().isEmpty()) {
                    if (CurrentImageSelect != 0) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product").push();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDateandTime = sdf.format(new Date());
                        Product product = new Product();
                        product.setId(databaseReference.getKey());
                        product.setName(name.getText().toString());
                        product.setDesc(desc.getText().toString());
                        product.setCurrency(Currency.valueOf(currency.getSelectedItem().toString()));
                        product.setPrice(Double.parseDouble(price.getText().toString()));
                        product.setDate(currentDateandTime);
                        product.setCount(numberPicker.getValue());
                        product.setCategory(Category.valueOf(categories.getSelectedItem().toString()));
                        product.setUserID(user.getId());
                        databaseReference.setValue(product);
                        final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
                        for (uploads = 0; uploads < ImageList.size(); uploads++) {
                            Uri Image = ImageList.get(uploads);
                            final StorageReference imagename = ImageFolder.child("image/" + Image.getLastPathSegment());
                            imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("productIMG").push();
                                            String url = String.valueOf(uri);
                                            ProductImg productImg = new ProductImg();
                                            productImg.setId(databaseReference1.getKey());
                                            productImg.setUrl(url);
                                            productImg.setProductID(databaseReference.getKey());
                                            databaseReference1.setValue(productImg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Notification notification = new Notification(name.getText().toString(), desc.getText().toString());
                                                    Sender sender = new Sender(notification, Token.currentToken);
                                                    Log.e("my", sender.to);
                                                    Log.e("my", sender.notification.body);
                                                    Log.e("my", sender.notification.title);
                                                    apiService.sender(sender).enqueue(new Callback<Response>() {
                                                        @Override
                                                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                                            if (response.body().success==1){
                                                                Toast.makeText(getContext(), "send notification", Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Response> call, Throwable t) {

                                                        }
                                                    });
                                                    name.setText("");
                                                    desc.setText("");
                                                    price.setText("");
                                                    numberPicker.setValue(1);
                                                    layout.removeAllViews();
                                                    textView1.setText("");
                                                    Toast.makeText(getContext(), "Successfully added!", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                    ImageList.clear();


                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), "No images uploaded", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "Please complete all the fields", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });


        numberPicker = root.findViewById(R.id.numberpicker);
        List<String> pickerVals = new ArrayList<String>();
        for (int i = 1; i <= 100; i++) {
            pickerVals.add(String.valueOf(i));
        }
        String[] vals = new String[pickerVals.size()];
        pickerVals.toArray(vals);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        numberPicker.setDisplayedValues(vals);
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText)
                ((EditText) child).setTextColor(Color.BLACK);
        }


        final Button addphoto = root.findViewById(R.id.addphoto);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(i, "Select picture"), PICK_IMG);
            }
        });


        addProductViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

//    private void SendLink(String url) {
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("link", url);
//        ProductImg productImg = new ProductImg();
//        productImg.setUrl(url);
//        productImg.setProductID();
//        databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(getContext(), "Successfully added!", Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//                ImageList.clear();
//            }
//        });


//    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    CurrentImageSelect = 0;
                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        ImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    textView1.setVisibility(View.VISIBLE);
                    textView1.setText("You have Selected " + ImageList.size() + " pictures");
                }
            }
        }
        layout.removeAllViews();
        for (Uri uri : ImageList) {
            ImageView imgView = new ImageView(getContext());
            imgView.setImageURI(uri);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
            imgView.setLayoutParams(lp);
            layout.addView(imgView);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast t = new Toast(getContext());
                    LayoutInflater factory = LayoutInflater.from(getContext());
                    final View view = factory.inflate(R.layout.img, null);
                    final ImageView imageView = view.findViewById(R.id.bigimg);
                    imageView.setImageURI(uri);
                    t.setView(view);
                    t.setGravity(Gravity.FILL, 0, 0);
                    t.show();
                }
            });
        }

    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        storageReference = firebaseStorage.getReference("images/" + UUID.randomUUID().toString());
//        Log.e("my", String.valueOf(requestCode));
//        if (requestCode == 1) {
//            Log.e("my", String.valueOf(data.getData()));
//
//            UploadTask uploadTask = storageReference.putFile(data.getData());
//            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    Log.e("my", String.valueOf(task.isSuccessful()));
//
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
//                    }
//                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_LONG).show();
//
//                    return storageReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    Log.e("my", String.valueOf(task.isSuccessful()));
//                    if (task.isSuccessful()) {
//                        Log.d("my", task.getResult().toString());
//                        String file_url = task.getResult().toString().substring(0, task.getResult().toString().indexOf("token"));
//                        Picasso.get().load(file_url).into(imageView);
//                    }
//                }
//            });
//
//        }
//
//
//    }
}