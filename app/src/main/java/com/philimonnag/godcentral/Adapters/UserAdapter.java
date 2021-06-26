package com.philimonnag.godcentral.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.philimonnag.godcentral.Model.User;
import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.databinding.ItemUserBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
   Context context;
   ArrayList<User>userArrayList;

    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.binding.Uname.setText(user.getuName());
        if(user.getBio()!=null){
            holder.binding.Ubio.setText(user.getBio());
        }else{
            holder.binding.Ubio.setText("New Here..");
        }

        Picasso.get().load(user.getUrl()).into(holder.binding.profilepic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context,ChartActivity.class);
//                intent.putExtra("userId",user.getUid());
//                intent.putExtra("name",user.getuName());
//                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString("userId", user.getUid());
                bundle.putString("name",user.getuName());
                bundle.putString("pic",user.getUrl());
                Navigation.findNavController(view).navigate(R.id. action_chatFragment_to_messagesFragment,bundle);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemUserBinding.bind(itemView);
        }
    }
}
