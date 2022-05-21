package com.company.View.UserSettingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class UserSettingPanel extends JPanel {
    private JPanel middlePanel;
    private JPanel descriptionPanel;
    private JPanel optionPanel;
    private InfoMatchingPanel manageFIFO;
    private InfoMatchingPanel manageReg;
    private JCheckBox[] options;
    private JButton apply;
    private String[] optionList = {
            "Detect control Hazards (Epoch register should be implemented)",
            "Show assembly code of each FIFO"
    };
    private Dimension descriptionPanelSize = new Dimension(480,50);
    private Dimension middlePanelSize = new Dimension(430, 320);
    private Dimension optionPanelSize = new Dimension (480, 80);

    public UserSettingPanel(HashSet<String> FIFOSet, HashSet<String> RegSet){

        apply = new JButton("Apply");
        JLabel description1 = new JLabel("Choose the number of FIFO and register to visualize. Then match the name each.");
        JLabel description2 = new JLabel("The FIFO will be visualized in given order");
        descriptionPanel = new JPanel();
        middlePanel = new JPanel();
        optionPanel = new JPanel();
        createOptions();

        setLayout(new FlowLayout());
        descriptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        middlePanel.setLayout(new BorderLayout());
        descriptionPanel.setPreferredSize(descriptionPanelSize);
        middlePanel.setPreferredSize(middlePanelSize);
        optionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionPanel.setPreferredSize(optionPanelSize);

        this.manageFIFO =new InfoMatchingPanel("FIFO",FIFOSet);
        this.manageReg = new InfoMatchingPanel("Register",RegSet);

        descriptionPanel.add(description1);
        descriptionPanel.add(description2);
        middlePanel.add(manageFIFO,"West");
        middlePanel.add(manageReg,"East");
        add(descriptionPanel);
        add(middlePanel);
        add(optionPanel);
        add(apply);
    }

    private void createOptions(){
        int len = optionList.length;
        options = new JCheckBox[len];
        for(int i = 0; i < len; i++){
            options[i] = new JCheckBox(optionList[i]);
            optionPanel.add(options[i]);
        }
    }

    public JButton getApply() {
        return apply;
    }

    public InfoMatchingPanel getManageFIFO() {
        return manageFIFO;
    }

    public InfoMatchingPanel getManageReg() {
        return manageReg;
    }

    public JCheckBox[] getOptions() {
        return options;
    }

    public void setOptions(JCheckBox[] options) {
        this.options = options;
    }
}
