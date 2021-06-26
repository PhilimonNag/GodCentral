package com.philimonnag.godcentral.ButtomNavigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Adapters.UserAdapter;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.databinding.FragmentChatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
private FragmentChatBinding binding;
UserAdapter userAdapter;
ArrayList<User>userArrayList;
FirebaseUser Fuser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentChatBinding.inflate(inflater,container,false);
       View root = binding.getRoot();
       Fuser= FirebaseAuth.getInstance().getCurrentUser();
       userArrayList = new ArrayList<>();
       userAdapter = new UserAdapter(getContext(),userArrayList);
       binding.UserRV.setHasFixedSize(true);
       binding.UserRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
       binding.UserRV.setAdapter(userAdapter);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userArrayList.clear();
                if(snapshot.exists()){
                    for(DataSnapshot amen :snapshot.getChildren()){
                        User user = amen.getValue(User.class);
                        if(!user.getUid().equals(Fuser.getUid())){
                            userArrayList.add(user);}
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
         reference.addListenerForSingleValueEvent(valueEventListener);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText);
                return false;
            }
        });

        return root;
    }

    private void processSearch(String query) {
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userArrayList.clear();
                if(snapshot.exists()){
                    for(DataSnapshot amen :snapshot.getChildren()){
                        User user = amen.getValue(User.class);
                        assert user != null;
                        if(!user.getUid().equals(Fuser.getUid())){
                            userArrayList.add(user);}
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Query q=FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uName").startAt(query).endAt(query+"\uf8ff");
        q.addValueEventListener(valueEventListener);

    }
}