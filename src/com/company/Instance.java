package com.company;

import java.util.ArrayList;
import java.util.*;

public class Instance {
    String name;
    String bit;
    String intf;
    ArrayList<Instance> children;

    public Instance(){};

    public Instance(String name, String bit, String intf){
        this.name = name;
        this.bit = bit;
        this.intf = intf;
    }

    public String getName() {
        return name;
    }

    public String getBit() {
        return bit;
    }

    public String getIntf() {
        return intf;
    }

    public ArrayList<Instance> getChildren() {
        return children;
    }



    public void getSub(HashMap<String, Type> typeMap) {// 구조체의 각 element별 정보를 가져옴.
        Type types =typeMap.get(this.intf);
//        if(this.intf == null) return;
        if(types == null) return;
        int size = types.sub.size();
        ArrayList<Instance> tmp = new ArrayList<>();
//        System.out.println(this.bit);
        int pointer = this.bit.length();
//        System.out.println(types.intf);
        for (int i=size-1; i>=0;i--) {// vcd 파일에서 기록된 bit value는 preceding zero value를 무시하므로 bit#0 부터 parsing해서 마지막 type 부터 첫번째 type까지 할당해야 오류가없음.
                if(pointer<0) pointer =0;// inst의 bit size와 기록된 bitvalue가 일치하지 않을 수 있으므로 pointer가 0보다 작을 수 있음. 이 경우 parsing 하고 남은 나머지를 할당.
                Type type = types.sub.get(i);
//                System.out.println(type.name);
                pointer -= type.size;
                String partialBit = this.bit.substring((pointer < 0) ? 0 : pointer, pointer + type.size);
                while(pointer<0){partialBit= "0"+partialBit; pointer++;} //leading zero 보충
//                System.out.println(type.size + " " + pointer);
                Instance inst = new Instance(type.name,partialBit, type.intf);
                inst.getSub(typeMap);
                tmp.add(0,inst);
        }
        children = tmp;
        return;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBit(String bit) {
        this.bit = bit;
    }

    public void setIntf(String intf) {
        this.intf = intf;
    }

    public void setChildren(ArrayList<Instance> children) {
        this.children = children;
    }
}
