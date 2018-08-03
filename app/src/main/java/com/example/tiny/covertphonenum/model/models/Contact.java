package com.example.tiny.covertphonenum.model.models;

import java.util.List;

public class Contact {
    private String id;
    private String name;
    private List<NumberFeild> number;

    public Contact() {
    }

    public Contact(String id, String name, List<NumberFeild> number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NumberFeild> getNumber() {
        return number;
    }

    public void setNumber(List<NumberFeild> number) {
        this.number = number;
    }
}
