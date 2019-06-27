package com.example.devicemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.SummaryListDetailActivity;

import java.util.ArrayList;

public class RecyclerFunitureAdapter extends RecyclerView.Adapter<RecyclerFunitureAdapter.Holder> {
    String[] brand = new String[0];

    int[] available = new int[0];
    Context context;

    public RecyclerFunitureAdapter(Context context){
        this.context = context;
    }
    public void setBrand(String[] brand) {
        this.brand = brand;
    }

    public void setCount(int[] count) {
        this.count = count;
    }

    public void setAvailable(int[] available) {
        this.available = available;
    }

    int[] count = new int[0];

    @NonNull
    @Override
    public RecyclerFunitureAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_other_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.setItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SummaryListDetailActivity.class);
                intent.putExtra("Type",brand[position]);
                intent.putExtra("Count",count[position]);
                intent.putExtra("Available", available[position]);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return brand.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvBrand,tvCount, tvAvailable;

        public Holder(View itemView) {
            super(itemView);
            tvBrand = (TextView) itemView.findViewById(R.id.tvBrand);
            tvCount = (TextView) itemView.findViewById(R.id.tvcount);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
        }
        public void setItem(int position) {
            tvBrand.setText(brand[position]);
            tvCount.setText(count[position]+"");
            tvAvailable.setText(available[position] + "");
        }

    }
}
