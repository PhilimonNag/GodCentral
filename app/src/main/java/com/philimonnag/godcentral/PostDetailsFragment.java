package com.philimonnag.godcentral;

import android.app.PendingIntent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Adapters.CommentAdapter;
import com.philimonnag.godcentral.Model.Comments;
import com.philimonnag.godcentral.Model.Notifications;
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.databinding.FragmentPostDetailsBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class PostDetailsFragment extends Fragment {
    private FragmentPostDetailsBinding binding;
    FirebaseUser firebaseUser;
    ArrayList<Comments>commentsArrayList;
    CommentAdapter commentAdapter;
    String postId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostDetailsBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        binding.posttitle.setText(getArguments().getString("postTitle"));
        Picasso.get().load(getArguments().getString("postImg")).into(binding.postimg);
        UserImg(getArguments().getString("userId"));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        commentsArrayList = new ArrayList<>();
        commentAdapter= new CommentAdapter(getContext(),commentsArrayList);
        binding.commentsRV.setHasFixedSize(true);
        binding.commentsRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.commentsRV.setAdapter(commentAdapter);
        postId=getArguments().getString("postId");
        binding.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment(getArguments().getString("postId"));
                binding.comment.setText("");
            }
        });
       loadComments(getArguments().getString("postId"));

        return root;
    }

    private void loadComments(String postId) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                commentsArrayList.clear();
                if(snapshot.exists()){
                    for(DataSnapshot cmt:snapshot.getChildren()){
                    Comments comments = cmt.getValue(Comments.class);
                    commentsArrayList.add(comments);
                    binding.commentsRV.scrollToPosition(commentsArrayList.lastIndexOf(comments));
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setComment(String postId) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        Comments comments = new Comments(postId,firebaseUser.getUid(),binding.comment.getText().toString());
        reference.push().setValue(comments);
        notification();


    }
    private void notification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications");
        Notifications notifications= new Notifications(firebaseUser.getUid(),postId,"Prayed "+binding.comment.getText().toString());
        reference.push().setValue(notifications);

    }
    private void UserImg(String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getUrl()).into(binding.pImg);
                    binding.pName.setText(user.getuName());
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}