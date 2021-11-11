package com.company.GUI;

import javax.swing.*;
import java.awt.*;

public class RegPanel extends Panel {
    int i;
    String val;
    public RegPanel(int i, String val){
        this. i = i;
        this. val = val;

        //component
        String name ="x"+String.valueOf(i);
        if(name.length()==2) name=" "+name;// alignment

        JLabel num = new JLabel(name);
        JTextField tf = new JTextField(val);

        //settings
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(120,20));
        tf.setEditable(false);
        tf.setPreferredSize(new Dimension(100,15));

        //Insertion
        add(num);
        add(tf);
    }
}
