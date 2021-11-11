package com.company.GUI;

import com.company.BlueVisualizer;
import com.company.Instance;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.List;

public class UserRegPanelSet extends JPanel {
    List<String> regList;
    Boolean binToHex;
//    List<UserRegPanel> userRegPanels;
    public UserRegPanelSet(HashMap<String, Instance> instanceInfo, List<String> regList,Boolean binToHex){
        this.regList = regList;
        this.binToHex = binToHex;
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(350,170));
        int num = regList.size();
        for (int i= 0; i<num; i++){
            String name = regList.get(i);
            Instance instance = instanceInfo.get(name);
            String bit;
            if(instance!=null){
                bit = BlueVisualizer.convertBin(instance.getBit(),binToHex);
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
