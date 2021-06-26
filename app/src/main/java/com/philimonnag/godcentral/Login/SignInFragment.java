package com.philimonnag.godcentral.Login;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.FragmentSigninBinding;

import org.jetbrains.annotations.NotNull;


public class SignInFragment extends Fragment {
private FragmentSigninBinding binding;
private FirebaseAuth mAuth;
private ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater,container,false);
        View root= binding.getRoot();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
         mAuth= FirebaseAuth.getInstance();
       binding.signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Navigation.findNavController(root).navigate(R.id.action_signinFragment_to_signUpFragment);
           }
       });
       binding.forgotpassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Navigation.findNavController(root).navigate(R.id.action_signinFragment_to_forgotPasswordFragment);
           }
       });
       binding.signin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               pd.show();
               String Email=binding.email.getText().toString().trim();
               String Password=binding.password.getText().toString().trim();
               if(TextUtils.isEmpty(Email)){
                   binding.email.setError("Email is required");
               }else if(Password.length()<6){
                   binding.password.setError("Password length must be grater than 6");
               }else {
                   mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               pd.dismiss();
                               Navigation.findNavController(root).navigate(R.id.action_signinFragment_to_homeFragment);
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull @NotNull Exception e) {
                           pd.dismiss();
                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }
       });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            Navigation.findNavController(getView()).navigate(R.id.action_signinFragment_to_homeFragment);
        }
    }
}