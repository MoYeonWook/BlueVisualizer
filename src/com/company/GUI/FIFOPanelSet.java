package com.company.GUI;

import com.company.BitType;
import com.company.FIFO;
import com.company.Instance;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class FIFOPanelSet extends Panel {
    int totalBgPanelWidth = 950;
    int margin = 5;
    int bgPanelHeight = 500;
    Dimension panelSetSize = new Dimension(1000,500);
    Dimension namePanelSize = new Dimension(200,30);

    public FIFOPanelSet( List<String> FIFOList, HashMap<String, Instance> FIFOInfo, BitType bitType, Boolean asmMode){
            setLayout(new FlowLayout());
            setPreferredSize(panelSetSize);
            Font FIFONameFont = new Font("Franklin Gothic Book",Font.PLAIN,20);
            Font asmFont = new Font("",Font.ITALIC,15);
            int num = FIFOList.size();
        for (String s : FIFOList) {
            //declaration
            JPanel bgPanel = new JPanel();
            JPanel namePanel = new JPanel();

            String FifoName = s;
            FIFO fifo = (FIFO) FIFOInfo.get(FifoName);
            JLabel header = new JLabel(FifoName);
            JLabel asmCode = (fifo == null) ? new JLabel("empty") : new JLabel(fifo.asm);

            //setting
            header.setFont(FIFONameFont);
            asmCode.setFont(asmFont);
            namePanel.setPreferredSize(new Dimension(namePanelSize));
            bgPanel.setPreferredSize(new Dimension(totalBgPanelWidth / num + margin, bgPanelHeight));
            bgPanel.setLayout(new FlowLayout());

//                asmCode.setFont(font);

            //add components
            if (asmMode) {
                namePanel.setLayout(new BorderLayout());
                namePanel.add(header, BorderLayout.WEST);
                namePanel.add(asmCode, BorderLayout.EAST);
            } else namePanel.add(header);
            bgPanel.add(namePanel);
            bgPanel.add(new FIFOPanel(fifo, num, bitType));
            add(bgPanel);
        }
    }
}
