package com.company.GUI;

import com.company.FIFO;
import com.company.Instance;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class FIFOPanelSet extends Panel {

    public FIFOPanelSet(int num, int status, List<String> FIFOList, HashMap<String, Instance> FIFOInfo,Boolean binToHex){
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(1000,500));
            Font font =new Font("Franklin Gothic Book",Font.PLAIN,20);
            for(int i=0;i<num; i++){
                String FifoName = FIFOList.get(i);
                JLabel header = new JLabel(FifoName);
//                System.out.println(FifoName);
                header.setFont(font);
                Panel panel = new Panel();
                panel.setPreferredSize(new Dimension(950/num+5,500));
                panel.setLayout(new FlowLayout());
                panel.add(header);
                if(status == i) panel.add(new FIFOPanel((FIFO)FIFOInfo.get(FifoName),status,num,binToHex));
                else  panel.add(new FIFOPanel((FIFO)FIFOInfo.get(FifoName),-1,num,binToHex));
                add(panel);
            }
    }
}
