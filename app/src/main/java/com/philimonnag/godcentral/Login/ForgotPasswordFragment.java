package com.philimonnag.godcentral.Login;

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
import com.google.firebase.auth.FirebaseAuth;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.FragmentForgotPasswordBinding;

import org.jetbrains.annotations.NotNull;

public class ForgotPasswordFragment extends Fragment {
  private FragmentForgotPasswordBinding binding;
  private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false);
        View root= binding.getRoot();
        binding.recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email =binding.forgotemail.getText().toString();
                if(TextUtils.isEmpty(Email)){
                    binding.forgotemail.setError("Email is Required");
                }else {
                    mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "A reset Link has sent to your registered email address", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(root).navigate(R.id.action_forgotPasswordFragment_to_signinFragment);
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
        });
        return root;
    }
}