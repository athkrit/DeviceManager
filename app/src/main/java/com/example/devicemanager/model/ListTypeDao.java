package com.example.devicemanager.model;

import java.util.List;

public class ListTypeDao {
    List<TypeItem> list;

    public ListTypeDao(){

    }

    public ListTypeDao(List<TypeItem> list) {
        this.list = list;
    }

    public List<TypeItem> getList() {
        return list;
    }

    public void setList(List<TypeItem> list) {
        this.list = list;
    }
}
