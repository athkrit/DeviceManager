package com.example.devicemanager.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.AddDeviceActivity;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.room.ItemEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class DeviceDetailFragment extends Fragment {

    private TextView tvSerialNumber, tvOwnerName, tvDeviceDetail,
            tvLastUpdate, tvAddedDate, tvType, tvItemId, tvBrand, tvModel;
    private static String serial;
    private Button btnCheck, btnEdit;
    private ProgressBar progressBar;
    private View progressDialogBackground;
    private LoadData loadData;
    private int updatedKey;
    private String lastKey;
    List<ItemEntity> itemEntity;

    public static DeviceDetailFragment newInstances(String barcode) {
        DeviceDetailFragment fragment = new DeviceDetailFragment();
        serial = barcode;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_device, container, false);
        initInstances(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11111) {
            if (resultCode == RESULT_OK) {
                getActivity().finish();
            }
        }
    }

    private void initInstances(View view) {

        loadData = new LoadData(getContext());

        tvSerialNumber = view.findViewById(R.id.tvSerialNumber);
        tvOwnerName = view.findViewById(R.id.tvOwnerName);
        tvDeviceDetail = view.findViewById(R.id.tvDeviceDetail);
        tvAddedDate = view.findViewById(R.id.tvAddedDate);
        tvLastUpdate = view.findViewById(R.id.tvLastUpdate);
        tvType = view.findViewById(R.id.tvType);
        tvItemId = view.findViewById(R.id.tvItemId);
        tvBrand = view.findViewById(R.id.tvBrand);
        tvModel = view.findViewById(R.id.tvModel);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnCheck = view.findViewById(R.id.btnCheck);
        btnEdit.setOnClickListener(clickListener);
        btnCheck.setOnClickListener(clickListener);

        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        progressDialogBackground = (View) view.findViewById(R.id.view);

        getUpdateKey();

        if (serial != null) {
            getData(serial);
        }

    }

    @SuppressLint("SetTextI18n")
    private void getData(String serialNew) {
        progressDialogBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        itemEntity = loadData.selectData(serialNew);

        if (itemEntity != null) {
            lastKey = itemEntity.get(0).getAutoId() + "";

            tvItemId.setText("Item ID : " + itemEntity.get(0).getUnnamed2());
            tvOwnerName.setText(checkNoneData(itemEntity.get(0).getPlaceName(), "No Owner"));
            tvDeviceDetail.setText(checkNoneData(itemEntity.get(0).getDetail(), "N/A"));
            tvBrand.setText(checkNoneData(itemEntity.get(0).getBrand(), "N/A"));
            tvType.setText(itemEntity.get(0).getType());
            tvModel.setText("Model : " + checkNoneData(itemEntity.get(0).getModel(), "N/A"));
            tvSerialNumber.setText("S/N : " + checkNoneData(itemEntity.get(0).getSerialNo(), "No Serial"));

            tvLastUpdate.setText(getResources().getString(R.string.last_check) + " : " + itemEntity.get(0).getLastUpdated());
            tvAddedDate.setText(getResources().getString(R.string.added_date) + " : " + setDate(itemEntity.get(0).getPurchasedDate()));
            hideDialog();
        } else {
            getActivity().finish();
            hideDialog();
        }
    }

    private String checkNoneData(String data, String text) {

        if (data.trim().matches("-")) {
            return text;
        } else {
            return data;
        }

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnEdit) {
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                intent.putExtra("Serial", serial);
                startActivityForResult(intent, 11111);
            } else if (view == btnCheck) {
                showAlertDialog(R.string.dialog_msg_checked, "check");
            }
        }
    };

    private void hideDialog() {
        progressDialogBackground.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showAlertDialog(int msg, final String state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String dialogMsg = getResources().getString(msg);

        builder.setMessage(dialogMsg).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (state.matches("check")) {
                    checkedDevice();
                    setUpdatedId(lastKey);
                } else if (state.matches("add")) {
                    Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                    intent.putExtra("Serial", serial);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                if (state.matches("add")) {
                    getActivity().finish();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkedDevice() {
        progressDialogBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final String idKey = loadData.selectData(serial).get(0).getAutoId() + "";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Data").child(idKey).child("lastUpdated");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        final Date date = new Date();
        databaseReference.setValue(dateFormat.format(date))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            int autoId = itemEntity.get(0).getAutoId();
                            hideDialog();
                            loadData.updateLastUpdate(dateFormat.format(date),autoId);
                            tvLastUpdate.setText("Last Check : " + loadData.getItem().get(autoId).getLastUpdated());
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("checkedDevice", e.toString());
            }
        });
    }

    private void getUpdateKey() {
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("Updated");
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    updatedKey = Integer.parseInt(s.getKey()) + 1;
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
                            updatedKey++;
                        }
                    }
                });
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
