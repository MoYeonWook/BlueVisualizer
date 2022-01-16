package com.company.GUI;

import com.company.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.math.BigInteger;

public class FIFOPanel extends JScrollPane{
    private JTree Ftree;
    int panelWidth = 950;
    int panelHeight = 450;
    public FIFOPanel(FIFO fifo, int num,BitType bitType) {

        setPreferredSize(new Dimension(panelWidth/(num), panelHeight));
//        if (fifo == null||fifo.getName()==null) {getViewport().setBackground(Color.white); return;}
        switch(fifo.getStatusType()){
            case Full:
                getViewport().setBackground(Color.white);
                break;
            case StallFull:
                getViewport().setBackground(Color.white);
                setBorder(new LineBorder(Color.black,5));
                break;
            case StallEmpty :
                getViewport().setBackground(Color.pink);
                return;
            case CtrlHazard :
                getViewport().setBackground(Color.pink);
                setBorder(new LineBorder(Color.red,5));
                return;
            default:
                getViewport().setBackground(Color.white);
                return;
        }


        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fifo.getName());
        setChildNode(fifo,root,bitType);
        Ftree = new JTree(root);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) Ftree.getCellRenderer();
        Ftree.setOpaque(false);
        setViewportView(Ftree);
    }

    private void setChildNode(Instance parent, DefaultMutableTreeNode nodeP, BitType bitType){
        if(parent.getChildren()==null)  return;
        for(Instance child: parent.getChildren()){
//            if(child.getName().equals("inst")) System.out.println(decoder.decode(child.getBit()));
            DefaultMutableTreeNode nodeC = new DefaultMutableTreeNode(child.getName()+" "+ BitType.convertBin(child.getBit(),bitType));
            nodeP.add(nodeC);
            setChildNode(child,nodeC,bitType);
        }
    }


}
