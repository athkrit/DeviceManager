package com.example.devicemanager.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM ItemEntity")
    List<ItemEntity> getAll();

    @Query("SELECT * FROM ItemEntity ORDER BY purchased_date ASC, item_detail DESC")
    List<ItemEntity> getAllOrderByDate();

    @Query("SELECT * FROM ItemEntity WHERE item_id = :id ")
    List<ItemEntity> getProduct(String id);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY purchased_date ASC")
    List<ItemEntity> getAllProductByTypeOrderDateAsc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY purchased_date DESC")
    List<ItemEntity> getAllProductByTypeOrderDateDesc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY brand ASC")
    List<ItemEntity> getAllProductByTypeOrderBrandAsc(String type);

    @Query("SELECT * FROM ItemEntity WHERE item_type = :type ORDER BY brand DESC")
    List<ItemEntity> getAllProductByTypeOrderBrandDesc(String type);

    @Insert
    void insertAll(ItemEntity item);

    @Query("UPDATE itementity SET lastUpdated = :lastUpdate WHERE autoId = :id")
    void updateLastUpdate(String lastUpdate,int id);

    @Query("UPDATE itementity SET " +
            "lastUpdated = :lastUpdate ,owner_name = :ownedName ,placeId = :ownerId,brand = :brand ,serial_no = :serial_no ,"+
            "item_detail = :item_detail ,model = :model ,warrantyDate = :warrantyDate ,purchasedPrice = :purchasedPrice ,"+
            "purchased_date = :purchased_date ,price = :price ,note = :note ,forwardDepreciation = :forwardDepreciation ,"+
            "depreciationRate = :depreciationRate ,depreciationYear = :depreciationYear ," +
            "accumulatedDepreciation = :accumulatedDepreciation ,forwardBudget = :forwardBudget WHERE autoId = :id")
    void updateDataFromAdd(String lastUpdate, String ownedName, String ownerId, String brand, String serial_no, String item_detail,
                           String model, String warrantyDate, String purchasedPrice, String purchased_date, String price, String note,
                           String forwardDepreciation, String depreciationRate, String depreciationYear, String accumulatedDepreciation,
                           String forwardBudget, int id);

    @Query("DELETE FROM ItemEntity")
    void delete();
}
