package com.example.devicemanager.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.example.devicemanager.room.AppDatabase;
import com.example.devicemanager.room.ItemDao;
import com.example.devicemanager.room.ItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoadData {
    private ItemDao itemDao;
    private AppDatabase db;

    public LoadData(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "app_database").build();
        itemDao = db.itemDao();
    }

    public void insert(ItemEntity itemEntity) {
        new InsertAsyncTask(itemDao).execute(itemEntity);
    }

    public void updateLastUpdate(String lastUpdate, int id) {
        new UpdateLastUpdate(itemDao, lastUpdate, id).execute();
    }

    public void updateItem(String lastUpdate, String ownedName, String ownerId, String brand, String serial_no, String item_detail,
                           String model, String warrantyDate, String purchasedPrice, String purchased_date, String price, String note,
                           String forwardDepreciation, String depreciationRate, String depreciationYear, String accumulatedDepreciation,
                           String forwardBudget, int id) {
        new UpdateItem(itemDao, lastUpdate, ownedName, ownerId, brand, serial_no, item_detail, model, warrantyDate, purchasedPrice,
                purchased_date, price, note, forwardDepreciation, depreciationRate, depreciationYear, accumulatedDepreciation,
                forwardBudget, id).execute();
    }

    public void deleteTable() {
        new DeleteAsyncTask(itemDao).execute();
    }

    public List<ItemEntity> selectData(String id) {
        List<ItemEntity> itemEntities = null;
        try {
            itemEntities = new SelectAsyncTask(itemDao, id).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return itemEntities;
    }

    public List<ItemEntity> getItem() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        try {
            itemEntities = new GetItemAsyncTask(itemDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return itemEntities;
    }

    public List<ItemEntity> getOrderedItem() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        try {
            itemEntities = new GetOrderedItemAsyncTask(itemDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return itemEntities;
    }

    public List<ItemEntity> selectProductByType(String type, String order) {
        List<ItemEntity> itemEntities = new ArrayList<>();
        try {
            itemEntities = new getAllProductByType(itemDao, type, order).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return itemEntities;
    }

    private static class InsertAsyncTask extends AsyncTask<ItemEntity, Void, Integer> {

        private ItemDao itemDao;

        InsertAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Integer doInBackground(ItemEntity... itemEntities) {
            itemDao.insertAll(itemEntities[0]);
            return 1;
        }
    }

    private static class GetItemAsyncTask extends AsyncTask<ItemEntity, Void, List<ItemEntity>> {

        private ItemDao itemDao;

        GetItemAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected List<ItemEntity> doInBackground(ItemEntity... itemEntities) {
            return itemDao.getAll();
        }
    }

    private static class GetOrderedItemAsyncTask extends AsyncTask<ItemEntity, Void, List<ItemEntity>> {

        private ItemDao itemDao;

        GetOrderedItemAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected List<ItemEntity> doInBackground(ItemEntity... itemEntities) {
            return itemDao.getAllOrderByDate();
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ItemEntity, Void, Void> {
        private ItemDao itemDao;

        DeleteAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(ItemEntity... itemEntities) {
            itemDao.delete();
            return null;
        }
    }

    private static class SelectAsyncTask extends AsyncTask<ItemEntity, Void, List<ItemEntity>> {
        private ItemDao itemDao;
        private String id;

        SelectAsyncTask(ItemDao itemDao, String id) {
            this.itemDao = itemDao;
            this.id = id;
        }

        @Override
        protected List<ItemEntity> doInBackground(ItemEntity... itemEntities) {
            return itemDao.getProduct(id);
        }
    }

    private static class getAllProductByType extends AsyncTask<ItemEntity, Void, List<ItemEntity>> {
        private ItemDao itemDao;
        private String type;
        private String order;

        getAllProductByType(ItemDao itemDao, String type, String order) {
            this.itemDao = itemDao;
            this.type = type;
            this.order = order;
        }

        @Override
        protected List<ItemEntity> doInBackground(ItemEntity... itemEntities) {
            if (order.matches("DateAsc")) {
                return itemDao.getAllProductByTypeOrderDateAsc(type);
            } else if (order.matches("DateDesc")) {
                return itemDao.getAllProductByTypeOrderDateDesc(type);
            } else if (order.matches("BrandAsc")) {
                return itemDao.getAllProductByTypeOrderBrandAsc(type);
            } else if (order.matches("BrandDesc")) {
                return itemDao.getAllProductByTypeOrderBrandDesc(type);
            } else {
                return null;
            }
        }
    }

    private class UpdateLastUpdate extends AsyncTask<String, Integer, Integer> {
        private ItemDao itemDao;
        String lastUpdate;
        int id;


        UpdateLastUpdate(ItemDao itemDao, String lastUpdate, int id) {
            this.itemDao = itemDao;
            this.lastUpdate = lastUpdate;
            this.id = id;
        }


        @Override
        protected Integer doInBackground(String... strings) {
            itemDao.updateLastUpdate(lastUpdate, id);
            return null;
        }
    }

    private class UpdateItem extends AsyncTask<String, Integer, Integer> {
        ItemDao itemDao;
        String lastUpdate, ownedName, ownerId, brand, serial_no, item_detail, model, warrantyDate, purchasedPrice, purchased_date,
                price, note, forwardDepreciation, depreciationRate, depreciationYear, accumulatedDepreciation, forwardBudget;
        int id;

        public UpdateItem(ItemDao itemDao, String lastUpdate, String ownedName, String ownerId, String brand, String serial_no,
                          String item_detail, String model, String warrantyDate, String purchasedPrice, String purchased_date,
                          String price, String note, String forwardDepreciation, String depreciationRate, String depreciationYear,
                          String accumulatedDepreciation, String forwardBudget, int id) {
            this.itemDao = itemDao;
            this.lastUpdate = lastUpdate;
            this.ownedName = ownedName;
            this.ownerId = ownerId;
            this.brand = brand;
            this.serial_no = serial_no;
            this.item_detail = item_detail;
            this.model = model;
            this.warrantyDate = warrantyDate;
            this.purchasedPrice = purchasedPrice;
            this.purchased_date = purchased_date;
            this.price = price;
            this.note = note;
            this.forwardDepreciation = forwardDepreciation;
            this.depreciationRate = depreciationRate;
            this.depreciationYear = depreciationYear;
            this.accumulatedDepreciation = accumulatedDepreciation;
            this.forwardBudget = forwardBudget;
            this.id = id;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            itemDao.updateDataFromAdd(lastUpdate, ownedName, ownerId, brand, serial_no, item_detail, model, warrantyDate, purchasedPrice, purchased_date,
                    price, note, forwardDepreciation, depreciationRate, depreciationYear, accumulatedDepreciation, forwardBudget,id);
            return null;
        }
    }
}
