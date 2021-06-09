package com.philimonnag.godcentral.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.philimonnag.godcentral.MainActivity;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemStatusBinding;
import com.philimonnag.godcentral.model.Status;
import com.philimonnag.godcentral.model.UserStatus;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @Override
    public TopStatusViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder( TopStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);

        Picasso.get().load(lastStatus.getImageUrl()).into(holder.binding.image);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                      new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                              .setStoriesList(myStories) // Required
                              .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                              .setTitleText(userStatus.getuName()) // Default is Hidden
                              .setSubtitleText("") // Default is Hidden
                              .setTitleLogoUrl(userStatus.getUrl()) // Default is Hidden
                              .setStoryClickListeners(new StoryClickListeners() {
                                  @Override
                                  public void onDescriptionClickListener(int position) {
                                      //your action
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
        return userStatuses.size();
    }

   public  class TopStatusViewHolder extends RecyclerView.ViewHolder{
       ItemStatusBinding binding;

       public TopStatusViewHolder( View itemView) {
           super(itemView);
           binding = ItemStatusBinding.bind(itemView);
       }
   }

}