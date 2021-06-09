package com.philimonnag.godcentral.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemStatusBinding;
import com.philimonnag.godcentral.model.Status;
import com.philimonnag.godcentral.model.UserStatus;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.ViewHolder> {
    Context context;
    ArrayList<UserStatus>userStatusArrayList;


    public UserStatusAdapter(Context context, ArrayList<UserStatus> userStatusArrayList) {
        this.context = context;
        this.userStatusArrayList = userStatusArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public UserStatusAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_status,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserStatusAdapter.ViewHolder holder, int position) {
        UserStatus userStatus =userStatusArrayList.get(position);
      //  Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
        Picasso.get().load(userStatus.getUrl()).into(holder.binding.image);
//       holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
//        holder.binding.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return userStatusArrayList.size();
    }
   interface getStatuse{

   }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemStatusBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding= ItemStatusBinding.bind(itemView);
        }
    }
}
