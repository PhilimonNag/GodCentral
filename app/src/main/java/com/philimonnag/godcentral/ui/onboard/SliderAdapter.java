package com.philimonnag.godcentral.ui.onboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;


import com.philimonnag.godcentral.R;

import org.jetbrains.annotations.NotNull;

public class SliderAdapter extends PagerAdapter {
   Context context;
   LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }
    public int []Slide_images={

        R.drawable.preaching,
            R.drawable.prayer
    };
    public String[]Slide_title={
            "Preach","Prayer"
    };
    public String[]Slide_description={
            "Jesus is the way life and Truth",
            " You can send us prayer , all prayer warrior will pray for your prayer"
    };

    @Override
    public int getCount() {
        return Slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view==(ConstraintLayout)object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImage = view.findViewById(R.id.slide_image);
        TextView slideHeadings= view.findViewById(R.id.slide_heding);
        TextView slideDes=view.findViewById(R.id.slide_desc);
        slideImage.setImageResource(Slide_images[position]);
        slideHeadings.setText(Slide_title[position]);
        slideDes.setText(Slide_description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
