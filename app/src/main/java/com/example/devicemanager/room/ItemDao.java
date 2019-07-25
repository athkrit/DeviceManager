package com.example.devicemanager.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM ItemEntity")
    LiveData<List<ItemEntity>> getAll();

    @Query("SELECT * FROM ItemEntity ORDER BY purchased_date ASC, item_detail DESC")
    LiveData<List<ItemEntity>> getAllOrderByDate();

    @Query("SELECT * FROM ItemEntity WHERE item_id = :id ")
    LiveData<List<ItemEntity>> getProduct(String id);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY purchased_date ASC")
    LiveData<List<ItemEntity>> getAllProductByTypeOrderDateAsc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY purchased_date DESC")
    LiveData<List<ItemEntity>> getAllProductByTypeOrderDateDesc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY brand ASC")
    LiveData<List<ItemEntity>> getAllProductByTypeOrderBrandAsc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY brand DESC")
    LiveData<List<ItemEntity>> getAllProductByTypeOrderBrandDesc(String type);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(ItemEntity item);

    @Query("UPDATE itementity SET lastUpdated = :lastUpdate WHERE autoId = :id")
    void updateLastUpdate(String lastUpdate, int id);

    @Query("UPDATE itementity SET " +
            "lastUpdated = :lastUpdate ,owner_name = :ownedName ,placeId = :ownerId,brand = :brand ,serial_no = :serial_no ," +
            "item_detail = :item_detail ,model = :model ,warrantyDate = :warrantyDate ,purchasedPrice = :purchasedPrice ," +
            "purchased_date = :purchased_date ,price = :price ,note = :note ,forwardDepreciation = :forwardDepreciation ," +
            "depreciationRate = :depreciationRate ,depreciationYear = :depreciationYear ,branchCode = :branchCode," +
            "accumulatedDepreciation = :accumulatedDepreciation ,forwardedBudget = :forwardedBudget WHERE autoId = :id")
    void updateDataFromAdd(String branchCode, String lastUpdate, String ownedName, String ownerId, String brand, String serial_no, String item_detail,
                           String model, String warrantyDate, String purchasedPrice, String purchased_date, String price, String note,
                           String forwardDepreciation, String depreciationRate, String depreciationYear, String accumulatedDepreciation,
                           String forwardedBudget, int id);

    @Query("DELETE FROM ItemEntity")
    void delete();
}
