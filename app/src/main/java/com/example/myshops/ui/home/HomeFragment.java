package com.example.myshops.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myshops.CustomAdapter;
import com.example.myshops.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ImageView imageView;
    Button button, button2;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    GridView gridView;
    int[] products;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = root.findViewById(R.id.simpleGridView);
        products = new int[]{R.mipmap.f1_foreground, R.mipmap.f2_foreground, R.mipmap.f3_foreground, R.mipmap.f4_foreground, R.mipmap.f5_foreground};
        CustomAdapter customAdapter = new CustomAdapter(getContext(), products);
        gridView.setAdapter(customAdapter);

//        firebaseStorage = FirebaseStorage.getInstance();
//        imageView = root.findViewById(R.id.imageView);
//        button = root.findViewById(R.id.button);
//        button2 = root.findViewById(R.id.button2);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent();
//                i.setType("image/*");
//                i.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(i, "Select picture"), 1);
//
//            }
//        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String notID = "com.example.android14";
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
//
//
//                NotificationCompat.Builder builder =
//                        new NotificationCompat.Builder(requireContext(), notID)
//                                .setAutoCancel(true)
//                                .setDefaults(Notification.DEFAULT_ALL)
//                                .setSmallIcon(R.drawable.person)
//                                .setContentInfo("info")
//                                .setWhen(System.currentTimeMillis())
//                                .setContentTitle("Title")
//                                .setContentText("Notification text")
//                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                .setSubText("Good")
//                                .setContentIntent(pendingIntent);
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    String channelId = "Your_channel_id";
//                    NotificationChannel channel = new NotificationChannel(
//                            channelId,
//                            "Channel human readable title",
//                            NotificationManager.IMPORTANCE_HIGH);
//                    notificationManager.createNotificationChannel(channel);
//                    builder.setChannelId(channelId);
//                }
//                notificationManager.notify(1, builder.build());
//            }
//        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storageReference = firebaseStorage.getReference("images/" + UUID.randomUUID().toString());
        if (requestCode == 1) {
            UploadTask uploadTask = storageReference.putFile(data.getData());
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("my", task.getResult().toString());
                        String file_url = task.getResult().toString().substring(0, task.getResult().toString().indexOf("token"));

                        Picasso.get().load(file_url).into(imageView);
                    }
                }
            });
        }


    }
}