package com.example.devicemanager.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.ScanBarCodeAddDeviceActivity;
import com.example.devicemanager.manager.Contextor;
import com.example.devicemanager.model.DataItem;
import com.example.devicemanager.model.ItemEntityViewModel;
import com.example.devicemanager.model.TypeItem;
import com.example.devicemanager.room.ItemEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class AddDeviceFragment extends Fragment {

    private static final String TAG = "LogAddDevice";
    private Spinner spType, spTypeList, spBranch;
    private EditText etOwnerName, etSerialNumber, etDeviceDetail, etDatePicker,
            etOwnerId, etBrand, etDeviceModel, etDevicePrice, etNote, etQuantity,
            etPurchasePrice, etForwardDepreciation, etDepreciationRate, etDepreciationinYear,
            etAccumulateDepreciation, etForwardedBudget, etWarranty, etOtherType;
    private Button btnShowMore;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;
    private String selected, lastKey, itemId, serial, abbreviation, type, unnamed2, YY;
    private int path, category, branch, order, countDevice = 1, quntity = 1, updatedKey = 0;
    private ProgressBar progressBar;
    private View progressDialogBackground;
    private DatabaseReference databaseReference;
    private TextView tvItemId;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private LinearLayout moreData;
    private Boolean clickMore = false;
    private List<ItemEntity> itemEntity;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ItemEntity itemSave;
    private SimpleDateFormat dateFormat;
    private Date dateCheck;
    private ItemEntityViewModel itemEntityViewModel;
    private ArrayList<ItemEntity> getItemEntity;
    private String[] deviceAndAccessories, furniture, other, building, allType;
    private TextInputLayout etOtherTypeLayout;
    private boolean hasOtherType = false;

    public static AddDeviceFragment newInstances() {
        AddDeviceFragment fragment = new AddDeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_edit_detail, container, false);
        initInstances(view, savedInstanceState);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_device, menu);
        MenuItem menuItem = menu.findItem(R.id.action_check);
        menuItem.expandActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_check) {
            etOtherType.setText(etOtherType.getText().toString().toUpperCase());
            hideKeyboardFrom(getContext(), getView());
            getUpdateKey();
            showAlertDialog("save");
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                serial = data.getStringExtra("serial");
                checkSerial();
                etSerialNumber.setText(serial);
            }
        }
    }

    private void initInstances(View view, Bundle savedInstanceState) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        dateCheck = new Date();

        spBranch = view.findViewById(R.id.spinnerBranch);
        spType = view.findViewById(R.id.spinnerDeviceType);
        spTypeList = view.findViewById(R.id.spinnerDeviceTypeList);

        sp = Contextor.getInstance().getContext()
                .getSharedPreferences("Type", Context.MODE_PRIVATE);

        building = setStringArray("building");
        deviceAndAccessories = setStringArray("device");
        furniture = setStringArray("furniture");
        other = setStringArray("other");
        allType = sp.getString("allType", "").split(",");

        setSpinnerFromResource(R.array.spinner_branch, spBranch);
        setSpinnerFromResource(R.array.spinner_type_device, spType);

        itemEntityViewModel = ViewModelProviders.of(this).get(ItemEntityViewModel.class);
        tvItemId = view.findViewById(R.id.tvItemId);
        etOwnerName = view.findViewById(R.id.etOwnerName);
        etSerialNumber = view.findViewById(R.id.etSerialNumber);
        etDeviceDetail = view.findViewById(R.id.etDeviceDetail);
        etDatePicker = view.findViewById(R.id.etPurchaseDate);
        etOwnerId = view.findViewById(R.id.etOwnerId);
        etBrand = view.findViewById(R.id.etDeviceBrand);
        etDeviceModel = view.findViewById(R.id.etDeviceModel);
        etDevicePrice = view.findViewById(R.id.etPrice);
        etNote = view.findViewById(R.id.etNote);
        etPurchasePrice = view.findViewById(R.id.etDevicePurchasePrice);
        etQuantity = view.findViewById(R.id.etQuantity);
        btnShowMore = view.findViewById(R.id.btnShowMore);
        moreData = view.findViewById(R.id.hidedLayout);
        etForwardDepreciation = view.findViewById(R.id.etForwardDepreciation);
        etDepreciationRate = view.findViewById(R.id.etDepreciationRate);
        etDepreciationinYear = view.findViewById(R.id.etDepreciationinYear);
        etAccumulateDepreciation = view.findViewById(R.id.etAccumulateDepreciation);
        etForwardedBudget = view.findViewById(R.id.etForwardedBudget);
        etWarranty = view.findViewById(R.id.etWarranty);
        etOtherTypeLayout = view.findViewById(R.id.etOtherTypeLayout);
        etOtherType = view.findViewById(R.id.etOtherType);
        btnShowMore.setOnClickListener(clickListener);
        etPurchasePrice.setOnFocusChangeListener(onFocusChangeListener);
        etDevicePrice.setOnFocusChangeListener(onFocusChangeListener);

        calendar = Calendar.getInstance(TimeZone.getDefault());
        this.date = onDateSet;
        etDatePicker.setOnClickListener(onClickDate);

        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        progressDialogBackground = (View) view.findViewById(R.id.view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");

        etSerialNumber.setOnTouchListener(onTouchScan);

        itemId = getArguments() != null ? getArguments().getString("Serial") : null;
        if (itemId != null) {
            if (itemId.length() < 14) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                etQuantity.setVisibility(View.INVISIBLE);
                setData();
            }
        }
        itemEntityViewModel.getAll().observe(getViewLifecycleOwner(), new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                getItemEntity = new ArrayList<>(itemEntities);
            }
        });

    }

    private String[] setStringArray(String str) {
        String[] buildingRef = sp.getString(str, "").split(",");
        String[] setType = new String[buildingRef.length + 1];

        Log.d(TAG, "building Ref: " + buildingRef[0]+ " size: " + buildingRef.length);

        for (int i = 0; i < setType.length; i++) {
            if (i == buildingRef.length)
                setType[i] = "OTHER";
            else
                setType[i] = buildingRef[i];
        }
        return setType;
    }

    private void setData() {
        tvItemId.setText(itemId);
        setSpinnerPositionFromResource(R.array.spinner_type_device, spType, Integer.parseInt(itemId.substring(6, 7)), null);
        String spinnerName;
        spinnerName = itemId.substring(8, 11);
        switch (Integer.parseInt(itemId.substring(6, 7))) {
            case 1:
                setSpinnerPositionFromSp(building, spTypeList, -1, spinnerName);
                break;
            case 2:
                setSpinnerPositionFromSp(deviceAndAccessories, spTypeList, -1, spinnerName);
                break;
            case 3:
                setSpinnerPositionFromSp(furniture, spTypeList, -1, spinnerName);
                break;
            case 4:
                setSpinnerPositionFromSp(other, spTypeList, -1, spinnerName);
                break;
        }
        spType.setEnabled(false);
        spTypeList.setEnabled(false);

        itemEntityViewModel = ViewModelProviders.of(this).get(ItemEntityViewModel.class);

        itemEntityViewModel.selectData(itemId).observe(getViewLifecycleOwner(), new Observer<List<ItemEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                itemEntity = itemEntities;
                if (itemEntity.size() == 0) {
                    return;
                }
                setSpinnerPositionFromResource(R.array.spinner_branch,
                        spBranch,
                        Integer.parseInt(itemEntity.get(0).getBranchCode()),
                        null);

                lastKey = "" + itemEntities.get(0).getAutoId();
                etOwnerId.setText(itemEntity.get(0).getPlaceId());
                etOwnerName.setText(itemEntity.get(0).getPlaceName());
                etBrand.setText(itemEntity.get(0).getBrand());
                etSerialNumber.setText(itemEntity.get(0).getSerialNo());
                etDeviceDetail.setText(itemEntity.get(0).getDetail());
                etDeviceModel.setText(itemEntity.get(0).getModel());
                etDevicePrice.setText(itemEntity.get(0).getPrice());
                etPurchasePrice.setText(itemEntity.get(0).getPurchasedPrice());
                etDatePicker.setText(setDate(itemEntity.get(0).getPurchasedDate(), "date"));
                etNote.setText(itemEntity.get(0).getNote());
                etForwardDepreciation.setText(itemEntity.get(0).getForwardDepreciation());
                etDepreciationRate.setText(itemEntity.get(0).getDepreciationRate());
                etDepreciationinYear.setText(itemEntity.get(0).getDepreciationYear());
                etAccumulateDepreciation.setText(itemEntity.get(0).getAccumulatedDepreciation());
                etForwardedBudget.setText(itemEntity.get(0).getForwardedBudget());
                etWarranty.setText(itemEntity.get(0).getWarrantyDate());
            }
        });
    }

    private void setSpinnerFromSp(String[] spinnerlist, Spinner spinner) {
        spinnerAdapter = new ArrayAdapter<CharSequence>(
                getActivity(),
                R.layout.spinner_item,
                spinnerlist);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(onSpinnerSelect);
    }

    private void setSpinnerFromResource(int spinnerlist, Spinner spinner) {
        spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(),
                spinnerlist,
                R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(onSpinnerSelect);
    }

    private void setSpinnerPositionFromSp(String[] spinerlist, Spinner spinner, int position, String spinerName) {
        if (position == -1) {
            spinnerAdapter = new ArrayAdapter<CharSequence>(
                    getActivity(),
                    R.layout.spinner_item,
                    spinerlist);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(spinnerAdapter);

            List<String> list = Arrays.asList(spinerlist);
            int spinnerPosition = 0;

            for (String str : list) {
                if (str.contains(spinerName)) {
                    spinnerPosition = list.indexOf(str);
                }
            }
            spinner.setSelection(spinnerPosition, true);
        } else {
            spinner.setSelection(position - 1);
        }
    }

    private void setSpinnerPositionFromResource(int spinerlist, Spinner spinner, int position, String spinerName) {
        if (position == -1) {
            spinnerAdapter = ArrayAdapter.createFromResource(
                    getContext(),
                    spinerlist,
                    R.layout.spinner_item
            );
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(spinnerAdapter);

            List<String> list = Arrays.asList(getActivity().getResources().getStringArray(spinerlist));
            int spinnerPosition = 0;

            for (String str : list) {
                if (str.contains(spinerName)) {
                    spinnerPosition = list.indexOf(str);
                }
            }
            spinner.setSelection(spinnerPosition, true);
        } else {
            spinner.setSelection(position - 1);
        }

    }

    private void showAlertDialog(final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int dialogMsg = 0;
        switch (type) {
            case "save":
                dialogMsg = R.string.dialog_msg_confirm;
                break;
            case "serial":
                dialogMsg = R.string.dialog_msg_check_serial;
                builder.setTitle(R.string.dialog_msg_head_check_serial);
                break;
        }

        if (itemId == null) {
            lastKey = getLastKey();
        }

        builder.setMessage(dialogMsg).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.matches("save") && checkForm()) {
                    YY = etDatePicker.getText().toString().substring(8, 10);

                    if (tvItemId.getText().toString().matches("Item Id")) {
                        order = 1;
                        String form = "DGO" + YY + branch + category;

                        for (int i = 0; i < getItemEntity.size(); i++) {
                            if (getItemEntity.get(i).getUnnamed2().toString().contains(form))
                                order++;
                        }
                        int count = 0;
                        try {
                            count = Integer.parseInt(etQuantity.getText().toString());
                            quntity = count;
                        } catch (NumberFormatException num) {
                            Toast.makeText(getContext(), "" + num, Toast.LENGTH_SHORT).show();
                        }
                        if (hasOtherType) {
                            addType();
                        }
                        for (int i = 0; i < count; i++) {
                            setUpdatedId(lastKey);
                            saveData();
                            int key = Integer.parseInt(lastKey) + 1;
                            lastKey = key + "";
                            order++;
                        }

                    } else {
                        progressDialogBackground.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        unnamed2 = tvItemId.getText().toString();
                        updateData();
                    }

                } else if (type.matches("serial")) {
                    Toast.makeText(getActivity(), "Already has this item", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(getContext(), DeviceDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();*/
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();*/
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void updateData() {
        final int autoId = itemEntity.get(0).getAutoId();
        String date = etDatePicker.getText().toString();
        String order = tvItemId.getText().toString().substring(11);
        DataItem item = new DataItem("ID", etOwnerId.getText().toString(), etOwnerName.getText().toString(),
                etBrand.getText().toString(), etSerialNumber.getText().toString(), etDeviceModel.getText().toString(),
                etDeviceDetail.getText().toString(), etDevicePrice.getText().toString(), etPurchasePrice.getText().toString(),
                setDate(date, "date"), etNote.getText().toString(), type, tvItemId.getText().toString(), etForwardDepreciation.getText().toString(),
                etDepreciationRate.getText().toString(), etDepreciationinYear.getText().toString(),
                etAccumulateDepreciation.getText().toString(), etForwardedBudget.getText().toString(), "" + YY,
                getUnnamed2().substring(3), "" + category, "" + branch, "-",
                "-", "" + dateFormat.format(dateCheck).toString(), "" + order,
                "" + abbreviation, "-", "DGO", etWarranty.getText().toString());

        databaseReference.child(lastKey).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setUpdatedId(lastKey);
                    itemEntityViewModel.update(branch + "", dateFormat.format(dateCheck), etOwnerName.getText().toString(), etOwnerId.getText().toString(),
                            etBrand.getText().toString(), etSerialNumber.getText().toString(), etDeviceDetail.getText().toString(),
                            etDeviceModel.getText().toString(), etWarranty.getText().toString(), etPurchasePrice.getText().toString(),
                            setDate(etDatePicker.getText().toString(), "db"), etDevicePrice.getText().toString(), etNote.getText().toString(),
                            etForwardDepreciation.getText().toString(), etDepreciationRate.getText().toString(),
                            etDepreciationinYear.getText().toString(), etAccumulateDepreciation.getText().toString(),
                            etForwardedBudget.getText().toString(), autoId);
                    Intent intentBack = new Intent();
                    intentBack.putExtra("itemId", itemEntity.get(0).getUnnamed2());
                    getActivity().setResult(RESULT_OK, intentBack);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    progressDialogBackground.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void saveData() {
        progressDialogBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String date = setDate(etDatePicker.getText().toString(), "date");
        //TODO:รหัสทรัพสิน ID
        DataItem item = new DataItem("ID", etOwnerId.getText().toString(), etOwnerName.getText().toString(),
                etBrand.getText().toString(), etSerialNumber.getText().toString(), etDeviceModel.getText().toString(),
                etDeviceDetail.getText().toString(), etDevicePrice.getText().toString(), etPurchasePrice.getText().toString(),
                setDate(date, "date"), etNote.getText().toString(), type, getUnnamed2(), etForwardDepreciation.getText().toString(),
                etDepreciationRate.getText().toString(), etDepreciationinYear.getText().toString(),
                etAccumulateDepreciation.getText().toString(), etForwardedBudget.getText().toString(), "" + YY,
                getUnnamed2().substring(3), "" + category, "" + branch, "-",
                "-", "" + dateFormat.format(dateCheck).toString(), "" + setOrderFormat(order),
                "" + abbreviation, "-", "DGO", etWarranty.getText().toString());

        itemSave = new ItemEntity(getItemEntity.size(), getUnnamed2(), type, etDeviceDetail.getText().toString(),
                etSerialNumber.getText().toString(), etOwnerName.getText().toString(), setDate(date, "db"), etNote.getText().toString(),
                "-", etOwnerId.getText().toString(), getUnnamed2().substring(3), etDevicePrice.getText().toString(),
                etDeviceModel.getText().toString(), etDepreciationRate.getText().toString(), "ID", etBrand.getText().toString(),
                abbreviation, setOrderFormat(order) + "", "-", YY + "", "DGO", "-",
                etForwardedBudget.getText().toString(), etAccumulateDepreciation.getText().toString(),
                etWarranty.getText().toString(), etDepreciationinYear.getText().toString(), branch + "",
                "" + category, etPurchasePrice.getText().toString(),
                etForwardDepreciation.getText().toString(), dateFormat.format(dateCheck));
        if (lastKey != null) {
            databaseReference.child(lastKey).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (countDevice == quntity) {
                            itemEntityViewModel.insert(itemSave);
                            Toast.makeText(getActivity(), "Complete!", Toast.LENGTH_SHORT).show();
                            progressDialogBackground.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent();
                            intent.putExtra("itemId", itemSave.getUnnamed2());
                            getActivity().setResult(RESULT_OK, intent);
                            getActivity().finish();
                        }
                        countDevice++;
                    } else {
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialogBackground.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private void addType() {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Type");
        Query query = databaseReference2.orderByKey().limitToLast(1);
        final int[] typeKey = new int[1];
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int key = 0;
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    try {
                        key = Integer.parseInt(s.getKey()) + 1;
                    } catch (NumberFormatException e) {
                        key = 999;

                    }
                }
                typeKey[0] = key + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TypeItem typeItem = new TypeItem();
        typeItem.setType(type);
        typeItem.setAssetId(category + "");
        databaseReference2.child("" + typeKey[0]).setValue(typeItem);
    }

    private void getUpdateKey() {
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("Updated");
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    try {
                        updatedKey = Integer.parseInt(s.getKey()) + 1;
                    } catch (NumberFormatException e) {
                        itemEntityViewModel.getAll().observe(getViewLifecycleOwner(), new Observer<List<ItemEntity>>() {
                            @Override
                            public void onChanged(@Nullable final List<ItemEntity> itemEntities) {
                                updatedKey = itemEntities.size();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                updatedKey = -1;
            }
        });
    }

    private void setUpdatedId(String lastKey) {
        FirebaseDatabase.getInstance().getReference().child("Updated")
                .child(updatedKey + "").child("id").setValue(lastKey)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Complete!", Toast.LENGTH_SHORT).show();

                            // TODO: Add Success SuccessDialog
                            progressDialogBackground.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        updatedKey++;
    }

    private String setDate(String inputDate, String type) {
        String inputFormat, outputFormat;
        SimpleDateFormat inputDateFormat, outputDateFormat;

        if (inputDate.contains("GMT")) {
            inputDate = inputDate.substring(0, inputDate.indexOf("GMT")).trim();
        }

        if (type.matches("date")){
            inputFormat = "dd/MM/yyyy";
            inputDateFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
            outputFormat = "EEE MMM dd yyyy HH:mm:ss";
            outputDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        }
        else if (type.matches("room")){
            inputFormat = "yyyy-MM-dd";
            inputDateFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
            outputFormat = "dd/MM/yyyy";
            outputDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        }
        else {
            inputFormat = "EEE MMM dd yyyy HH:mm:ss";
            inputDateFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
            outputFormat = "yyyy-MM-dd";
            outputDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        }

        Date date;
        String str = inputDate;

        try {
            date = inputDateFormat.parse(inputDate);
            if (date != null){
                str = outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;

    }

    private String getUnnamed2() {
        if (unnamed2 == null) {
            String date = etDatePicker.getText().toString();
            YY = date.substring(8, 10);
            String generateSerial = "DGO" + YY + branch + category + "-" + abbreviation + setOrderFormat(order);
            return generateSerial;
        }
        return unnamed2;
    }

    private String setOrderFormat(int order) {
        String num;
        if (order < 1) {
            num = "001";
            order++;
        } else if (order < 10) {
            num = "00" + order;
        } else if (order < 100) {
            num = "0" + order;
        } else {
            num = "" + order;
        }
        return num;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        etDatePicker.setText(sdf.format(calendar.getTime()));
    }

    private String getLastKey() {
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    lastKey = s.getKey();
                    path = Integer.parseInt(lastKey) + 1;
                    lastKey = String.valueOf(path);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Cannot Insert Data", Toast.LENGTH_SHORT).show();
                progressDialogBackground.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                lastKey = "";
            }
        });
        return lastKey;
    }

    private void checkSerial() {
        for (int i = 0; i < getItemEntity.size(); i++) {
            if (getItemEntity.get(i).getSerialNo().matches(serial)) {
                showAlertDialog("serial");
            }
        }
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkForm() {
        if (spTypeList.getSelectedItem().toString().matches("OTHER")) {
            if (!TextUtils.isEmpty(etOtherType.getText())) {
                String other = etOtherType.getText().toString().toUpperCase();
                for (String s : allType) {
                    if (other.matches(s)) {
                        Toast.makeText(getActivity(), "Type already created", Toast.LENGTH_SHORT).show();
                        hasOtherType = false;
                        return false;
                    }
                }
                if (other.length() < 3) {
                    Toast.makeText(getActivity(), "Type less than 3 character", Toast.LENGTH_SHORT).show();
                    hasOtherType = false;
                    return false;
                } else {
                    hasOtherType = true;
                    abbreviation = other.substring(0, 3);
                    type = other;
                }
            } else {
                hasOtherType = false;
                return false;
            }
        }
        if (type == null || type.length() == 0) {
            type = spTypeList.getSelectedItem().toString();
        }

        if (branch == 0) {
            branch = spBranch.getSelectedItemPosition() + 1;
        }

        if (category == 0) {
            category = spType.getSelectedItemPosition() + 1;
        }

        if (abbreviation == null || abbreviation.length() == 0) {
            abbreviation = spTypeList.getSelectedItem().toString();
            if (abbreviation.length() > 3) {
                abbreviation = abbreviation.substring(0, 3);
            }
        }

        if (TextUtils.isEmpty(etDeviceDetail.getText())) {
            etDeviceDetail.setText("-");
        }
        if (TextUtils.isEmpty(etOwnerName.getText())) {
            etOwnerName.setText("-");
        }
        if (TextUtils.isEmpty(etDeviceDetail.getText())) {
            etDeviceDetail.setText("-");
        }
        if (TextUtils.isEmpty(etSerialNumber.getText())) {
            etSerialNumber.setText("-");
        }
        if (TextUtils.isEmpty(etBrand.getText())) {
            etBrand.setText("-");
        }
        if (TextUtils.isEmpty(etNote.getText())) {
            etNote.setText("-");
        }
        if (TextUtils.isEmpty(etOwnerId.getText())) {
            etOwnerId.setText("-");
        }
        if (TextUtils.isEmpty(etDeviceModel.getText())) {
            etDeviceModel.setText("-");
        }
        if (TextUtils.isEmpty(etForwardDepreciation.getText())) {
            etForwardDepreciation.setText("-");
        }
        if (TextUtils.isEmpty(etDepreciationRate.getText())) {
            etDepreciationRate.setText("-");
        }
        if (TextUtils.isEmpty(etDepreciationinYear.getText())) {
            etDepreciationinYear.setText("-");
        }
        if (TextUtils.isEmpty(etAccumulateDepreciation.getText())) {
            etAccumulateDepreciation.setText("-");
        }
        if (TextUtils.isEmpty(etForwardedBudget.getText())) {
            etForwardedBudget.setText("-");
        }
        if (TextUtils.isEmpty(etWarranty.getText())) {
            etWarranty.setText("-");
        }

        if (TextUtils.isEmpty(etDevicePrice.getText()) && TextUtils.isEmpty(etPurchasePrice.getText())) {
            Toast.makeText(getContext(), "Please input price", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etDevicePrice.getText())) {
            etDevicePrice.setText("-");
        }
        if (TextUtils.isEmpty(etPurchasePrice.getText())) {
            etPurchasePrice.setText("-");
        }
        if (TextUtils.isEmpty(etDatePicker.getText())) {
            Toast.makeText(getContext(), "Please input purchased date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etQuantity.getText())) {
            etDeviceModel.setText("1");
            Toast.makeText(getContext(), "1 piece", Toast.LENGTH_SHORT).show();
        }
        if (YY == null || YY.length() == 0) {
            YY = etDatePicker.getText().toString().substring(8, 10);
        }

        return true;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboardFrom(getContext(), getView());
            if (view == btnShowMore) {
                if (!clickMore) {
                    clickMore = true;
                    moreData.setVisibility(View.VISIBLE);
                } else {
                    clickMore = false;
                    moreData.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    private View.OnClickListener onClickDate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboardFrom(getContext(), getView());
            if (itemId != null) {
                String[] d = etDatePicker.getText().toString().split("/");
                new DatePickerDialog(getContext(),
                        date, Integer.parseInt(d[0]),
                        Integer.parseInt(d[1]) - 1,
                        Integer.parseInt(d[2])).show();
            } else {
                new DatePickerDialog(getContext(),
                        date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener onDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private View.OnTouchListener onTouchScan = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int DRAWABLE_RIGHT = 2;

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getX() >= (etSerialNumber.getWidth()
                        - etSerialNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Intent intent = new Intent(getActivity(), ScanBarCodeAddDeviceActivity.class);
                    startActivityForResult(intent, 12345);
                    return true;
                }
            }
            return false;
        }
    };

    private AdapterView.OnItemSelectedListener onSpinnerSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView == spType && itemId == null) {
                selected = adapterView.getItemAtPosition(i).toString();
                switch (i) {
                    case 0:
                        setSpinnerFromSp(building, spTypeList);
                        category = 1;
                        break;
                    case 1:
                        setSpinnerFromSp(deviceAndAccessories, spTypeList);
                        category = 2;
                        break;
                    case 2:
                        setSpinnerFromSp(furniture, spTypeList);
                        category = 3;
                        break;
                    case 3:
                        setSpinnerFromSp(other, spTypeList);
                        category = 4;
                        break;
                }
            } else if (adapterView == spTypeList) {
                selected = adapterView.getItemAtPosition(i).toString();
                abbreviation = selected.toUpperCase().substring(0, 3);
                type = selected.toUpperCase();
                if (selected.matches("OTHER")) {
                    etOtherTypeLayout.setVisibility(View.VISIBLE);
                } else {
                    etOtherTypeLayout.setVisibility(View.INVISIBLE);
                    hasOtherType = false;
                }
            } else if (adapterView == spBranch) {
                switch (i) {
                    case 0:
                        branch = 1; break;
                    case 1:
                        branch = 2; break;
                    case 2:
                        branch = 3; break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            selected = "none";
        }
    };

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (view == etDevicePrice) {
                if (b) {
                    etDevicePrice.setHint("1000.00");
                } else {
                    etDevicePrice.setHint("");
                }
            } else if (view == etPurchasePrice) {
                if (b) {
                    etPurchasePrice.setHint("1000.00");
                } else {
                    etPurchasePrice.setHint("");
                }
            }
        }
    };

}
