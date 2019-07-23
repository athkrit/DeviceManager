package com.example.devicemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.SummaryListDetailActivity;
import com.example.devicemanager.manager.Contextor;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.Holder> {
    private Context context;
    private int[] available,count,total;
    private String[] brand,type;

    public SummaryAdapter(Context context) {
        this.context = context;

    }

    public void setBrand(String[] brand) {
        this.brand = brand;
        notifyDataSetChanged();
    }

    public void setTotal(int[] total) {
        this.total = total;
        notifyDataSetChanged();
    }

    public void setCount(int[] count) {
        this.count = count;
        notifyDataSetChanged();
    }

    public void setAvailable(int[] available) {
        this.available = available;
        notifyDataSetChanged();
    }

    public void setType(String[] type) {
        this.type = type;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SummaryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_other_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.setItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.length > 1) {
                    Intent intent = new Intent(context, SummaryListDetailActivity.class);
                    intent.putExtra("Type", type[position]);
                    intent.putExtra("Brand", "-");
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, SummaryListDetailActivity.class);
                    intent.putExtra("Type", type[0]);
                    intent.putExtra("Brand", brand[position]);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (type == null)
            return 0;
        if (type.length == 1) {
            return brand.length;
        }
        return type.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvBrand, tvTotal, tvAvailable, tvActive, tvType;

        Holder(View itemView) {
            super(itemView);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
            tvActive = itemView.findViewById(R.id.tvActive);
            tvType = itemView.findViewById(R.id.tvType);
        }

        public void setItem(int position) {
            if (brand != null) {
                tvBrand.setText("" + brand[position]);
            }
            tvTotal.setText("" + total[position]);
            tvAvailable.setText("" + available[position]);
            tvActive.setText("" + count[position]);
            if (type.length <= 1) {
                tvType.setText(type[0]);
            } else {
                tvType.setText("" + type[position]);
            }
        }
    }
}
