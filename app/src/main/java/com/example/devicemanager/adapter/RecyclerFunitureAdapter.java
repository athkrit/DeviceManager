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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class RecyclerFunitureAdapter extends RecyclerView.Adapter<RecyclerFunitureAdapter.Holder> {
    String[] brand;
    int[] available;
    int[] count;
    int[] total;
    Context context;

    public RecyclerFunitureAdapter(Context context) {
        this.context = context;
    }

    public void setBrand(String[] brand) {
        this.brand = brand;
    }

    public void setTotal(int[] total) {
        this.total = total;
    }

    public void setCount(int[] count) {
        this.count = count;
    }

    public void setAvailable(int[] available) {
        this.available = available;
    }


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
//        holder.setChart(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SummaryListDetailActivity.class);
                intent.putExtra("Type", brand[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brand.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvBrand,tvTotal,tvAvailable,tvActive;
//        HorizontalBarChart barChart;

        public Holder(View itemView) {
            super(itemView);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
            tvActive = itemView.findViewById(R.id.tvActive);
//            barChart = itemView.findViewById(R.id.barChart);
        }
        public void setItem(int position) {
            tvBrand.setText(""+brand[position]);
            tvTotal.setText(""+total[position]);
            tvAvailable.setText(""+available[position]);
            tvActive.setText(""+count[position]);
        }
//        private void setChart(final int position) {
//            barChart.getDescription().setEnabled(false);
//            barChart.setPinchZoom(false);
//            barChart.setClickable(false);
//            barChart.setTouchEnabled(false);
//            barChart.setDoubleTapToZoomEnabled(false);
//            barChart.getLegend().setEnabled(false);
//            barChart.animateXY(1000, 1000);
//            barChart.setDrawValueAboveBar(true);
//
//            XAxis xAxis = barChart.getXAxis();
//            xAxis.setDrawGridLines(false);
//            xAxis.setDrawAxisLine(false);
//            xAxis.setEnabled(false);
//
//            YAxis leftAxis = barChart.getAxisLeft();
//            leftAxis.setAxisMinimum(0);
//            leftAxis.setAxisMaximum(30);
//            leftAxis.setEnabled(false);
//
//            YAxis rightAxis = barChart.getAxisRight();
//            rightAxis.setDrawAxisLine(true);
//            rightAxis.setDrawGridLines(false);
//            rightAxis.setEnabled(false);
//
//            ArrayList<BarEntry> data = new ArrayList<>();
//            data.add(new BarEntry(2, RecyclerFunitureAdapter.this.available[position]));
//            data.add(new BarEntry(1, RecyclerFunitureAdapter.this.count[position]));
//            data.add(new BarEntry(0, RecyclerFunitureAdapter.this.total[position]));
//
//            BarDataSet dataSet = new BarDataSet(data, "");
//            dataSet.setValueTextSize(8);
//            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//            dataSets.add(dataSet);
//
//            BarData barData = new BarData(dataSets);
//            barChart.setData(barData);
//        }
    }
}
