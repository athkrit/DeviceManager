package com.example.devicemanager.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devicemanager.R;
import com.example.devicemanager.adapter.SummaryAdapter;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.model.ItemEntityViewModel;
import com.example.devicemanager.room.ItemEntity;
import com.getbase.floatingactionbutton.FloatingActionButton;


import java.util.Arrays;
import java.util.List;

public class SummaryFragment extends Fragment {
    private FloatingActionButton fabContainer;
    private com.google.android.material.floatingactionbutton.FloatingActionButton
            fabAll, fabDevice, fabLaptop, fabFurniture, fabOther;
    private boolean isFABOpen = false;
    private RecyclerView rvSummary;
    private SummaryAdapter summaryAdapter, summaryAdapterLaptop;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout layoutAll, layoutDevice, layoutLaptop, layoutFurniture, layoutOther;
    private View hidedView;
    private ItemEntityViewModel itemEntityViewModel;
    private LoadData loadData;
    String[] typeDevice, typeFurniture, typeOther, typeAll;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        loadData = new LoadData(getContext());
        fabContainer = rootView.findViewById(R.id.fabFilter);

        sp = getContext().getSharedPreferences("Type", Context.MODE_PRIVATE);
        editor = sp.edit();

        layoutAll = rootView.findViewById(R.id.layoutAll);
        layoutDevice = rootView.findViewById(R.id.layoutDevice);
        layoutFurniture = rootView.findViewById(R.id.layoutFurniture);
        layoutLaptop = rootView.findViewById(R.id.layoutLaptop);
        layoutOther = rootView.findViewById(R.id.layoutOther);
        hidedView = rootView.findViewById(R.id.hidedView);

        fabAll = rootView.findViewById(R.id.fabAll);
        fabDevice = rootView.findViewById(R.id.fabDevice);
        fabFurniture = rootView.findViewById(R.id.fabFurniture);
        fabLaptop = rootView.findViewById(R.id.fabLaptop);
        fabOther = rootView.findViewById(R.id.fabOther);

        fabContainer.setOnClickListener(onClickListener);
        layoutAll.setOnClickListener(onClickListener);
        layoutOther.setOnClickListener(onClickListener);
        layoutDevice.setOnClickListener(onClickListener);
        layoutLaptop.setOnClickListener(onClickListener);
        layoutFurniture.setOnClickListener(onClickListener);

        fabAll.setOnClickListener(onClickListener);
        fabLaptop.setOnClickListener(onClickListener);
        fabFurniture.setOnClickListener(onClickListener);
        fabDevice.setOnClickListener(onClickListener);
        fabOther.setOnClickListener(onClickListener);

        layoutManager = new LinearLayoutManager(getContext());
        rvSummary = rootView.findViewById(R.id.rvSummary);
        itemEntityViewModel = ViewModelProviders.of(this).get(ItemEntityViewModel.class);
        typeAll = sp.getString("allType", null).split(",");
//                new String[]{"ACCESS POINT", "ADAPTER", "AIR CONDITIONER", "APPLE CARE", "BARCODE READER", "BATTERY",
//                "BICYCLE", "CABINET", "CAMERA", "CAR", "CARD READER", "CARPET", "CART", "CASH DRAWER", "CHAIR",
//                "CHARGER", "CHROMECAST", "CLOTHES DRYERS", "COFFEE MACHINE", "COMPUTER", "COUNTER", "CURTAIN",
//                "DEVELOPER PROGRAM", "DISPLAY PORT", "DOCUMENT SHREDDER", "DONGLE", "DOOR ACCESS", "DRAWER",
//                "E-COMMERCE", "EQUIPMENT", "FAN", "FILM", "FURNITURE", "GAME", "GAS STOVE", "HDD",
//                "IMAC", "INTERIOR DECORATION", "IPAD", "IPAD COVER", "IPOD", "ITEM", "JUICE BLENDER",
//                "KEYBOARD", "KITCHEN", "LABEL PRINTER", "LAMP", "LAPTOP", "LOCKER", "MICRO SD CARD",
//                "MINIDRIVE", "MIRCROWAVE", "MOBILE PHONE", "MODEM ROUTER", "MONITOR", "NETWORK SWITCH",
//                "POCKET WIFI", "POWER BANK", "POWER SUPPLIER", "PRINTER", "PROGRAM", "REFRIGERATOR",
//                "RICE COOKER", "ROUTER", "SCANNER", "SERVER", "SERVER CABINET", "SHELVES", "SINK",
//                "SOFA", "SOFTWARE", "SOLID STATE DRIVE ", "SSD", "STOOL", "SWING", "TABLE", "TABLET",
//                "TELEPHONE", "USB", "WASHING MACHINE", "WATCH", "WATER HEATER", "WATER PUMP",
//                "WHITE BOARD", "WIRELESS", "ขาแขวน", "อุปกรณ์คอมพิวเตอร์"};

        typeDevice = sp.getString("device", null).split(",");
//                new String[]{"ACCESS POINT", "ADAPTER", "APPLE CARE", "BATTERY", "CARD READER",
//                "CHARGER", "CHROMECAST", "COMPUTER", "DEVELOPER PROGRAM", "DISPLAY PORT", "DONGLE", "E-COMMERCE",
//                "GAME", "HDD", "IMAC", "IPAD", "IPAD COVER", "IPOD", "ITEM", "KEYBOARD", "LAPTOP", "MICRO SD CARD",
//                "MINIDRIVE", "MOBILE PHONE", "MODEM ROUTER", "MONITOR", "NETWORK SWITCH", "POCKET WIFI", "POWER BANK",
//                "POWER SUPPLIER", "PROGRAM", "ROUTER", "SERVER", "SERVER CABINET", "SOFTWARE", "SOLID STATE DRIVE ",
//                "SSD", "TABLET", "USB", "WIRELESS", "ขาแขวน", "อุปกรณ์คอมพิวเตอร์"};

        typeFurniture = sp.getString("furniture", null).split(",");
//                new String[]{"AIR CONDITIONER", "BARCODE READER", "CABINET", "CAMERA", "CARPET", "CART", "CASH DRAWER",
//                "CHAIR", "CLOTHES DRYERS", "COFFEE MACHINE", "COUNTER", "CURTAIN", "DOCUMENT SHREDDER", "DOOR ACCESS", "DRAWER",
//                "EQUIPMENT", "FAN", "FILM", "FURNITURE", "GAS STOVE", "INTERIOR DECORATION", "JUICE BLENDER", "KITCHEN",
//                "LABEL PRINTER", "LAMP", "LOCKER", "MIRCROWAVE", "PRINTER", "REFRIGERATOR", "RICE COOKER", "SCANNER", "SHELVES",
//                "SINK", "SOFA", "STOOL", "SWING", "TABLE", "TABLET", "TELEPHONE", "TELEVISION", "WASHING MACHINE", "WATCH",
//                "WATER HEATER", "WATER PUMP", "WHITE BOARD"};
        String[] other = sp.getString("other", null).split(",");
        String[] building = sp.getString("building", null).split(",");

        typeOther = new String[other.length + building.length];
        for (int i = 0; i < typeOther.length; i++) {
            if (i >= other.length)
                typeOther[i] = building[i-other.length];

            else
                typeOther[i] = other[i];

        }
//                new String[]{"BUILDING", "BICYCLE", "CAR"};
        Arrays.sort(typeAll);
        Arrays.sort(typeDevice);
        Arrays.sort(typeFurniture);
        Arrays.sort(typeOther);

        getDataByType(typeAll);

    }

    private void getDataByType(final String[] type) {
        if (type == null || type.length == 0) {
            Toast.makeText(getActivity(), "Error to get ", Toast.LENGTH_SHORT).show();
            return;
        }
        summaryAdapter = new SummaryAdapter(getContext());
        final int[] typeTotal = new int[type.length];
        final int[] typeAvailable = new int[type.length];
        final int[] typeInUse = new int[type.length];
        itemEntityViewModel.getAll().observe(this, new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                for (int i = 0; i < itemEntities.size(); i++) {
                    for (int j = 0; j < type.length; j++)
                        if (itemEntities.get(i).getType().trim().matches(type[j])) {
                            String place = itemEntities.get(i).getPlaceName();
                            if (place.matches("-")) {
                                typeAvailable[j] = typeAvailable[j] + 1;
                            } else {
                                typeInUse[j] = typeInUse[j] + 1;
                            }
                            typeTotal[j] = typeTotal[j] + 1;
                            break;
                        }
                }
                summaryAdapter.setAvailable(typeAvailable);
                summaryAdapter.setBrand(null);
                summaryAdapter.setType(type);
                summaryAdapter.setCount(typeInUse);
                summaryAdapter.setTotal(typeTotal);
                summaryAdapter.notifyDataSetChanged();
                rvSummary.setLayoutManager(layoutManager);
                rvSummary.setAdapter(summaryAdapter);
            }
        });
    }

    private void getLaptop() {
        summaryAdapterLaptop = new SummaryAdapter(getContext());
        final String[] type = {"LAPTOP"};
        final String[] brand = {"Apple", "Dell", "HP", "Lenovo", "True IDC Chromebook 11", "-"};
        final int[] brandTotal = new int[brand.length];
        final int[] brandAvailable = new int[brand.length];
        final int[] brandInUse = new int[brand.length];
        itemEntityViewModel.getAll().observe(this, new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                for (int i = 0; i < itemEntities.size(); i++) {
                    for (int j = 0; j < brand.length; j++)
                        if (itemEntities.get(i).getType().trim().toLowerCase().matches(type[0].toLowerCase())) {
                            if (itemEntities.get(i).getBrand().trim().toLowerCase().matches(brand[j].trim().toLowerCase())) {
                                String place = itemEntities.get(i).getPlaceName();
                                if (place.matches("-")) {
                                    brandAvailable[j] = brandAvailable[j] + 1;
                                } else {
                                    brandInUse[j] = brandInUse[j] + 1;
                                }
                                brandTotal[j] = brandTotal[j] + 1;
                                break;
                            }
                        }
                }
                summaryAdapterLaptop.setAvailable(brandAvailable);
                summaryAdapterLaptop.setBrand(brand);
                summaryAdapterLaptop.setType(type);
                summaryAdapterLaptop.setCount(brandInUse);
                summaryAdapterLaptop.setTotal(brandTotal);
                summaryAdapterLaptop.notifyDataSetChanged();
                rvSummary.setLayoutManager(layoutManager);
                rvSummary.setAdapter(summaryAdapterLaptop);
            }
        });
    }

    private void closeFABMenu() {
        isFABOpen = false;
        hidedView.setVisibility(View.INVISIBLE);
        layoutAll.animate().translationY(0);
        layoutLaptop.animate().translationY(0);
        layoutDevice.animate().translationY(0);
        layoutFurniture.animate().translationY(0);
        layoutOther.animate().translationY(0);
        delayCloseFab();
    }

    private void showFABMenu() {
        isFABOpen = true;
        delayOpenFab();
        hidedView.setVisibility(View.VISIBLE);
        hidedView.setOnClickListener(onClickView);
        layoutAll.animate().translationY(-getResources().getDimension(R.dimen.transition_floating_1));
        layoutLaptop.animate().translationY(-getResources().getDimension(R.dimen.transition_floating_2));
        layoutDevice.animate().translationY(-getResources().getDimension(R.dimen.transition_floating_3));
        layoutFurniture.animate().translationY(-getResources().getDimension(R.dimen.transition_floating_4));
        layoutOther.animate().translationY(-getResources().getDimension(R.dimen.transition_floating_5));
    }

    private void delayCloseFab() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutAll.setVisibility(View.INVISIBLE);
                layoutDevice.setVisibility(View.INVISIBLE);
                layoutLaptop.setVisibility(View.INVISIBLE);
                layoutFurniture.setVisibility(View.INVISIBLE);
                layoutOther.setVisibility(View.INVISIBLE);
            }
        }, 200);
    }

    private void delayOpenFab() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutAll.setVisibility(View.VISIBLE);
                layoutLaptop.setVisibility(View.VISIBLE);
                layoutDevice.setVisibility(View.VISIBLE);
                layoutFurniture.setVisibility(View.VISIBLE);
                layoutOther.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == fabContainer) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            } else if (view == layoutAll || view == fabAll) {
                getDataByType(typeAll);
                closeFABMenu();
            } else if (view == layoutDevice || view == fabDevice) {
                getDataByType(typeDevice);
                closeFABMenu();
            } else if (view == layoutLaptop || view == fabLaptop) {
                getLaptop();
                closeFABMenu();
            } else if (view == layoutFurniture || view == fabFurniture) {
                getDataByType(typeFurniture);
                closeFABMenu();
            } else if (view == layoutOther || view == fabOther) {
                getDataByType(typeOther);
                closeFABMenu();
            }
        }
    };

    private View.OnClickListener onClickView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeFABMenu();
        }
    };
}
