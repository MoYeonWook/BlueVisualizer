package com.company.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ValueSearchPanel extends JPanel {
    JComboBox<String> FIFOName;
    JComboBox<String> valueName;
    JCheckBox direction = new JCheckBox("backward");
    JTextField value = new JTextField("",10);
    JButton search = new JButton("search");

    public ValueSearchPanel(List<String> FIFOList){

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        FIFOName = new JComboBox(FIFOList.toArray());
        valueName = new JComboBox();
        FIFOName.setSelectedItem(null);
        JLabel FIFONameLabel = new JLabel("FIFO");
        JLabel valueNameLabel = new JLabel("valueName");
        JLabel valueLabel = new JLabel("value ");

        setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));

        panel1.add(FIFONameLabel);
        panel1.add(FIFOName);
        panel2.add(valueNameLabel);
        panel2.add(valueName);
        panel3.add(valueLabel);
        panel3.add(value);
        panel4.add(direction);

        add(panel1);
        add(panel2);
        add(panel3);
        add(panel4);
        add(search);
    }

    public JCheckBox getDirection() {
        return direction;
    }

    public JComboBox<String> getFIFOName() {
        return FIFOName;
    }

    public void setFIFOName(JComboBox FIFOName) {
        this.FIFOName = FIFOName;
    }

    public JComboBox<String> getValueName() {
        return valueName;
    }

    public void setValueName(JComboBox valueName) {
        this.valueName = valueName;
    }

    public JTextField getValue() {
        return value;
    }

    public void setValue(JTextField value) {
        this.value = value;
    }

    public JButton getSearch() {
        return search;
    }

    public void setSearch(JButton search) {
        this.search = search;
    }
}
