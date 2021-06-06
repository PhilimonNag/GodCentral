package com.philimonnag.godcentral.ui.onboard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.philimonnag.godcentral.ui.Login.LoginActivity;
import com.philimonnag.godcentral.databinding.ActivityOnBoardingBinding;

public class OnBoardingActivity extends AppCompatActivity {
ActivityOnBoardingBinding binding;
  private SliderAdapter sliderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        binding=ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sliderAdapter=new SliderAdapter(OnBoardingActivity.this);
        binding.viewPager.setAdapter(sliderAdapter);
        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
            }
        });
}
}