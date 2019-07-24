package com.example.devicemanager.adapter;

import android.annotation.SuppressLint;
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
import com.example.devicemanager.activity.DeviceDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RecyclerListDetailAdapter extends RecyclerView.Adapter<RecyclerListDetailAdapter.Holder> {
    private Context context;
    private ArrayList<String> brand;
    private ArrayList<String> detail;
    private ArrayList<String> owner;
    private ArrayList<String> addedDate;
    private ArrayList<String> status;
    private ArrayList<String> key;
    private ArrayList<String> lastUpdated;
    private boolean btnClick = true;

    public void setBrand(ArrayList<String> brand) {
        this.brand = brand;
        notifyDataSetChanged();
    }

    public void setKey(ArrayList<String> key) {
        this.key = key;
        notifyDataSetChanged();
    }

    public void setDetail(ArrayList<String> detail) {
        this.detail = detail;
        notifyDataSetChanged();
    }

    public void setOwner(ArrayList<String> owner) {
        this.owner = owner;
        notifyDataSetChanged();
    }

    public void setAddedDate(ArrayList<String> addedDate) {
        this.addedDate = addedDate;
        notifyDataSetChanged();
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public void setLastUpdated(ArrayList<String> lastUpdated) {
        this.lastUpdated = lastUpdated;
        notifyDataSetChanged();
    }


    public RecyclerListDetailAdapter(Context context) {
        this.context = context;
        notifyDataSetChanged();
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
                if(!btnClick){
                    return;
                }
                btnClick=false;
                Intent intent = new Intent(context, DeviceDetailActivity.class);
                intent.putExtra("serial", key.get(position));
                context.startActivity(intent);
                btnClick=true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (brand == null) {
            return 0;
        }
        if (brand.size() == 0) {
            return 0;
        }
        return brand.size();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private TextView tvBrand, tvDetail, tvOwner, tvAddedDate, tvStatus, tvLastUpdated;

    class Holder extends RecyclerView.ViewHolder {

        Holder(View itemView) {
            super(itemView);
            tvBrand = (TextView) itemView.findViewById(R.id.tvBrand);
            tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
            tvOwner = (TextView) itemView.findViewById(R.id.tvOwner);
            tvAddedDate = (TextView) itemView.findViewById(R.id.tvAddedDate);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvLastUpdated = itemView.findViewById(R.id.tvLastUpdate);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        public void setItem(int position) {

            if (!brand.get(position).matches("-") &&
                    detail.get(position).toLowerCase().contains(brand.get(position).toLowerCase())) {
                tvBrand.setText(detail.get(position));
            } else if (brand.get(position).matches("-")) {
                tvBrand.setText(detail.get(position));
            } else {
                tvBrand.setText(brand.get(position));
                tvDetail.setText(detail.get(position));
            }

            if (owner.get(position).matches("-")) {
                tvOwner.setText("no owner");
            } else {
                tvOwner.setText(owner.get(position));
            }

            String[] date = setDate(addedDate.get(position), lastUpdated.get(position)).split(",");
            tvAddedDate.setText(date[0]);
            tvLastUpdated.setText(checkLastUpdate(date[1], date[2], date[3]));
            tvStatus.setText(" Updated: " +  status.get(position));

            if (status.get(position).matches("InUse")) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            }
        }

        private String setDate(String inputDate, String lastUpdatedDate) {

            if (lastUpdatedDate.matches("-")){
                lastUpdatedDate = inputDate;
            }

            String inputFormat = "yyyy-MM-dd";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(
                    inputFormat, Locale.ENGLISH);
            String outputFormat = "dd MMMM yyyy";
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                    outputFormat, Locale.ENGLISH);
            SimpleDateFormat outputDFormat = new SimpleDateFormat(
                    "dd", Locale.ENGLISH);
            SimpleDateFormat outputMFormat = new SimpleDateFormat(
                    "MM", Locale.ENGLISH);
            SimpleDateFormat outputYFormat = new SimpleDateFormat(
                    "yyyy", Locale.ENGLISH);

            Date date, updatedDate;
            String str = inputDate;
            String d = "0";
            String m = "0";
            String y = "0";

            try {
                date = inputDateFormat.parse(inputDate);
                updatedDate = inputDateFormat.parse(lastUpdatedDate);
                if (date != null) {
                    str = outputDateFormat.format(date);
                }
                if (updatedDate != null){
                    d = outputDFormat.format(updatedDate);
                    m = outputMFormat.format(updatedDate);
                    y = outputYFormat.format(updatedDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return str + "," + d + "," + m + "," + y;

        }

        private String checkLastUpdate(String d, String m, String y) {

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            int year = calendar.get(Calendar.YEAR) - Integer.parseInt(y);

            if (year == 1) {
                return "last year";
            } else if (year > 1) {
                return year + " years ago";
            }

            int month = calendar.get(Calendar.MONTH) - Integer.parseInt(m);
            if (month == 1) {
                return "last month";
            } else if (month > 1) {
                return month + " month ago";
            }

            int date = calendar.get(Calendar.DATE) - Integer.parseInt(d);
            if (date == 1) {
                return "yesterday";
            } else if (date > 1) {
                return date + " days ago";
            } else if (date == 0) {
                return "today";
            } else {
                return "";
            }
        }

    }

}
