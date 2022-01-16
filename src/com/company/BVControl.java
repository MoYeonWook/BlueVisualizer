package com.company;

import com.company.GUI.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BVControl {
    BVModel bvModel;
    BVView bvView;

    public BVControl(BVModel bvModel, BVView bvView){
        this.bvModel = bvModel;
        this.bvView = bvView;
    }

    public void initialize(){
        bvView.createUserSettingPanel(bvModel.getFIFOSet(), bvModel.getRegSet());
        UserSettingPanel userSettingPanel= bvView.getUserSettingPanel();
        //options number
        int CONTROLHAZARD = 0;
        int ASMMODE = 1;

        //userSettingPanel handler
        userSettingPanel.getApply()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Visualizing Chosen data...");
                        bvModel.setFIFOList (userSettingPanel.getManageFIFO().getPosName());
                        bvModel.setRegList(userSettingPanel.getManageReg().getPosName());
                        bvModel.setAsmInFIFO(new String[bvModel.getFIFOList().size()]);
                        for(int i =0;i<32;i++) bvModel.getRfile().add("rfile"+i); // add all the name of cpu register
                        for(int i =0; i<bvModel.getTimeLine().size();i++) bvModel.initializeStat(i); // start at i = 1, cycle 0 has already initialized.
                        bvView.setPreferredSize(bvView.getFrameSize());
                        bvView.pack();
                        bvView.setResizable(false);
                        redraw();
                    }
                });

        userSettingPanel.getOptions()[CONTROLHAZARD]
                .addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if(e.getStateChange() == ItemEvent.SELECTED) bvModel.setDetectControlHazard(true);
                        else if(e.getStateChange() == ItemEvent.DESELECTED) bvModel.setDetectControlHazard(false);
                    }
                });
        userSettingPanel.getOptions()[ASMMODE]
                .addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if(e.getStateChange() == ItemEvent.SELECTED) bvModel.setAsmMode(true);
                        else if(e.getStateChange() == ItemEvent.DESELECTED) bvModel.setAsmMode(false);
                    }
                });

        bvView.revalidate();
    }

    private void addListener(){
        LeftSubPanel leftSubPanel = bvView.getLeftSubPanel();
        RightSubPanel rightSubPanel = bvView.getRightSubPanel();
        TopSubPanel topSubPanel = bvView.getTopSubPanel();

        //topSubPanel Handler
        topSubPanel.getBinHexButton()
                .addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BitType bitType = bvModel.getBitRepresentation();
                    if(bitType == BitType.BIN) bvModel.setBitRepresentation(BitType.HEX);
                    else if(bitType == BitType.HEX) bvModel.setBitRepresentation(BitType.DEC);
                    else bvModel.setBitRepresentation(BitType.BIN);
                    redraw();
                }
            });
        topSubPanel.getAsmModeButton()
                .addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bvModel.setAsmMode(!bvModel.isAsmMode());
                    redraw();
                }
            });

        topSubPanel.getValueSearchPanel().getFIFOName()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JComboBox valueName = topSubPanel.getValueSearchPanel().getValueName();
                        valueName.removeAllItems();
                        topSubPanel.getValueSearchPanel().getValue().setText("");
                        String FIFOName = topSubPanel.getValueSearchPanel().getFIFOName().getSelectedItem().toString();
                        System.out.println(FIFOName);
                        String FIFOType = bvModel.getInterfaceMap().get(FIFOName);
                        System.out.println(FIFOType);
                        List<String> subtypes = showSubtypes(FIFOType);
                        for(String subtype: subtypes){
                            valueName.addItem(subtype);
                        }
                        valueName.setSelectedIndex(0);
                        valueName.revalidate();
                    }
                });

        topSubPanel.getValueSearchPanel().getValueName()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        topSubPanel.getValueSearchPanel().getValue().setText("");
                    }
                });

        topSubPanel.getValueSearchPanel().getSearch()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String FIFOName = topSubPanel.getValueSearchPanel().getFIFOName().getSelectedItem().toString();
                        String valueName = topSubPanel.getValueSearchPanel().getValueName().getSelectedItem().toString();
                        String value =  topSubPanel.getValueSearchPanel().getValue().getText();
                        int dir = (topSubPanel.getValueSearchPanel().getDirection().isSelected()) ? -1 : 1;
                        int cycle = valueFinder(FIFOName,valueName,value, dir);
                        bvModel.setCycle(cycle);
                        redraw();
                    }
                });

        //leftSubPanel handler
        leftSubPanel.getFir()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("first");
                        redraw();

                    }
                });
        leftSubPanel.getPrs()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("previous stall");
                        redraw();
                    }
                });

        leftSubPanel.getPre()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("previous");
                        redraw();
                    }
                });

        leftSubPanel.getNxt()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("next");
                        redraw();
                    }
                });

        leftSubPanel.getNxs()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("next stall");
                        redraw();
                    }
                });

        leftSubPanel.getLst()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cycleFinder("last");
                        redraw();
                    }
                });

        leftSubPanel.getCycleNum()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        bvModel.setCycle(Integer.parseInt(leftSubPanel.getCycleNum().getText()));
                        redraw();
                    }
                });

    }
    private List<String> showSubtypes(String FIFOType){
        HashMap<String, Type> typeMap = bvModel.getTypeMap();
        ArrayList<String> subtypes = new ArrayList<>();
        Type type = typeMap.get(FIFOType);
        if(type == null) return subtypes;

        for(Type t :type.getSub()) {
            List<String> tempSubType =  showSubtypes(t.getIntf());
            subtypes.add(t.getName());
            for(String subtype : tempSubType){
                subtypes.add(subtype);
            }
        }
        return subtypes;
    }
    private boolean compareValue(Instance instance, String valueName, String value){
        String bit = instance.getBit();
        if(bit ==null) return false;
        bit =  BitType.convertBin(bit,bvModel.getBitRepresentation());
        if(instance.getName().equals(valueName)&&bit.equals(value)) return true;
        if(instance.getChildren()==null) return false;
        else{
            for(Instance i : instance.getChildren()) {
                if(compareValue(i, valueName, value)) return true;
            }
        }
        return false;
    }

    private int valueFinder(String FIFOName,String valueName, String value, int dir){
        int cycle = bvModel.getCycle();
        int current = cycle;
        List<HashMap<String, Instance>> timeLine = bvModel.getTimeLine();
        while(cycle > 0 && cycle < timeLine.size()-1){
            cycle += dir;
            if(compareValue(timeLine.get(cycle).get(FIFOName),valueName,value)) return cycle;
        }
        bvModel.setMsg("No result has found\n"+bvModel.getMsg());
        return current;
    }

    private void cycleFinder(String mode){
        int cycle = bvModel.getCycle();
        int result = cycle;
        switch(mode){
            case "first":
                bvModel.setMsg("This is the first cycle\n"+bvModel.getMsg());
                result = 1;
                break;
            case "previous stall":
                result = findStall(cycle,-1);
                if(result==cycle) bvModel.setMsg("There is no hazard before this cycle\n"+bvModel.getMsg()); // 수정
                break;
            case "previous":
                if(cycle == 1) bvModel.setMsg("This is the first cycle\n"+bvModel.getMsg());
                else result--;
                break;
            case "next stall":
                result = findStall(cycle,1);
                if(result==cycle) bvModel.setMsg("There is no hazard after this cycle\n"+bvModel.getMsg());
                break;
            case "next":
                if(cycle == bvModel.getTimeLine().size()-1) bvModel.setMsg("This is the last cycle\n"+bvModel.getMsg());
                else result++;
                break;
            case "last":
                bvModel.setMsg("This is the last cycle\n"+bvModel.getMsg());
                result = bvModel.getTimeLine().size()-1;
                break;
            default:
                result = cycle;
                bvModel.setMsg("button error\n"+bvModel.getMsg());
                break;
        }
        bvModel.setCycle(result);
    }

    public int findStall(int i, int dir) {//이전 또는 이후 stall 발생 위치. i : cycle dir: +1/-1 탐색할 방향.
        HashMap<String, FIFO> fifoInfo;
        int current = i;
        while (i > 0 && i < bvModel.getTimeLine().size()) {
            i+=dir;
            if(hazardStatus(i)) return i;
        }
        return current;
    }

    private boolean hazardStatus(int cycle){

        boolean  isHazard = false;
        int totalHazardCnt = bvModel.getHazardRec().getOrDefault(cycle,0);
        if(totalHazardCnt != 0) isHazard = true;

        return isHazard;
    }

    private void updateMsg(int cycle){
        int totalHazardCnt = findStat(bvModel.getHazardRec(),cycle);
        int controlHazardCnt = findStat(bvModel.getControlHazardRec(),cycle);
        int instCnt = findStat(bvModel.getInstRec(),cycle);

        bvModel.setMsg(bvModel.getMsg()+"Hazards: "+ totalHazardCnt+"\n");
        if(bvModel.canDetectControlHazard()) bvModel.setMsg(bvModel.getMsg()+"control Hazards: "+ controlHazardCnt+"\n");
        bvModel.setMsg(bvModel.getMsg()+"Executed Instructions: "+ instCnt+"\n");
    }


    private int findStat(HashMap<Integer,Integer> hazardRec, int cycle){ // return the most recent statistics.
        int stat;
        while(true) {
            stat = hazardRec.getOrDefault(cycle--, 0);
            if (cycle < 1 || stat != 0) return stat;
        }
    }



    public void redraw(){

        int cycle = bvModel.getCycle();
        boolean asmMode = bvModel.isAsmMode();
        BitType bitType = bvModel.getBitRepresentation();
        HashMap<String,Instance> regFIFOInfo = bvModel.getTimeLine().get(cycle);
        updateMsg(cycle);
        bvView.getContentPane().removeAll();
        bvView.createComponent(bvModel.getFIFOList(),bvModel.getRegList(),bvModel.getRfile(),regFIFOInfo,cycle,bvModel.getMsg(),bitType,asmMode);
        bvView.adjustSize();
        bvView.addComponent();
        addListener();
        bvView.revalidate();
        bvModel.setMsg("");
    }
}
