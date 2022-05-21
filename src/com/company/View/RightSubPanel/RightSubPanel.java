package com.company.View.RightSubPanel;

import com.company.Model.BitType;
import com.company.Model.Instance;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


public class RightSubPanel extends JPanel {
    private JPanel marginPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JPanel topPanel = new JPanel();
    private UserRegPanelSet userRegPanelSet;
    private RegPanelSet regPanelBottom1;
    private RegPanelSet regPanelBottom2;
    private Font title2 = new Font("Candara Light",Font.PLAIN,20);


    public RightSubPanel(List<String> regList, HashMap<String,Instance> regInfo,List<String> rfile, BitType bitType){

        this.userRegPanelSet = new UserRegPanelSet(regInfo,regList,bitType);
        this.regPanelBottom1 = new RegPanelSet(regInfo,rfile,0,bitType);
        this.regPanelBottom2 = new RegPanelSet(regInfo,rfile,16,bitType);
        setLayout(new FlowLayout());
        topPanel.setLayout(new FlowLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.black),"Register", TitledBorder.CENTER,TitledBorder.ABOVE_TOP,title2));
        bottomPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.black),"User-Selected Register", TitledBorder.CENTER,TitledBorder.ABOVE_TOP,title2));
        attach();

    }
    public void attach(){
        bottomPanel.add(userRegPanelSet);
        topPanel.add(regPanelBottom1);
        topPanel.add(regPanelBottom2);
        add(marginPanel);
        add(topPanel);
        add(bottomPanel);
    }


    public JPanel getMarginPanel() {
        return marginPanel;
    }

    public void setMarginPanel(JPanel marginPanel) {
        this.marginPanel = marginPanel;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    public void setBottomPanel(JPanel bottomPanel) {
        this.bottomPanel = bottomPanel;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public void setTopPanel(JPanel topPanel) {
        this.topPanel = topPanel;
    }
}
