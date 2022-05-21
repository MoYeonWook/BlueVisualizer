package com.company.View.LeftSubPanel;

import com.company.Model.BitType;
import com.company.Model.FIFO;
import com.company.Model.Instance;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.*;
import java.awt.*;

public class FIFOPanel extends JScrollPane{
    private JTree Ftree;
    int panelWidth = 950;
    int panelHeight = 450;
    public FIFOPanel(FIFO fifo, int num, BitType bitType) {

        setPreferredSize(new Dimension(panelWidth/(num), panelHeight));
        if(fifo == null) return;
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
            case Invalid:
                getViewport().setBackground(Color.white);
                getViewport().setLayout(new FlowLayout());
                getViewport().add(new JLabel("Invalid (maybe type)"));
                return;
           default:
                getViewport().setBackground(Color.white);
                return;
        }


        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fifo.getName());
        setChildNode(fifo,root,bitType);
        Ftree = new JTree(root);
        exapndAllNodes(Ftree);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) Ftree.getCellRenderer();
        Ftree.setOpaque(false);
        setViewportView(Ftree);
    }

    private void setChildNode(Instance parent, DefaultMutableTreeNode nodeP, BitType bitType){
        if(parent.getChildren()==null)  return;
        for(Instance child: parent.getChildren()){
            DefaultMutableTreeNode nodeC = new DefaultMutableTreeNode(child.getName()+" "+ BitType.convertBin(child.getBit(),bitType));
            nodeP.add(nodeC);
            setChildNode(child,nodeC,bitType);
        }
    }

    private void exapndAllNodes(JTree tree){
        int j = tree.getRowCount();
        int i = 0;
        while(i<j){
            tree.expandRow(i++);
            j = tree.getRowCount();
        }
    }


}
