package com.philimonnag.godcentral.Login;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.FragmentSignUpBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class SignUpFragment extends Fragment {
   private FragmentSignUpBinding binding;
    private StorageReference storageRef;
    private Uri imgUri;
    private String gender;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    Date date;
    private ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding= FragmentSignUpBinding.inflate(inflater,container,false);
       View root= binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        binding.male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="Male";
            }
        });
        binding.female.setOnClickListener(view -> gender="female");
        binding.signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pd=new ProgressDialog(getContext());
               pd.setCancelable(false);
               pd.setMessage("Creating Acount");
               pd.show();
               register(root);
           }
       });
       binding.pickimg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               picImage();
           }
       });
       return  root;
    }

    private void register(View root) {
        String uName= binding.username.getText().toString().trim();
        String email= binding.email.getText().toString().trim();
        String password= binding.password.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            binding.email.setError("Email is Required");
        }else if(TextUtils.isEmpty(uName)) {
            binding.username.setError("User Name is Required");
        }else if(gender==null){
            binding.male.setError("Required");
            binding.female.setError("Required");

        }else if(password.length()<6){
            binding.password.setError("Password Length Must be greater than 6");
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Thank You", Toast.LENGTH_SHORT).show();
                                UploadUserData(uName,email,root);
                            }else{
                                Toast.makeText(getContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signinFragment);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void UploadUserData(String uName, String email,View root) {
        date = new Date();
        String userid=mAuth.getCurrentUser().getUid();
        storageRef = FirebaseStorage.getInstance().getReference("Profile Images").child(date.getTime()+"");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] fileInBytes = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(fileInBytes);
        Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();}
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri=task.getResult();
                    String url=downloadUri.toString();
                    String bio= "New Here";
                    User user = new User(uName,url,gender,email,userid,bio);
                    database.getReference()
                            .child("users")
                            .child(userid)
                            .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_homeFragment);

                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Failed Sign Up", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signinFragment);
                    pd.dismiss();
                }
            }
        });

    }

    private void picImage() {
        ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            imgUri=data.getData();
            Picasso.get().load(imgUri).into(binding.profilepic);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

