package com.philimonnag.godcentral.Adapters;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Model.Notifications;
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemNotificationBinding;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<Notifications>notificationsArrayList;
    public NotificationAdapter(Context context, ArrayList<Notifications> notificationsArrayList) {
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Notifications notifications = notificationsArrayList.get(position);
        holder.binding.Ubio.setText(notifications.getText());
        userImg(notifications.getUserId(),holder.binding.profilepic ,holder.binding.Uname);
        postImg(notifications.getPostId(),holder.binding.postimg);
        holder.binding.postimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(notifications.getPostId());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Post post = snapshot.getValue(Post.class);
                            Bundle bundle= new Bundle();
                            bundle.putString("postImg", post.getPostImg());
                            bundle.putString("postTitle",post.getPostTitle());
                            bundle.putString("postId", post.getPostId());
                            bundle.putString("userId", post.getUserId());
                            Navigation.findNavController(v).navigate(R.id.action_notificationFragment_to_postDetailsFragment,bundle);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
     ItemNotificationBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemNotificationBinding.bind(itemView);
        }
    }
    private void postImg(String postId, ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   Post post = snapshot.getValue(Post.class);
                    Picasso.get().load(post.getPostImg()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void userImg(String userId, ImageView imageView, TextView textView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getUrl()).into(imageView);
                    textView.setText(user.getuName());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
