package com.company.View.RightSubPanel;

import com.company.Model.BitType;
import com.company.Model.Instance;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.List;

public class UserRegPanelSet extends JPanel {
    List<String> regList;
    BitType bitType;
    String bit;
    public UserRegPanelSet(HashMap<String, Instance> instanceInfo, List<String> regList,BitType bitType){
        this.regList = regList;
        this.bitType = bitType;
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(350,150));
        int num = regList.size();

        for (String name : regList) {
            Instance instance = instanceInfo.get(name);

            if (instance != null) {
                bit = BitType.convertBin(instance.getBit(), bitType);
            } else bit = "not initialized";

            UserRegPanel userRegPanel = new UserRegPanel(name, bit);
            add(userRegPanel);
        }

    }


}
