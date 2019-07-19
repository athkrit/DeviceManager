package com.example.devicemanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.DeviceDetailActivity;
import com.example.devicemanager.manager.Contextor;
import com.example.devicemanager.room.ItemEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.Holder> {

    private static ArrayList<ItemEntity> source;
    private static List<ItemEntity> list;
    private Context context;
    private Holder.ItemClickListener mClickListener;
    private List<ItemEntity> filteredList = new ArrayList<>();

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public static List<ItemEntity> getList() {
        return list;
    }

    private String itemId;

    public ItemListAdapter(Context context, List<ItemEntity> list) {
        this.context = context;
        this.list = list;
        this.source = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Contextor.getInstance().getContext())
                .inflate(R.layout.list_item_search, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        if (getItemCount() > 0 && list.get(position) != null) {
            holder.setText(position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mClickListener != null) {
//                    mClickListener.onItemClick(v, position, list.get(position).getUnnamed2());
//                }
                Intent intent = new Intent(context, DeviceDetailActivity.class);
                intent.putExtra("serial", list.get(position).getUnnamed2());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            boolean checkData = false;
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(source);

            } else {
                String[] filterPattern = charSequence.toString().toLowerCase().trim().split("\\s+");

                for (int i = 0; i < source.size(); i++) {
                    String brand = source.get(i).getBrand();
                    String type = source.get(i).getType();
                    String detail = source.get(i).getDetail();
                    String date = source.get(i).getPurchasedDate();
                    String place = source.get(i).getPlaceName();

                    String data = (brand + type + detail + date + place);

                    for (String s : filterPattern) {
                        checkData = data.toLowerCase().trim().contains(s);
                        if (!checkData) {
                            break;
                        }
                    }
                    if (checkData) {
                        filteredList.add(source.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            List<ItemEntity> posts;
            posts = (List<ItemEntity>) filterResults.values;
            if (posts != null) {
                list.addAll(posts);
            }
            notifyDataSetChanged();
        }
    };

    public void setClickListener(Holder.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvSearchType, tvSearchDetail, tvSearchName, tvSearchPurchasedDate;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvSearchType = itemView.findViewById(R.id.tvSearchType);
            tvSearchDetail = itemView.findViewById(R.id.tvSearchDetail);
            tvSearchName = itemView.findViewById(R.id.tvSearchName);
            tvSearchPurchasedDate = itemView.findViewById(R.id.tvSearchPurchasedDate);
        }

        @SuppressLint("SetTextI18n")
        public void setText(int position) {
            String brand = list.get(position).getBrand().trim();
            String detail = list.get(position).getDetail().trim();

            if (!brand.matches("-") && !checkBrand(detail, brand)) {
                tvSearchDetail.setText("(" + brand + ") " + detail);
            } else {
                tvSearchDetail.setText(detail);
            }
            tvSearchName.setText(list.get(position).getPlaceName().trim());
            tvSearchType.setText(list.get(position).getType().trim());
            tvSearchPurchasedDate.setText(setDate(list.get(position).getPurchasedDate().trim()));

        }

        public interface ItemClickListener {
            void onItemClick(View view, int position, String serial);
        }

        private boolean checkBrand(String detail, String brand) {
            return detail.toLowerCase().contains(brand.toLowerCase());
        }

        private String setDate(String inputDate) {

            String inputFormat = "yyyy-MM-dd";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(
                    inputFormat, Locale.ENGLISH);
            String outputFormat = "dd/MM/yyyy";
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                    outputFormat, Locale.ENGLISH);

            Date date;
            String str = inputDate;

            try {
                date = inputDateFormat.parse(inputDate);
                str = outputDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return str;

        }

    }
}
