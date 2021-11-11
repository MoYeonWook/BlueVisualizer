package com.company.GUI;

import com.company.BlueVisualizer;
import com.company.FIFO;
import com.company.Instance;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RegPanelSet extends Panel {
    private RegPanel[] regs = new RegPanel[16];


    public RegPanelSet(HashMap<String, Instance> RegInfo, List<String> rfile, int idx,boolean binToHex){

        setLayout(new GridLayout(16,1,0,0));
        setPreferredSize(new Dimension(180,440));

        for(int i = 0 ; i<16;i++){
            String name = rfile.get(i+idx);
//            System.out.println(name);
            Instance reg = RegInfo.get(name);
            String bit;
            if(reg==null) bit = "0";
            else bit = BlueVisualizer.convertBin(reg.getBit(),binToHex);
            regs[i] = new RegPanel(i+idx,bit);
            add(regs[i]);
        }
    }


}