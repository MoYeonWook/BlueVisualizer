package com.company.View;

import com.company.Model.BitType;
import com.company.Model.Instance;
import com.company.View.LeftSubPanel.LeftSubPanel;
import com.company.View.RightSubPanel.RightSubPanel;
import com.company.View.TopSubPanel.TopSubPanel;
import com.company.View.TopSubPanel.ValueSearchPanel;
import com.company.View.UserSettingPanel.UserSettingPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BVView extends JFrame {
   private UserSettingPanel userSettingPanel;
   private LeftSubPanel leftSubPanel;
   private RightSubPanel rightSubPanel;
   private TopSubPanel topSubPanel;
   private ValueSearchPanel valueSearchPanel;
   private Dimension initialSize = new Dimension(550,550);
//   private Dimension userSettingSize = new Dimension(500,450);
   private Dimension frameSize = new Dimension(1500,850);
   private Dimension topPanelSize = new Dimension(1400,80);
   private Dimension leftPanelSize = new Dimension(1000,700);
   private Dimension leftPanelControlSize = new Dimension(leftPanelSize.width-10, 30);
   private Dimension leftPanelDescriptionSize = new Dimension(leftPanelSize.width-10,150);
   private Dimension rightPanelSize = new Dimension(400,700);
   private Dimension rightMarginSize = new Dimension(400,15);
   private Dimension rightUpperPanelSize = new Dimension(390,480);
   private Dimension rightBottomPanelSize = new Dimension(390,190);


  public BVView(String name){
      super(name);
      setLayout(new FlowLayout());
      setPreferredSize(initialSize);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();
      setResizable(false);
      setVisible(true);
  }
  public void createUserSettingPanel(HashSet<String> FIFOSet, HashSet<String> RegSet){
      userSettingPanel = new UserSettingPanel(FIFOSet,RegSet);
      userSettingPanel.setPreferredSize(initialSize);
      add(userSettingPanel);
  }

  public void createComponent(List<String> FIFOList,List<String> RegList, List<String> rfile, HashMap<String, Instance> regFIFOInfo, int cycle, String msg, BitType bitType, Boolean asmMode){
      if(valueSearchPanel==null) valueSearchPanel = new ValueSearchPanel(FIFOList);
      topSubPanel = new TopSubPanel(asmMode,bitType,FIFOList,valueSearchPanel);
      leftSubPanel = new LeftSubPanel(FIFOList, cycle, regFIFOInfo,  msg, bitType, asmMode);
      rightSubPanel = new RightSubPanel(RegList,regFIFOInfo, rfile, bitType);
  }


  public void adjustSize(){
      topSubPanel.setPreferredSize(topPanelSize);
      leftSubPanel.setPreferredSize(leftPanelSize);
      leftSubPanel.getControl().setPreferredSize(leftPanelControlSize);
      leftSubPanel.getDescription().setPreferredSize(leftPanelDescriptionSize);
      rightSubPanel.setPreferredSize(rightPanelSize);
      rightSubPanel.getMarginPanel().setPreferredSize(rightMarginSize);
      rightSubPanel.getTopPanel().setPreferredSize(rightUpperPanelSize);
      rightSubPanel.getBottomPanel().setPreferredSize(rightBottomPanelSize);
  }

  public void addComponent(){
    add(topSubPanel);
    add(leftSubPanel);
    add(rightSubPanel);
  }

    public UserSettingPanel getUserSettingPanel() {
        return userSettingPanel;
    }

    public LeftSubPanel getLeftSubPanel() {
        return leftSubPanel;
    }

    public RightSubPanel getRightSubPanel() {
        return rightSubPanel;
    }

    public TopSubPanel getTopSubPanel() {  return topSubPanel; }

    public  Dimension getFrameSize() {  return frameSize;   }

}
