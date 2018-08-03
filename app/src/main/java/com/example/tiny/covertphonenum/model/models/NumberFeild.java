package com.example.tiny.covertphonenum.model.models;

public class NumberFeild {
    private String numberContact;
    private String numberContactOld;
    private int typeContact;

    public NumberFeild() {
    }

    public NumberFeild(String numberContact, String numberContactOld, int typeContact) {
        this.numberContact = numberContact;
        this.numberContactOld = numberContactOld;
        this.typeContact = typeContact;
    }

    public String getNumberContact() {
        return numberContact;
    }

    public void setNumberContact(String numberContact) {
        this.numberContact = numberContact;
    }

    public String getNumberContactOld() {
        return numberContactOld;
    }

    public void setNumberContactOld(String numberContactOld) {
        this.numberContactOld = numberContactOld;
    }

    public int getTypeContact() {
        return typeContact;
    }

    public void setTypeContact(int typeContact) {
        this.typeContact = typeContact;
    }
}
