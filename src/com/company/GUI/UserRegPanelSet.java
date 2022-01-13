package com.company.GUI;

import com.company.BitType;
import com.company.Instance;

import javax.swing.*;
import java.math.BigInteger;
import java.util.*;
import java.awt.*;
import java.util.List;

import static com.company.BitType.convertBin;

public class UserRegPanelSet extends JPanel {
    List<String> regList;
    BitType bitType;
    String bit;
//    List<UserRegPanel> userRegPanels;
    public UserRegPanelSet(HashMap<String, Instance> instanceInfo, List<String> regList,BitType bitType){
        this.regList = regList;
        this.bitType = bitType;
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(350,170));
        int num = regList.size();
        for (int i= 0; i<num; i++){
            String name = regList.get(i);
            Instance instance = instanceInfo.get(name);
            if(instance!=null){
                bit = BitType.convertBin(instance.getBit(),bitType);
            }else bit = "not initialized";
//            System.out.println(name+" "+bit);
            UserRegPanel userRegPanel = new UserRegPanel(name,bit);
//            userRegPanels.add(userRegPanel);
            add(userRegPanel);
        }

    }

//    public void setUserRegPanelSet(HashMap<String, Instance> instanceInfo){
//        for(UserRegPanel urp : userRegPanels){
//            String name = urp.name.getText();
//            Instance instance = instanceInfo.get(name);
//            String bit;
//            if(instance!=null){
//                bit = BlueVisualizer.convertBin(instance.getBit(),binToHex);
//            }else bit = "not initialized";
//            urp.modifyValue(name,bit);
//        }
//    }
}
