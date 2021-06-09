package com.philimonnag.godcentral.Adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.philimonnag.godcentral.R;
import com.philimonnag.godcentral.model.PreachModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreachAdapter extends RecyclerView.Adapter<PreachAdapter.ViewHolder> {
    private ArrayList<PreachModel> preachModelArrayList;
    private Context context;

    // constructor class for our Adapter
    public PreachAdapter(ArrayList<PreachModel> preachModelArrayList, Context context) {
        this.preachModelArrayList = preachModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preach, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        PreachModel modal = preachModelArrayList.get(position);
        holder.courseNameTV.setText(modal.getpreach());
        holder.mName.setText(modal.getuName());

        // we are using Picasso to load images
        // from URL inside our image view.
        Picasso.get().load(modal.geturl()).into(holder.courseIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting on click listener
                // for our items of recycler items.
                Toast.makeText(context, "Clicked item is " + modal.getpreach(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                String[]options={"Update","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      if(i==0){
                          Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
                      }
                      if(i==1){
                          Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                      }
                    }
                }).create().show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return preachModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView courseNameTV,mName;
        private ImageView courseIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            courseNameTV = itemView.findViewById(R.id.idTVtext);
            courseIV = itemView.findViewById(R.id.idIVimage);
            mName =itemView.findViewById(R.id.mName);
        }
    }
}
