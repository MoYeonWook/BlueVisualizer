package com.company.View.RightSubPanel;

import javax.swing.*;
import java.awt.*;

public class RegPanel extends Panel {
    int i;
    String val;
    Dimension textFieldSize = new Dimension(100,18);
    Dimension regPanelSize = new Dimension(120,22);
    public RegPanel(int i, String val){
        this. i = i;
        this. val = val;

        //component
        String name ="x"+i;
        if(name.length()==2) name=" "+name;// alignment

        JLabel num = new JLabel(name);
        JTextField tf = new JTextField(val);

        //settings
        setLayout(new FlowLayout());
        setPreferredSize(regPanelSize);
        tf.setEditable(false);
        tf.setPreferredSize(textFieldSize);
        tf.setHorizontalAlignment(2);

        //Insertion
        add(num);
        add(tf);
    }
}
