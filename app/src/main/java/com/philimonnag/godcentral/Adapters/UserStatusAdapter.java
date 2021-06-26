package com.philimonnag.godcentral.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.MainActivity;
import com.philimonnag.godcentral.Model.Status;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.Model.UserStatus;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemStatusBinding;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.ViewHolder> {
    Context context;
    ArrayList<UserStatus>userStatusArrayList;
    String dateString;
    FirebaseUser firebaseUser;

    public UserStatusAdapter(Context context, ArrayList<UserStatus> userStatusArrayList) {
        this.context = context;
        this.userStatusArrayList = userStatusArrayList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_status,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserStatus userStatus =userStatusArrayList.get(position);
        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
        holder.binding.sname.setText(userStatus.getuName());
        Picasso.get().load(lastStatus.getImageUrl()).into(holder.binding.image);
        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        dateString = simpleDateFormat.format(new Date(status.getTimeStamp()));
                        myStories.add(new MyStory(status.getImageUrl(),simpleDateFormat.parse(dateString),status.getCaption()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getuName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getUrl())// Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {

                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return userStatusArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemStatusBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding= ItemStatusBinding.bind(itemView);
        }
    }
}
