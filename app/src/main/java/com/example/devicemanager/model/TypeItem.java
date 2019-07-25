package com.example.devicemanager.model;

import androidx.room.Entity;

@Entity
public class TypeItem {
    String type;
    String assetId;

    public TypeItem() {

    }

    public TypeItem(String type, String assetId) {
        this.type = type;
        this.assetId = assetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
