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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.philimonnag.godcentral.Adapters.PreachAdapter;
import com.philimonnag.godcentral.Adapters.TopStatusAdapter;
import com.philimonnag.godcentral.PreachAdd;
import com.philimonnag.godcentral.StatusActivity;
import com.philimonnag.godcentral.Volley.MyFunctions;

import com.philimonnag.godcentral.databinding.FragmentHomeBinding;
import com.philimonnag.godcentral.model.PreachModel;
import com.philimonnag.godcentral.model.Status;
import com.philimonnag.godcentral.model.UserStatus;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseDatabase database;
    DocumentReference documentReference;
    ArrayList<PreachModel>preachModelArrayList;
    PreachAdapter preachAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        database=FirebaseDatabase.getInstance();
        String userid=mAuth.getCurrentUser().getUid();
        documentReference=db.collection("users").document(userid);

        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Picasso.get().load(value.getString("url")).into(binding.profileImage);
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
                startActivity(new Intent(getActivity(), StatusActivity.class));
            }
        });

        userStatuses = new ArrayList<>();
        binding.statusList.setHasFixedSize(true);
        binding.statusList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        statusAdapter = new TopStatusAdapter(getContext(), userStatuses);
        binding.statusList.setAdapter(statusAdapter);


        preachModelArrayList = new ArrayList<>();
        binding.preaching.setHasFixedSize(true);
        binding.preaching.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        preachAdapter = new PreachAdapter(preachModelArrayList,getContext());
        binding.preaching.setAdapter(preachAdapter);
         loadPreachings();
         //loadPrayers();
      binding.addpreach.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(getActivity(), PreachAdd.class));
          }
      });
        return root;
    }




    private void loadPreachings() {
        db.collection("Preachings").orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                PreachModel preachModel = d.toObject(PreachModel.class);

                                preachModelArrayList.add(preachModel);
                            }
                            preachAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No Preach data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPrayers() {
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus status = new UserStatus();
                        status.setuName(storySnapshot.child("name").getValue(String.class));
                        status.setUrl(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}