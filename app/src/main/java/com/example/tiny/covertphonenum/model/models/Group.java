package com.example.tiny.covertphonenum.model.models;

import java.util.List;

public class Group {
    private int id;
    private String name;
    private List<Prefix> prefixList;

    public Group(int id, String name, List<Prefix> prefixList) {
        this.id = id;
        this.name = name;
        this.prefixList = prefixList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Prefix> getPrefixList() {
        return prefixList;
    }

    public void setPrefixList(List<Prefix> prefixList) {
        this.prefixList = prefixList;
    }
}
