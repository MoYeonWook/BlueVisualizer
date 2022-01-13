package com.company;

import com.company.GUI.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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

        //userSettingPanel handler
        userSettingPanel.getSubmit()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Visualizing Chosen data...");
                        bvModel.setFIFOList (userSettingPanel.getManageFIFO().getPosName());
                        bvModel.setRegList(userSettingPanel.getManageReg().getPosName());
                        bvModel.setAsmInFIFO(new String[bvModel.getFIFOList().size()]);
                        for(int i =0;i<32;i++) bvModel.getRfile().add("rfile"+i); // add all the name of cpu register
                        for(int i =1; i<bvModel.getTimeLine().size();i++) bvModel.initializeStat(i); // start at i = 1, cycle 0 has already initialized.
                        bvView.setPreferredSize(bvView.getFrameSize());
                        bvView.pack();
                        redraw();
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

    public void cycleFinder(String mode){
        int cycle = bvModel.getCycle();
        int result = cycle;
        switch(mode){
            case "first":
                result = 1;
                break;
            case "previous stall":
                result = findStall(cycle,-1);
                if(result==cycle) bvModel.setMsg("This is the first stall of your program"); // 수정
                break;
            case "previous":
                if(cycle == 1) bvModel.setMsg("This is the first cycle");
                else result--;
                break;
            case "next stall":
                result = findStall(cycle,1);
                if(result==cycle) bvModel.setMsg("This is the last stall of your program");
                break;
            case "next":
                if(cycle == bvModel.getTimeLine().size()-1) bvModel.setMsg("This is the last cycle");
                else result++;
                break;
            case "last":
                result = bvModel.getTimeLine().size()-1;
                break;
            default:
                result = cycle;
                bvModel.setMsg("button error");
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
        int controlHazardCnt;
        int instCnt;

        if(totalHazardCnt != 0) isHazard = true;

        totalHazardCnt = findStat(bvModel.getHazardRec(),cycle);
        controlHazardCnt = findStat(bvModel.getControlHazardRec(),cycle);
        instCnt = findStat(bvModel.getInstRec(),cycle);

        if(bvModel.canDetectControlHazard())bvModel.setMsg(bvModel.getMsg()+"Total control Hazards: "+ controlHazardCnt+"\n");
        bvModel.setMsg(bvModel.getMsg()+"Total data Hazards: "+ totalHazardCnt+"\n");
        bvModel.setMsg(bvModel.getMsg()+"Total Instructions: "+ instCnt+"\n");

        return isHazard;
    }

    private int findStat(HashMap<Integer,Integer> hazardRec, int cycle){
        int stat;
        while(true) {
            stat = hazardRec.getOrDefault(cycle--, 0);
            if (stat > 0 || cycle == 0) return stat;
        }
    }


    public void redraw(){
        int cycle = bvModel.getCycle();
        boolean asmMode = bvModel.isAsmMode();
        BitType bitType = bvModel.getBitRepresentation();
        HashMap<String,Instance> regFIFOInfo = bvModel.getTimeLine().get(cycle);
        bvView.getContentPane().removeAll();
        bvView.createComponent(bvModel.getFIFOList(),bvModel.getRegList(),bvModel.getRfile(),regFIFOInfo,cycle,bvModel.getMsg(),bitType,asmMode);
        bvView.adjustSize();
        bvView.addComponent();
        addListener();
        bvView.revalidate();
        bvModel.setMsg("");
    }
}
