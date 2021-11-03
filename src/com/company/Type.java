package com.company;

import java.util.ArrayList;


public class Type {
    String name;
    String intf;
    int size;
    ArrayList<Type> sub;

    public Type(String intf, int size) {
        this.intf = intf;
        this.size = size;
        this.sub = new ArrayList<Type>();
    }

    public Type(String name, String intf, int size) {
        this.name = name;
        this.intf = intf;
        this.size = size;
        this.sub = new ArrayList<Type>();
    }
}
