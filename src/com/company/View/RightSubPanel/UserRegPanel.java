package com.company.View.RightSubPanel;

import javax.swing.*;
import java.awt.*;

public class UserRegPanel extends Panel {
    JLabel name;
    JLabel content;
    Dimension size = new Dimension(350,30);
    Dimension nameSize = new Dimension(70, 30);
    Dimension contentSize = new Dimension(270,30);

    public UserRegPanel(String n, String bit){
        name = new JLabel(n,SwingConstants.LEFT);
        content = new JLabel(bit,SwingConstants.CENTER);
        //
        name.setPreferredSize(nameSize);
        content.setPreferredSize(contentSize);
        //
        add(name);
        add(content);
        setPreferredSize(size);


    }
    public void modifyValue(String n, String bit){
        name.setText(n);
        content.setText(bit);
    }
}
