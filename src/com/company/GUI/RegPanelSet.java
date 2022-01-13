package com.company.GUI;

import com.company.BitType;
import com.company.FIFO;
import com.company.Instance;

import java.awt.*;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class RegPanelSet extends Panel {
    private RegPanel[] regs = new RegPanel[16];


    public RegPanelSet(HashMap<String, Instance> RegInfo, List<String> rfile, int idx, BitType bitType){

        setLayout(new GridLayout(16,1,0,0));
        setPreferredSize(new Dimension(180,440));

        for(int i = 0 ; i<16;i++){
            String name = rfile.get(i+idx);
//            System.out.println(name);
            Instance reg = RegInfo.get(name);
            String bit;
            if(reg==null) bit = "0";
            else bit = BitType.convertBin(reg.getBit(),bitType);
            regs[i] = new RegPanel(i+idx,bit);
            add(regs[i]);
        }
    }
    private String convertBin(String bin, boolean binToHex){
        int idx =-1;// no leading zero;
        for(int i=0; i<bin.length();i++){
            if(bin.charAt(i)=='0') idx = i;
            else break;
        }
        if(idx == bin.length()-1) bin = "0";
        else bin = bin.substring(idx+1);


        if(binToHex)  return new BigInteger(bin, 2).toString(16);
        else return bin;
    }

}
