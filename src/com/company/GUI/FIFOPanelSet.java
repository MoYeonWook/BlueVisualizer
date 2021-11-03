package com.company.GUI;

import com.company.FIFO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class FIFOPanelSet extends Panel {

    public FIFOPanelSet(int num, int status, List<String> FIFOList, HashMap<String, FIFO> FIFOInfo){
            setLayout(new FlowLayout());
            Font font =new Font("Verdana",Font.PLAIN,20);
            for(int i=0;i<num; i++){
                String FifoName = FIFOList.get(i);
                JLabel header = new JLabel(FifoName);
                header.setFont(font);
                Panel panel = new Panel();
                panel.setPreferredSize(new Dimension(1100/num,500));
                panel.setLayout(new FlowLayout());
                panel.add(header);
                if(status == i) panel.add(new FIFOPanel(FIFOInfo.get(FifoName),status,num));
                else  panel.add(new FIFOPanel(FIFOInfo.get(FifoName),-1,num));
                add(panel);
            }
    }
}
