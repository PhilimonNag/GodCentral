package com.philimonnag.godcentral.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Model.Comments;
import com.philimonnag.godcentral.Model.Notifications;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemUserBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    ArrayList<Comments>commentsArrayList;
   FirebaseUser firebaseUser;
    public CommentAdapter(Context context, ArrayList<Comments> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Comments comments=commentsArrayList.get(position);
        holder.binding.Ubio.setText(comments.getPrayer());
        userDetails(comments.getUserId(),holder.binding.profilepic,holder.binding.Uname);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comments.getUserId().equals(firebaseUser.getUid())){
                    new AlertDialog.Builder(context).setMessage("Delete ?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Query query=FirebaseDatabase.getInstance().getReference().child("comments").
                                    child(comments.getPostId()).orderByChild("prayer")
                                    .equalTo(comments.getPrayer());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                      if(snapshot.exists()){
                                          for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                              dataSnapshot.getRef().removeValue();
                                              removeNotification(comments.getPrayer(),comments.getUserId(), comments.getPostId());
                                              notifyDataSetChanged();
                                          }
                                      }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();


                }
                return false;

            }
        });
    }

    private void removeNotification(String prayer,String userId,String postId) {
        Query query= FirebaseDatabase.getInstance().getReference().child("notifications").
               orderByChild("text").equalTo("Prayed "+prayer);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Notifications notifications= dataSnapshot.getValue(Notifications.class);
                        if(notifications.getUserId().equals(userId)&&notifications.getPostId().equals(postId))
                        {dataSnapshot.getRef().removeValue();}
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemUserBinding.bind(itemView);
        }
    }
    private void userDetails(String UserId, ImageView imageView, TextView textView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(UserId);
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

            }
        });
    }
}
