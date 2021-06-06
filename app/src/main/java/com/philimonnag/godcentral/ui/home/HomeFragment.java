package com.philimonnag.godcentral.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.philimonnag.godcentral.PrayerAdd;
import com.philimonnag.godcentral.Volley.MyFunctions;

import com.philimonnag.godcentral.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        String userid=mAuth.getCurrentUser().getUid();
        documentReference=db.collection("users").document(userid);

        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                binding.username.setText(value.getString("uName"));
                Picasso.get().load(value.getString("url")).into(binding.profilepic);
                Picasso.get().load(value.getString("url")).into(binding.profileImage);
//                url=value.getString("url");
//                Im=value.getString("uName");
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
                   // share=jsonObject.getString("bookname")+" "+jsonObject.getString("chapter")+":"+jsonObject.getString("verse")+"\n"+jsonObject.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(getActivity(), PrayerAdd.class));
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}