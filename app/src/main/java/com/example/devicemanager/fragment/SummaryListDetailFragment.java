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
    private RecyclerView recyclerView;
    private RecyclerListDetailAdapter recyclerListDetailAdapter, newRecyclerListDetailAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String type, brandType;
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

//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_item_list_detail,R.id.text, countries);
        ArrayAdapter<CharSequence> spinnerFilterAdapter = ArrayAdapter.createFromResource(
                Contextor.getInstance().getContext(),
                R.array.spinner_filter,
                R.layout.spinner_item_list_detail);
        spinnerFilterAdapter.setDropDownViewResource(R.layout.spinner_item_list_detail);

        ArrayAdapter<CharSequence> spinnerSortByAdapter = ArrayAdapter.createFromResource(
                Contextor.getInstance().getContext(),
                R.array.spinner_sort_by,
                R.layout.spinner_item_list_detail);
        spinnerSortByAdapter.setDropDownViewResource(R.layout.spinner_item_list_detail);

        spFilter.setAdapter(spinnerFilterAdapter);
        spSortBy.setAdapter(spinnerSortByAdapter);

        spFilter.setOnItemSelectedListener(onSpinnerSelect);
        spSortBy.setOnItemSelectedListener(onSpinnerSelect);

        layoutManager = new LinearLayoutManager(getActivity());

        progressBar = (ProgressBar) rootView.findViewById(R.id.spin_kit);
        progressDialogBackground = (View) rootView.findViewById(R.id.view);

        progressDialogBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvListDetail);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerListDetailAdapter);

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

    private void setSpinnerDefault(ArrayAdapter<CharSequence> spinnerFilterAdapter, Spinner spinner) {
        if (spinner == spFilter) {
            int spinnerPosition = spinnerFilterAdapter.getPosition("All");
            spinner.setSelection(spinnerPosition);
        } else if (spinner == spSortBy) {
            int spinnerPosition = spinnerFilterAdapter.getPosition("Date");
            spinner.setSelection(spinnerPosition);
        }
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
                    for (int i = 0; i < itemEntities.size(); i++) {
                        if (brandType.matches("-") || brandType.matches(itemEntities.get(i).getBrand().trim().toLowerCase())) {
                            String productType = itemEntities.get(i).getType().trim();
                            String productBrand = itemEntities.get(i).getBrand().trim();
                            String productDetail = itemEntities.get(i).getDetail().trim();
                            String productAddedDate = itemEntities.get(i).getPurchasedDate().trim();
                            String productOwner = itemEntities.get(i).getPlaceName().trim();
                            String productLastUpdated = itemEntities.get(i).getLastUpdated().trim();
                            String productStatus;
                            if (productOwner.matches("-")) {
                                productStatus = "Available";
                            } else {
                                productStatus = "InUse";
                            }
                            String productKey = itemEntities.get(i).getUnnamed2().trim();
                            Log.d("date", "" + productAddedDate);
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
                recyclerListDetailAdapter.setBrand(brand);
                recyclerListDetailAdapter.setDetail(detail);
                recyclerListDetailAdapter.setOwner(owner);
                recyclerListDetailAdapter.setAddedDate(addedDate);
                recyclerListDetailAdapter.setStatus(status);
                recyclerListDetailAdapter.setKey(key);
                recyclerListDetailAdapter.setLastUpdated(lastUpdated);
                recyclerListDetailAdapter.notifyDataSetChanged();

                progressDialogBackground.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private AdapterView.OnItemSelectedListener onSpinnerSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView == spFilter) {
                String filter = adapterView.getItemAtPosition(i).toString();
                switch (filter) {
                    case "All":
                        recyclerView.setAdapter(recyclerListDetailAdapter);
                        break;
                    case "Available":
                        spinnerSetRecyclerview(filter);
                        break;
                    case "InUse":
                        spinnerSetRecyclerview(filter);
                        break;
                }
            }
            if (adapterView == spSortBy) {
                String sortBy = adapterView.getItemAtPosition(i).toString();
                switch (sortBy) {
                    case "Date ▲":
                        DownloadData("DateAsc");
                        checkSpType();
                        break;
                    case "Date ▼":
                        DownloadData("DateDesc");
                        checkSpType();
                        break;
                    case "Brand ▲":
                        DownloadData("BrandAsc");
                        checkSpType();
                        break;
                    case "Brand ▼":
                        DownloadData("BrandDesc");
                        checkSpType();
                        break;
                }
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void checkSpType() {
        String filter = spFilter.getSelectedItem().toString();
        if (!filter.matches("All")) {
            spinnerSetRecyclerview(filter);
        } else {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerListDetailAdapter);
        }
    }

    private void spinnerSetRecyclerview(String spinnerStatus) {

        ArrayList<String> filterStatus = new ArrayList<String>();
        ArrayList<String> filterBrand = new ArrayList<String>();
        ArrayList<String> filterDetail = new ArrayList<String>();
        ArrayList<String> filterOwner = new ArrayList<String>();
        ArrayList<String> filterAddedDate = new ArrayList<String>();
        ArrayList<String> filterKey = new ArrayList<String>();
        for (int position = 0; position < status.size(); position++) {
            if (status.get(position).matches(spinnerStatus)) {
                filterStatus.add(status.get(position));
                filterBrand.add(brand.get(position));
                filterDetail.add(detail.get(position));
                filterOwner.add(owner.get(position));
                filterAddedDate.add(addedDate.get(position));
                filterKey.add(key.get(position));
            }
        }
        newRecyclerListDetailAdapter = new RecyclerListDetailAdapter(getContext());
        newRecyclerListDetailAdapter.setBrand(filterBrand);
        newRecyclerListDetailAdapter.setDetail(filterDetail);
        newRecyclerListDetailAdapter.setOwner(filterOwner);
        newRecyclerListDetailAdapter.setAddedDate(filterAddedDate);
        newRecyclerListDetailAdapter.setStatus(filterStatus);
        newRecyclerListDetailAdapter.setKey(filterKey);
        newRecyclerListDetailAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(newRecyclerListDetailAdapter);
    }
}
