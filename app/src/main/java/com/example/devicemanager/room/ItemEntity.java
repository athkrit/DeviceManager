package com.example.devicemanager.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ItemEntity {

    @PrimaryKey
    private int autoId;

    @ColumnInfo(name = "item_id")
    private String unnamed2;

    @ColumnInfo(name = "item_type")
    private String type;

    @ColumnInfo(name = "item_detail")
    private String detail;

    @ColumnInfo(name = "serial_no")
    private String serialNo;

    @ColumnInfo(name = "owner_name")
    private String placeName;

    @ColumnInfo(name = "purchased_date")
    private String purchasedDate;

    private String note;
    private String departmentCode;
    private String placeId;
    private String assetId;
    private String price;
    private String model;
    private String depreciationRate;
    private String id;
    private String brand;
    private String shortCode;
    private String order;
    @Ignore
    private String groupByFIXGL;
    @Ignore
    private String ad;
    private String unnamed1;
    @Ignore
    private String sticker;
    private String forwardedBudget;
    private String accumulatedDepreciation;
    private String warrantyDate;
    private String depreciationYear;
    private String branchCode;
    private String assetTypeCode;
    private String purchasedPrice;
    private String forwardDepreciation;
    private String lastUpdated;

    public ItemEntity() {

    }

    public ItemEntity(int autoId, String unnamed2, String type, String detail, String serialNo,
                      String placeName, String purchasedDate, String note, String departmentCode,
                      String placeId, String assetId, String price, String model,
                      String depreciationRate, String id, String brand, String shortCode,
                      String order, String groupByFIXGL, String ad, String unnamed1, String sticker,
                      String forwardedBudget, String accumulatedDepreciation, String warrantyDate,
                      String depreciationYear, String branchCode, String assetTypeCode,
                      String purchasedPrice, String forwardDepreciation,
                      String lastUpdated) {
        this.autoId = autoId;
        this.unnamed2 = unnamed2;
        this.type = type;
        this.detail = detail;
        this.serialNo = serialNo;
        this.placeName = placeName;
        this.purchasedDate = purchasedDate;
        this.note = note;
        this.departmentCode = departmentCode;
        this.placeId = placeId;
        this.assetId = assetId;
        this.price = price;
        this.model = model;
        this.depreciationRate = depreciationRate;
        this.id = id;
        this.brand = brand;
        this.shortCode = shortCode;
        this.order = order;
        this.groupByFIXGL = groupByFIXGL;
        this.ad = ad;
        this.unnamed1 = unnamed1;
        this.sticker = sticker;
        this.forwardedBudget = forwardedBudget;
        this.accumulatedDepreciation = accumulatedDepreciation;
        this.warrantyDate = warrantyDate;
        this.depreciationYear = depreciationYear;
        this.branchCode = branchCode;
        this.assetTypeCode = assetTypeCode;
        this.purchasedPrice = purchasedPrice;
        this.lastUpdated = lastUpdated;
        this.forwardDepreciation = forwardDepreciation;
    }

    public String getUnnamed2() {
        return unnamed2;
    }

    public void setUnnamed2(String unnamed2) {
        this.unnamed2 = unnamed2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDepreciationRate() {
        return depreciationRate;
    }

    public void setDepreciationRate(String depreciationRate) {
        this.depreciationRate = depreciationRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getGroupByFIXGL() {
        return groupByFIXGL;
    }

    public void setGroupByFIXGL(String groupByFIXGL) {
        this.groupByFIXGL = groupByFIXGL;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getUnnamed1() {
        return unnamed1;
    }

    public void setUnnamed1(String unnamed1) {
        this.unnamed1 = unnamed1;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getForwardedBudget() {
        return forwardedBudget;
    }

    public void setForwardedBudget(String forwardedBudget) {
        this.forwardedBudget = forwardedBudget;
    }

    public String getAccumulatedDepreciation() {
        return accumulatedDepreciation;
    }

    public void setAccumulatedDepreciation(String accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }

    public String getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(String warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public String getDepreciationYear() {
        return depreciationYear;
    }

    public void setDepreciationYear(String depreciationYear) {
        this.depreciationYear = depreciationYear;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getAssetTypeCode() {
        return assetTypeCode;
    }

    public void setAssetTypeCode(String assetTypeCode) {
        this.assetTypeCode = assetTypeCode;
    }

    public String getPurchasedPrice() {
        return purchasedPrice;
    }

    public void setPurchasedPrice(String purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }

    public String getForwardDepreciation() {
        return forwardDepreciation;
    }

    public void setForwardDepreciation(String forwardDepreciation) {
        this.forwardDepreciation = forwardDepreciation;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
