package com.philimonnag.godcentral;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philimonnag.godcentral.databinding.FragmentFlashBinding;


public class FlashFragment extends Fragment {
  private FragmentFlashBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentFlashBinding.inflate(inflater,container,false);
        View root=binding.getRoot();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Navigation.findNavController(root).navigate(R.id.action_flashFragment_to_signinFragment);
            }
        },3000);
        return root;
    }
}