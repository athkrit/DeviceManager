package com.example.devicemanager.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.room.AppDatabase;
import com.example.devicemanager.room.ItemEntity;

import java.util.List;

public class ItemEntityViewModel extends AndroidViewModel {
    private LoadData loadData;
    private LiveData<List<ItemEntity>> listLiveData;
    private LiveData<List<ItemEntity>> listOrder;
    private Application Application;

    public ItemEntityViewModel(@NonNull Application application) {
        super(application);
        Application = application;
        loadData = new LoadData(application);
        listLiveData = loadData.getAllItemEntity();
        listOrder = loadData.getOrderedItem();
    }

    public LiveData<List<ItemEntity>> getAll() {
        loadData = new LoadData(Application);
        listLiveData = loadData.getAllItemEntity();
        return listLiveData;
    }

    public LiveData<List<ItemEntity>> getOrder() {
        loadData = new LoadData(Application);
        listOrder = loadData.getOrderedItem();
        return listOrder;
    }

    public LiveData<List<ItemEntity>> selectData(String serialNew) {
        loadData = new LoadData(Application);
        return loadData.selectData(serialNew);
    }

    public LiveData<List<ItemEntity>> selectProductByType(String type, String order) {
        loadData = new LoadData(Application);
        return loadData.selectProductByType(type,order);
    }

    public void insert(ItemEntity itemEntity) {
        loadData.insert(itemEntity);
    }

    public void updateLastUpdate(String lastUpdate, int id) {
        loadData.updateLastUpdate(lastUpdate, id);
    }

    public void update(String branchCode,String lastUpdate, String ownedName, String ownerId, String brand, String serial_no, String item_detail,
                       String model, String warrantyDate, String purchasedPrice, String purchased_date, String price, String note,
                       String forwardDepreciation, String depreciationRate, String depreciationYear, String accumulatedDepreciation,
                       String forwardBudget, int id) {
        loadData.updateItem(branchCode,lastUpdate, ownedName, ownerId, brand, serial_no, item_detail, model, warrantyDate, purchasedPrice,
                purchased_date, price, note, forwardDepreciation, depreciationRate, depreciationYear, accumulatedDepreciation,
                forwardBudget, id);
    }

}
