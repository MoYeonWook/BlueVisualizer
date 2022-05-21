package com.company.Model;

import java.util.ArrayList;
import java.util.*;

public class Instance {
    String name;
    String bit;
    String intf;
    boolean maybe = false;
    boolean valid = true;
    ArrayList<Instance> children;

    public Instance(){}

    public Instance(String name, String bit, String intf){
        this.name = name;
        this.bit = bit;

        if(intf!= null){
            String[] checkMaybe = intf.split("_");

            if(checkMaybe[0].equals("Maybe#")){
                this.intf = intf.substring(7);
                maybe = true;

            }else this.intf =intf;
        }

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

    public boolean isMaybe() {
        return maybe;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void getSub(HashMap<String, Type> typeMap) {// 구조체의 각 element별 정보를 가져옴.
        Type types =typeMap.get(intf);

        if(types == null) return;

        int instanceNum = types.getSub().size();
        int bitLength = types.getSize();
        int pointer = bit.length();
        children = new ArrayList<>();

        for (int i=instanceNum-1; i>=0;i--) {// vcd 파일에서 기록된 bit value는 leadingg zero를 무시하므로 bit#0 부터 parsing해서 마지막 type 부터 첫번째 type까지 할당해야 오류가없음.
                if(pointer<0) pointer =0;// inst의 bit size와 기록된 bitvalue가 일치하지 않을 수 있으므로 pointer가 0보다 작을 수 있음. 이 경우 parsing 하고 남은 나머지를 할당.
                Type type = types.getSub().get(i);
                pointer -= type.getSize();
                String partialBit = this.getBit().substring(Math.max(0,pointer), pointer + type.getSize());
                Instance inst = new Instance(type.getName(),partialBit, type.getIntf());
                inst.getSub(typeMap);
                children.add(0,inst);
        }

        if(maybe){
            if(bitLength >= bit.length()) valid = false;
            else if(bit.charAt(0)=='1') valid = true;
            else valid = false;
        }
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
