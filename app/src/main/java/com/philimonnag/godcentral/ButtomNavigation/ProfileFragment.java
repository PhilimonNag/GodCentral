package com.philimonnag.godcentral.ButtomNavigation;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Adapters.PostAdapter;
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    User user;
    PostAdapter postAdapter;
    ArrayList<Post>postArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(inflater,container,false);
        View root= binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage("Updating...");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    binding.uEmail.setText(user.getEmail());
                    binding.uName.setText(user.getuName());
                    binding.uBio.setHint(user.getBio());
                    Picasso.get().load(user.getUrl()).into(binding.profilepicture);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(" Logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_signinFragment);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bio= binding.uBio.getText().toString();
                if(TextUtils.isEmpty(bio)){
                    binding.uBio.setError("Bio Required");
                }else{
                    pd.show();
                    User userUpdate = new User(user.getuName(), user.getUrl(), user.getGender(), user.getEmail(), user.getUid(), bio);
                    FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid())
                            .setValue(userUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        pd.dismiss();
                            Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                            binding.uBio.setHint(bio);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        postArrayList =new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postArrayList);
        binding.MyPostRv.setHasFixedSize(true);
        binding.MyPostRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.MyPostRv.setAdapter(postAdapter);
        loadMyPosts();
      return  root;
    }

    private void loadMyPosts() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    postArrayList.clear();
                    for (DataSnapshot posts:snapshot.getChildren()){
                        Post post= posts.getValue(Post.class);
                        if(user.getUid().equals(post.getUserId())){
                            postArrayList.add(post);}
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}