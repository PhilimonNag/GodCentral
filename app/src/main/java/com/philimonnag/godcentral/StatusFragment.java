package com.philimonnag.godcentral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philimonnag.godcentral.Model.Status;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.Model.UserStatus;
import com.philimonnag.godcentral.databinding.FragmentStatusBinding;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class StatusFragment extends Fragment {
    private FragmentStatusBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference reference;
    Uri statusUri;
    User user;
    Date date;
    String privacy="everyone";
    ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentStatusBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        date = new Date();
        pd=new ProgressDialog(getContext());
        pd.setMessage("Pleas Wait..");
        pd.setCancelable(false);
        database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user=snapshot.getValue(User.class);
                    binding.gender.setText(user.getGender());}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy=user.getGender();
            }
        });
        binding.Everyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy="everyone";
            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(statusUri==null) {
                    Toast.makeText(getContext(), "choose a image", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.caption.getText().toString())){
                    binding.caption.setError(" Write Something Don't be shy...");
                }else {
                    statusUpload();}
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_statusFragment_to_homeFragment);

            }
        });
        binding.imagechoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picPostImg();
            }
        });
        return root;
    }
    private void picPostImg() {
        ImagePicker.with(this)
                .crop(9,16)
                .compress(1024)
                .start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            statusUri=data.getData();
            Picasso.get().load(statusUri).into(binding.imagechoose);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void statusUpload() {
        pd.show();
        reference=storage.getReference().child("status").child(date.getTime()+"");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),statusUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] fileInBytes = baos.toByteArray();
        UploadTask uploadTask = reference.putBytes(fileInBytes);
        Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();}
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Uri uri=task.getResult();
                    UserStatus userStatus = new UserStatus();
                    userStatus.setuName(user.getuName());
                    userStatus.setUrl(user.getUrl());
                    userStatus.setUserId(user.getUid());
                    userStatus.setLastUpdated(date.getTime());
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("uName", userStatus.getuName());
                    obj.put("url", userStatus.getUrl());
                    obj.put("lastUpdated", userStatus.getLastUpdated());
                    obj.put("userId",userStatus.getUserId());
                    String caption = binding.caption.getText().toString();
                    Status status = new Status(uri.toString(),privacy,caption,userStatus.getLastUpdated());
                    database.getReference()
                            .child("stories")
                            .child(mAuth.getCurrentUser().getUid())
                            .updateChildren(obj);
                    database.getReference()
                            .child("stories")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("statuses")
                            .push()
                            .setValue(status);
                    Navigation.findNavController(getView()).navigate(R.id.action_statusFragment_to_homeFragment);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Navigation.findNavController(getView()).navigate(R.id.action_statusFragment_to_homeFragment);
            }
        });
    }
}