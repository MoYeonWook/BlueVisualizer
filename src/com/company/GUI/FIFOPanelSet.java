package com.company.GUI;

import com.company.BitType;
import com.company.FIFO;
import com.company.Instance;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class FIFOPanelSet extends Panel {

    public FIFOPanelSet(int status, List<String> FIFOList, HashMap<String, Instance> FIFOInfo, BitType bitType, Boolean asmMode){
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(1000,500));
            Font font =new Font("Franklin Gothic Book",Font.PLAIN,20);
            int num = FIFOList.size();
            for(int i=0;i<num; i++){
                String FifoName = FIFOList.get(i);
                FIFO fifo = (FIFO) FIFOInfo.get(FifoName);
                JLabel header = new JLabel(FifoName);
                JLabel asmCode = (fifo==null) ? new JLabel("empty"):new JLabel(fifo.asm);
//                System.out.println(FifoName);
                header.setFont(font);
                asmCode.setFont(font);
                Panel panel = new Panel();
                panel.setPreferredSize(new Dimension(950/num+5,500));
                panel.setLayout(new FlowLayout());
                if(asmMode) panel.add(asmCode);
                else panel.add(header);
                if(status == i) panel.add(new FIFOPanel(fifo,status,num,bitType));
                else  panel.add(new FIFOPanel(fifo,-1,num,bitType));
                add(panel);
            }
    }
}
