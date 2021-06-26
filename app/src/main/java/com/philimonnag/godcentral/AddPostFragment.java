package com.philimonnag.godcentral;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
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
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.databinding.FragmentAddPostBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class AddPostFragment extends Fragment {
    private FragmentAddPostBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference reference;
    Uri postUri;
    User user;
    Date date;
    int choose=-1;
    String privacy="everyone";
    ProgressDialog pd;
    String post;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding=FragmentAddPostBinding.inflate(inflater,container,false);
         View root =binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        date = new Date();
        pd= new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage("uploading Post...");
        database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user=snapshot.getValue(User.class);
                    binding.gender.setText(user.getGender());
                    Picasso.get().load(user.getUrl()).into(binding.profilepic);
                    binding.username.setText(user.getuName());
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postUri==null) {
                    Toast.makeText(getContext(), "choose a image", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.postText.getText().toString())){
                    binding.postText.setError(" Write Something Don't be shy...");
                }else {
                    statusUpload(user.getUid());}
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_addPostFragment_to_homeFragment);
            }
        });

        binding.gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                privacy= user.getGender();
            }
        });
        binding.Everyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacy="everyone";
            }
        });
        binding.colorchooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(choose)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                              choose=selectedColor;
                              binding.postcard.setBackgroundColor(selectedColor);
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        binding.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picPostImg();
            }
        });

         return root;
    }

    private void picPostImg() {
        ImagePicker.with(this)
                     .crop(16,9)
                    .compress(1024)
                    .start();
    }


    private void statusUpload(String userId) {
        pd.show();
        reference=storage.getReference().child("post").child(date.getTime()+"");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),postUri);
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
                    Uri downloadUri=task.getResult();
                    String postimg=downloadUri.toString();
                    DatabaseReference databaseReference = database.getReference("posts");
                    String postId = databaseReference.push().getKey();
                    post=binding.postText.getText().toString();
//                    String prifileimage= user.getUrl();
//                    String username = user.getuName();
                    String time = String.valueOf(date.getTime());
                    Post posts = new Post(userId,postId,time,privacy,postimg,post,choose);
                    databaseReference.child(postId).setValue(posts);
                    pd.dismiss();
                    Navigation.findNavController(getView()).navigate(R.id.action_addPostFragment_to_homeFragment);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Navigation.findNavController(getView()).navigate(R.id.action_addPostFragment_to_homeFragment);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            postUri=data.getData();
            Picasso.get().load(postUri).into(binding.postImg);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}