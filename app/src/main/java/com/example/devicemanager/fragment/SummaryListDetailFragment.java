package com.example.devicemanager.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.adapter.RecyclerListDetailAdapter;
import com.example.devicemanager.manager.Contextor;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.model.ItemEntityViewModel;
import com.example.devicemanager.room.ItemEntity;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SummaryListDetailFragment extends Fragment {
    private static final String TAG = "SummaryListDetail";
    private RecyclerView recyclerView;
    private RecyclerListDetailAdapter recyclerListDetailAdapter, newRecyclerListDetailAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String type, brandType, filterBy, sortBy;
    private ArrayList<String> brand = new ArrayList<String>();
    private ArrayList<String> detail = new ArrayList<String>();
    private ArrayList<String> owner = new ArrayList<String>();
    private ArrayList<String> addedDate = new ArrayList<String>();
    private ArrayList<String> status = new ArrayList<String>();
    private ArrayList<String> key = new ArrayList<String>();
    private ArrayList<String> lastUpdated = new ArrayList<>();
    private ProgressBar progressBar;
    private View progressDialogBackground;
    private Spinner spFilter, spSortBy;
    private LoadData loadData;
    private ItemEntityViewModel itemEntityViewModel;


    @SuppressWarnings("unused")
    public static SummaryListDetailFragment newInstance() {
        SummaryListDetailFragment fragment = new SummaryListDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary_list_detail, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        type = getArguments().getString("Type").trim();
        brandType = getArguments().getString("Brand").trim().toLowerCase();
        loadData = new LoadData(getContext());

        spFilter = rootView.findViewById(R.id.spinnerFilter);
        spSortBy = rootView.findViewById(R.id.spinnerSortBy);

        spFilter.setAdapter(setSpinnerAdapter(R.array.spinner_filter));
        spSortBy.setAdapter(setSpinnerAdapter(R.array.spinner_sort_by));
        spFilter.setOnItemSelectedListener(onSpinnerSelect);
        spSortBy.setOnItemSelectedListener(onSpinnerSelect);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListDetail);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerListDetailAdapter);

        progressBar = (ProgressBar) rootView.findViewById(R.id.spin_kit);
        progressDialogBackground = (View) rootView.findViewById(R.id.view);
        progressDialogBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortBy = "DateAsc";
    }

    private ArrayAdapter<CharSequence> setSpinnerAdapter(int array) {
        ArrayAdapter<CharSequence> spinnerFilterAdapter = ArrayAdapter.createFromResource(
                Contextor.getInstance().getContext(),
                array,
                R.layout.spinner_item_list_detail);
        spinnerFilterAdapter.setDropDownViewResource(R.layout.spinner_item_list_detail);
        return spinnerFilterAdapter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    private void DownloadData(String order) {
        brand.clear();
        detail.clear();
        owner.clear();
        addedDate.clear();
        brand.clear();
        key.clear();
        status.clear();
        lastUpdated.clear();

        recyclerListDetailAdapter = new RecyclerListDetailAdapter(getContext());

        itemEntityViewModel = ViewModelProviders.of(this).get(ItemEntityViewModel.class);
        itemEntityViewModel.selectProductByType(type, order).observe(getViewLifecycleOwner(), new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                if (itemEntities != null) {
                    label:
                    for (int i = 0; i < itemEntities.size(); i++) {

                        if (brandType.matches("-") ||
                                brandType.contains(itemEntities.get(i).getBrand().trim().toLowerCase())) {

                            Log.d(TAG, "Type: " + brandType);
                            String productType = itemEntities.get(i).getType().trim();
                            String productBrand = itemEntities.get(i).getBrand().trim();
                            String productDetail = itemEntities.get(i).getDetail().trim();
                            String productAddedDate = itemEntities.get(i).getPurchasedDate().trim();
                            String productOwner = itemEntities.get(i).getPlaceName().trim();
                            String productLastUpdated = itemEntities.get(i).getLastUpdated().trim();
                            String productKey = itemEntities.get(i).getUnnamed2().trim();
                            String productStatus;

                            if (productOwner.matches("-")) {
                                productStatus = "Available";
                            } else {
                                productStatus = "In Use";
                            }

                            if (!productOwner.matches("-") &&
                                    filterBy.matches("Available")) {
                                continue label;
                            } else if (filterBy.matches("In Use")
                                    && productOwner.matches("-")) {
                                continue label;
                            } else {
                                brand.add(productBrand);
                                detail.add(productDetail);
                                owner.add(productOwner);
                                addedDate.add(productAddedDate);
                                status.add(productStatus);
                                key.add(productKey);
                                lastUpdated.add(productLastUpdated);
                            }
                        }
                    }
                    Log.d(TAG, "Loaded item size: " + itemEntities.size());
                }
                recyclerListDetailAdapter.setBrand(brand);
                recyclerListDetailAdapter.setDetail(detail);
                recyclerListDetailAdapter.setOwner(owner);
                recyclerListDetailAdapter.setAddedDate(addedDate);
                recyclerListDetailAdapter.setStatus(status);
                recyclerListDetailAdapter.setKey(key);
                recyclerListDetailAdapter.setLastUpdated(lastUpdated);

                recyclerView.setAdapter(recyclerListDetailAdapter);
                recyclerListDetailAdapter.notifyDataSetChanged();

                progressDialogBackground.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private AdapterView.OnItemSelectedListener onSpinnerSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String filter = adapterView.getItemAtPosition(i).toString();
            if (adapterView == spFilter) {
                filterBy = filter;
                if (filter.matches("All") || filter.matches("In Use") ||
                        filter.matches("Available")){
                    DownloadData(sortBy);
                }
            }
            if (adapterView == spSortBy) {
                Log.d("test2407", filterBy);
                switch (filter) {
                    case "Date ▲":
                        sortBy = "DateAsc";
                        DownloadData("DateAsc");
                        break;
                    case "Date ▼":
                        sortBy = "DateDesc";
                        DownloadData("DateDesc");
                        break;
                    case "Brand ▲":
                        sortBy = "BrandAsc";
                        DownloadData("BrandAsc");
                        break;
                    case "Brand ▼":
                        sortBy = "BrandDesc";
                        DownloadData("BrandDesc");
                        break;
                }
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
}
