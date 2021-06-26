package com.philimonnag.godcentral.ButtomNavigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Adapters.PostAdapter;
import com.philimonnag.godcentral.Adapters.UserStatusAdapter;
import com.philimonnag.godcentral.Model.Post;
import com.philimonnag.godcentral.Model.Status;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.Model.UserStatus;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.Volley.MyFunctions;
import com.philimonnag.godcentral.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseUser firebaseUser;
    String share;
    PostAdapter postAdapter;
    ArrayList<Post>postArrayList;
    User user;
    UserStatusAdapter statusAdapter;
    ArrayList<UserStatus>statuses;
    ArrayList<Status>statuse;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentHomeBinding.inflate(inflater,container,false);
        View root=binding.getRoot();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getUrl()).into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyFunctions myFunctions = new MyFunctions(getContext());
        myFunctions.getBibleVerse( new MyFunctions.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Someting Wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    binding.book.setText(jsonObject.getString("bookname")+" "+jsonObject.getString("chapter")+":"+jsonObject.getString("verse"));
                    binding.verse.setText(jsonObject.getString("text"));

                    share=jsonObject.getString("bookname")+" "+jsonObject.getString("chapter")+":"+jsonObject.getString("verse")+"\n"+jsonObject.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
       binding.sharebibleverse.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent sendIntent = new Intent();
               sendIntent.setAction(Intent.ACTION_SEND);
               sendIntent.putExtra(Intent.EXTRA_TEXT, share);
               sendIntent.setType("text/plain");
               Intent shareIntent = Intent.createChooser(sendIntent, null);
               startActivity(shareIntent);
           }
       });
       binding.postadd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_addPostFragment);
           }
       });
        postArrayList = new ArrayList<>();
        binding.postRV.setHasFixedSize(true);
        binding.postRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        postAdapter = new PostAdapter(getContext(),postArrayList);
        binding.postRV.setAdapter(postAdapter);
        loadPost();
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_statusFragment);
            }
        });
        binding.postup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_addPostFragment);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_exit)
                    .setMessage("Do you want to exit ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                         getActivity().finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        statuses = new ArrayList<>();
        binding.statusList.setHasFixedSize(true);
        binding.statusList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        statusAdapter= new UserStatusAdapter(getContext(),statuses);
        binding.statusList.setAdapter(statusAdapter);
        loadPrayers();

        return root;
    }

    private void loadPost() {
       DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("posts");
       reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   postArrayList.clear();
                    for (DataSnapshot posts:snapshot.getChildren()){
                       Post post= posts.getValue(Post.class);
                        if(user.getGender().equals(post.getPrivacy())||post.getPrivacy().equals("everyone")){
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
    private void loadPrayers() {
//
//        Query query= FirebaseDatabase.getInstance().getReference().child("stories");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    statuses.clear();
//                    for(DataSnapshot stories:snapshot.getChildren()){
//                        UserStatus userStatus = new UserStatus();
//                        userStatus.setUrl(stories.child("url").getValue(String.class));
//                        userStatus.setuName(stories.child("uName").getValue(String.class));
//                        userStatus.setLastUpdated(stories.child("lastUpdated").getValue(long.class));
//                        statuse =new ArrayList<>();
//                        for(DataSnapshot st: stories.child("statuses").getChildren()) {
//                            Status status = st.getValue(Status.class);
//                            assert status != null;
//                            if (status.getPrivacy().equals(user.getGender()) || status.getPrivacy().equals("everyone")) {
//                                statuse.add(status);
//                            }
//                        }
//                        if(!statuse.isEmpty())
//                        {userStatus.setStatuses(statuse);
//                        statuses.add(userStatus);}
//                    }
//                    statusAdapter.notifyDataSetChanged();
//               }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        FirebaseDatabase.getInstance().getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    statuses.clear();
                    for(DataSnapshot stories:snapshot.getChildren()){
                        UserStatus userStatus = new UserStatus();
                        userStatus.setUrl(stories.child("url").getValue(String.class));
                        userStatus.setuName(stories.child("uName").getValue(String.class));
                        userStatus.setLastUpdated(stories.child("lastUpdated").getValue(long.class));
                        statuse =new ArrayList<>();
                        for(DataSnapshot st: stories.child("statuses").getChildren()){
                            Status status=st.getValue(Status.class);
                            statuse.add(status);
                        }
                        userStatus.setStatuses(statuse);
                        statuses.add(userStatus);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}

