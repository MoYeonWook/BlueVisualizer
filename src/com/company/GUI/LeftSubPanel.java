package com.company.GUI;

import com.company.BitType;
import com.company.Instance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

public class LeftSubPanel extends Panel {

    private FIFOPanelSet fifoPanelSet;
//    private Dimension leftPanelSize;
    private String timeUnit;

    private Panel control = new Panel();
    private Panel buttons = new Panel(new FlowLayout());
    private Panel cycleInfo = new Panel(new FlowLayout());
    private JLabel cycletxt = new JLabel("Cycle: ");
    private JTextField cycleNum = new JTextField(String.valueOf(0),10);
    private JTextArea description;
    private JButton fir = new JButton("first cycle");
    private JButton prs = new JButton("previous hazard");
    private JButton pre = new JButton("previous cycle");
    private JButton nxt = new JButton("next cycle");
    private JButton nxs = new JButton("next hazard");
    private JButton lst = new JButton("last cycle");

    public LeftSubPanel(List<String> FIFOList, int cycle, HashMap<String, Instance> FIFOInfo, String msg, BitType bitType, Boolean asmMode) {

        this.fifoPanelSet = new FIFOPanelSet(FIFOList.size(),FIFOList,FIFOInfo,bitType,asmMode);
        description = new JTextArea(msg,5,50);
        cycleNum.setText(String.valueOf(cycle));

        //settings
        setLayout(new FlowLayout());
        control.setLayout(new BorderLayout());
        description.setFont(new Font("", Font.PLAIN, 15));
        attach();
        }

    public void attach() {
        buttons.add(fir);
        buttons.add(prs);
        buttons.add(pre);
        buttons.add(nxt);
        buttons.add(nxs);
        buttons.add(lst);
        cycleInfo.add(cycletxt, BorderLayout.EAST);
        cycleInfo.add(cycleNum, BorderLayout.EAST);
        control.add(buttons, BorderLayout.WEST);
        control.add(cycleInfo, BorderLayout.EAST);
        add(fifoPanelSet);
        add(control);
        add(description);
    }

    public Panel getControl() {
        return control;
    }

    public void setControl(Panel control) {
        this.control = control;
    }

    public JTextArea getDescription() {
        return description;
    }

    public void setDescription(JTextArea description) {
        this.description = description;
    }

    public JTextField getCycleNum() {
        return cycleNum;
    }

    public JButton getFir() {
        return fir;
    }

    public JButton getPrs() {
        return prs;
    }

    public JButton getPre() {
        return pre;
    }

    public JButton getNxt() {
        return nxt;
    }

    public JButton getNxs() {
        return nxs;
    }

    public JButton getLst() {
        return lst;
    }
}
