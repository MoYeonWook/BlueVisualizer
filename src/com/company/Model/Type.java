package com.company.Model;

import java.util.ArrayList;


public class Type {
   private String name;
   private String intf;
   private int size;
   private ArrayList<Type> sub;

    public Type(String intf, int size) {
        this.intf = intf;
        this.size = size;
        this.sub = new ArrayList<>();
    }

    public Type(String name, String intf, int size) {
        this.name = name;
        this.intf = intf;
        this.size = size;
        this.sub = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntf() {
        return intf;
    }

    public void setIntf(String intf) {
        this.intf = intf;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Type> getSub() {
        return sub;
    }

    public void setSub(ArrayList<Type> sub) {
        this.sub = sub;
    }
}
