package com.example.tiny.covertphonenum.model.models;

public class Prefix {
    private int id;
    private  String oldPRe;
    private  String newPre;


    public Prefix(int id, String oldPRe, String newPre) {
        this.id = id;
        this.oldPRe = oldPRe;
        this.newPre = newPre;
    }

    public Prefix() {
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldPRe() {
        return oldPRe;
    }

    public void setOldPRe(String oldPRe) {
        this.oldPRe = oldPRe;
    }

    public String getNewPre() {
        return newPre;
    }

    public void setNewPre(String newPre) {
        this.newPre = newPre;
    }
}
