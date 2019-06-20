package com.example.devicemanager.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.devicemanager.R;
import com.example.devicemanager.activity.CameraActivity;
import com.example.devicemanager.manager.Contextor;

import java.io.File;

public class AddDeviceFragment extends Fragment {

    private Spinner spType ;
    private ImageView ivDevice;
    private static String serial;
    EditText etOwnerName,etSerialNumber,etDeviceDetail;
    private Button btnConfirm, btnCancel;
    Button btnScanBarcode;

    public static AddDeviceFragment newInstances(String barcode){
        serial = barcode;
        AddDeviceFragment fragment = new AddDeviceFragment();
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
        View view = getLayoutInflater().inflate(R.layout.fragment_edit_detail, container, false);
        initInstances(view,savedInstanceState);
        return view;
    }

    private void initInstances(View view, Bundle savedInstanceState){

        spType = view.findViewById(R.id.spinnerDeviceType);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                Contextor.getInstance().getContext(),
                R.array.device_types,
                R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spType.setAdapter(spinnerAdapter);

        ivDevice = view.findViewById(R.id.ivDevice);
        ivDevice.setOnClickListener(onClickImage);
        etOwnerName=(EditText) view.findViewById(R.id.etOwnerName);
        etSerialNumber=(EditText) view.findViewById(R.id.etSerialNumber);
        etDeviceDetail=(EditText) view.findViewById(R.id.etDeviceDetail);
        btnScanBarcode = (Button) view.findViewById(R.id.btnScanBarcode);
        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarCodeAddDeviceActivity.class);
                startActivityForResult(intent , 12345);
            }
        });

        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);


        String path = getArguments().getString("Path");

        if ( path != null) {
            Uri uri = Uri.fromFile(new File(getArguments().getString("Path")));
            Glide.with(Contextor.getInstance().getContext())
                    .load(uri)
                    .into(ivDevice);
        }
        if(!serial.matches("null")){
            etSerialNumber.setText(serial);
            etDeviceDetail.setText("Macbook Pro 14");
            etOwnerName.setText("Mr.Natthapat Phatthana");
        }
        else {

        }
    }

    private void showAlertDialog(int msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String dialogMsg = getResources().getString(msg);

        builder.setMessage(dialogMsg).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // TODO: Save State in this condition & Fix stacked activity
    View.OnClickListener onClickImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Contextor.getInstance().getContext(), CameraActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == btnCancel){
                showAlertDialog(R.string.dialog_msg_cancel);
            }
            else if (view == btnConfirm) {
                showAlertDialog(R.string.dialog_msg_confirm);
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                etSerialNumber.setText(data.getStringExtra("serial"));
            }
        }
    }
}
