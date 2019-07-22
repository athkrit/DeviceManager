package com.example.devicemanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.devicemanager.R;
import com.example.devicemanager.activity.AddDeviceActivity;
import com.example.devicemanager.activity.DeviceDetailActivity;
import com.example.devicemanager.activity.ScanBarcodeActivity;
import com.example.devicemanager.adapter.ItemListAdapter;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.model.ItemEntityViewModel;
import com.example.devicemanager.model.TypeItem;
import com.example.devicemanager.room.AppDatabase;
import com.example.devicemanager.room.ItemEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;


/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class MainFragment extends Fragment implements ItemListAdapter.Holder.ItemClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Button btnAdd, btnCheck, btnSummary,downloadStatus;
    //private FloatingActionButton floatingButton;
    private android.widget.SearchView searchView;
    private boolean isFABOpen = false;
    private TextView tvLogout;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ItemListAdapter adapter, adapterNew;
    private LinearLayoutManager layoutManager;
    private LoadData loadData;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private View view;
    private ProgressBar progressBar;
    private AppDatabase database;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ItemEntityViewModel itemEntityViewModel;
    private boolean refreshStatus = true;
    private int iii = 0;
    private FloatingActionButton floatingButton;

    public MainFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        itemEntityViewModel = ViewModelProviders.of(this).get(ItemEntityViewModel.class);

        itemEntityViewModel.getOrder().observe(getViewLifecycleOwner(), new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                adapter.setList(itemEntities);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        //menuItem.expandActionView();

        final SearchView searchViewActionBar = (SearchView) menuItem.getActionView();
        searchViewActionBar.clearFocus();
        //searchViewActionBar.setIconifiedByDefault(false);
        searchViewActionBar.setPadding(0,0,20,0);
        searchViewActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                adapter.getFilter().filter(query);

                searchViewActionBar.clearFocus();
                view.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter == null) {
                    return true;
                }
                adapter.getFilter().filter(newText);
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onItemClick(View view, int position, String serial) {
        Intent intent = new Intent(getActivity(), DeviceDetailActivity.class);
        intent.putExtra("serial", serial);
        startActivity(intent);
    }

    private void init(Bundle savedInstanceState) {

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        sp = getContext().getSharedPreferences("DownloadStatus", Context.MODE_PRIVATE);
        editor = sp.edit();

        floatingButton = rootView.findViewById(R.id.fabAdd);
        floatingButton.setOnClickListener(onClickFab);
        floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.add_gradient_blue));
        recyclerView = rootView.findViewById(R.id.recyclerView);
        view = rootView.findViewById(R.id.view);
        progressBar = rootView.findViewById(R.id.spin_kit);

        loadData = new LoadData(getContext());


        layoutManager = new LinearLayoutManager(getContext());

        loadData();

        adapter = new ItemListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefresh);

    }

    private View.OnClickListener onClickFab = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
            startActivityForResult(intent, 11111);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11111) {
            if (resultCode == RESULT_OK) {
                loadData = new LoadData(getActivity());
                SuccessDialog();
                if (loadData.deleteTable() == 1) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadData();
                }
            }
        }
    }

    private void loadData() {
//        String[] typeAll = getResources().getStringArray(R.array.other_summary);
//        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Type");
//        for (int i = 0 ; i < typeAll.length ; i++) {
//            TypeItem typeItem = new TypeItem();
//            typeItem.setType(typeAll[i]);
//            typeItem.setAssetId("4");
//            databaseReference2.push().setValue(typeItem);
//        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    ItemEntity item = s.getValue(ItemEntity.class);

                    if (item != null) {
                        if (!item.getPurchasedDate().matches("") &&
                                !item.getPurchasedDate().matches("-")) {
                            item.setPurchasedDate(setDate(item.getPurchasedDate()));
                        }
                        item.setAutoId(Integer.parseInt(s.getKey()));
                        itemEntityViewModel.insert(item);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                refreshStatus = true;
                view.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
                refreshStatus = true;
                view.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String setDate(String inputDate) {
        if (inputDate.contains("GMT")) {
            inputDate = inputDate.substring(0, inputDate.indexOf("GMT")).trim();
        }
        String inputFormat = "EEE MMM dd yyyy HH:mm:ss";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(
                inputFormat, Locale.ENGLISH);
        String outputFormat = "yyyy-MM-dd";
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

    private void SuccessDialog() {
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener pullToRefresh = new androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (refreshStatus) {
                refreshStatus = false;
                if (loadData.deleteTable() == 1) {
                    loadData();
                } else {
                    refreshStatus = true;
                }
            }
        }
    };
}
