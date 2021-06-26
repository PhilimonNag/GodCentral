package com.philimonnag.godcentral.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.philimonnag.godcentral.Model.Notifications;
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemPostBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    ArrayList<Post>postArrayList;
    private  FirebaseUser firebaseUser;

    public PostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Post post= postArrayList.get(position);
        holder.binding.postText.setText(post.getPostTitle());
        Picasso.get().load(post.getPostImg()).into(holder.binding.postImg);
        holder.binding.prayercard.setBackgroundColor(post.getClr());
       UserImg(post.getUserId(),holder.binding.profilepic,holder.binding.username);
       like(post.getPostId(), holder.binding.likePost);
       nLike(holder.binding.textlikes,post.getPostId());
       comment(post.getPostId(),holder.binding.prayedtext);
        holder.binding.likePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.binding.likePost.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                    notification(firebaseUser.getUid(),post.getPostId());
                    //amen
                }else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }

            }
        });
        holder.binding.cmmentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("postImg", post.getPostImg());
                bundle.putString("postTitle", post.getPostTitle());
                bundle.putString("postId", post.getPostId());
                bundle.putString("userId", post.getUserId());
                if(Navigation.findNavController(v).getCurrentDestination().getId()==R.id.homeFragment){
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_postDetailsFragment,bundle);

                }else{
                    Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_postDetailsFragment,bundle);
                }

            }
        });
        holder.binding.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("postImg", post.getPostImg());
                bundle.putString("postTitle", post.getPostTitle());
                bundle.putString("postId", post.getPostId());
                bundle.putString("userId", post.getUserId());
                if(Navigation.findNavController(v).getCurrentDestination().getId()==R.id.homeFragment){
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_postDetailsFragment,bundle);

                }else{
                    Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_postDetailsFragment,bundle);
                }
            }
        });
        if(firebaseUser.getUid().equals(post.getUserId())){
            holder.binding.sendPersonalMessage.setVisibility(View.GONE);
        }
        holder.binding.sendPersonalMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                FirebaseDatabase.getInstance().getReference().child("users").child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            bundle.putString("userId", user.getUid());
                            bundle.putString("name",user.getuName());
                            bundle.putString("pic",user.getUrl());
                            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_messagesFragment,bundle);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });
       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(firebaseUser.getUid().equals(post.getUserId())){
                new AlertDialog.Builder(v.getContext()).setIcon(R.drawable.ic_delete).setTitle("Delete")
                        .setMessage("Are You Sure You Want To Delete ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("posts")
                                .child(post.getPostId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("comments")
                                .child(post.getPostId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("likes")
                                .child(post.getPostId()).removeValue();
                        removeNot(post.getPostId());
                        removePicFromStorage(post.getPostImg());
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void removePicFromStorage(String postImg) {
        StorageReference reference=FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(postImg);
         reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void unused) {
                 Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull @NotNull Exception e) {
                 Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void removeNot(String postId) {
        Query query=FirebaseDatabase.getInstance().getReference().child("notifications").orderByChild("postId").equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        dataSnapshot.getRef().removeValue();
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void notification(String UserId,String Postid) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications");
        Notifications notifications= new Notifications(UserId,Postid,"Amen To Your Prayer");
        reference.push().setValue(notifications);

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemPostBinding.bind(itemView);
        }
    }
    private void UserImg(String userId, ImageView imageView, TextView textView){
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
    private void  like(String postId , ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_heart_filled);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_heart_border);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void nLike(TextView likes,String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" Amen");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void comment(String postId,TextView textView){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount()+" Prayed");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
