package com.company.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;

public class UserSettingPanel extends JPanel {
    private InfoMatching manageFIFO;
    private InfoMatching manageReg;
    private JButton submit;

    public UserSettingPanel(HashSet<String> FIFOSet, HashSet<String> RegSet){

        JLabel description1 = new JLabel("Choose the number of FIFO and register to visualize.");
        JLabel description2 = new JLabel("Then match the name each.");
        setLayout(new FlowLayout());
        submit = new JButton("submit");
        add(description1);
        add(description2);
        this.manageFIFO =new InfoMatching("FIFO",FIFOSet);
        this.manageReg = new InfoMatching("register",RegSet);
        add(manageFIFO);
        add(manageReg);
        add(submit);
    }

    public JButton getSubmit() {
        return submit;
    }

    public InfoMatching getManageFIFO() {
        return manageFIFO;
    }

    public InfoMatching getManageReg() {
        return manageReg;
    }
}
