package com.example.devicemanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.DeviceDetailActivity;

import java.util.ArrayList;

public class RecyclerListDetailAdapter extends RecyclerView.Adapter<RecyclerListDetailAdapter.Holder> {
    Context context;
    ArrayList<String> brand, detail, owner, addedDate, status, key;

    public void setBrand(ArrayList<String> brand) {
        this.brand = brand;
    }

    public void setKey(ArrayList<String> key) {
        this.key = key;
    }

    public void setDetail(ArrayList<String> detail) {
        this.detail = detail;
    }

    public void setOwner(ArrayList<String> owner) {
        this.owner = owner;
    }

    public void setAddedDate(ArrayList<String> addedDate) {
        this.addedDate = addedDate;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    public RecyclerListDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.setItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DeviceDetailActivity.class);
                intent.putExtra("serial", key.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (brand == null) {
            return 0;
        } else {
            return brand.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    TextView tvBrand, tvDetail, tvOwner, tvAddedDate, tvStatus;

    class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
            tvBrand = (TextView) itemView.findViewById(R.id.tvBrand);
            tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
            tvOwner = (TextView) itemView.findViewById(R.id.tvOwner);
            tvAddedDate = (TextView) itemView.findViewById(R.id.tvAddedDate);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
        }

        @SuppressLint("ResourceAsColor")
        public void setItem(int position) {
            tvBrand.setText(brand.get(position));
            tvDetail.setText(detail.get(position));
            tvOwner.setText(owner.get(position));
            tvAddedDate.setText(addedDate.get(position));
            tvStatus.setText(status.get(position));
            if (status.get(position).matches("InUse")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            }
        }

    }

}
