package com.philimonnag.godcentral.ButtomNavigation;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;
import com.philimonnag.godcentral.Adapters.NotificationAdapter;
import com.philimonnag.godcentral.Model.Notifications;
import com.philimonnag.godcentral.databinding.FragmentNotificationsBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
public class NotificationFragment extends Fragment {
     private FragmentNotificationsBinding binding;
     NotificationAdapter adapter;
     ArrayList<Notifications>notifications;
     FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentNotificationsBinding.inflate(inflater,container,false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        View root = binding.getRoot();
        notifications=new ArrayList<>();
        adapter = new NotificationAdapter(getContext(),notifications);
        binding.notificationRV.setHasFixedSize(true);
        binding.notificationRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.notificationRV.setAdapter(adapter);
       loadNotifications();
        return root;
    }

    private void loadNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications");
         reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               notifications.clear();
                if(snapshot.exists()){
                    for(DataSnapshot d:snapshot.getChildren()){
                        Notifications notification=d.getValue(Notifications.class);
                        if(!notification.getUserId().equals(firebaseUser.getUid()))
                        {notifications.add(notification);}
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}