package com.company.GUI;

import com.company.BlueVisualizer;
import com.company.Decoder;
import com.company.FIFO;
import com.company.Instance;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.math.BigInteger;

public class FIFOPanel extends JScrollPane {
    FIFO fifo;
    JTree Ftree;
    static Decoder decoder = new Decoder();
    public FIFOPanel(FIFO fifo, int status, int num,boolean binToHex) {

        setPreferredSize(new Dimension(950/(num), 450));
        if (fifo == null||fifo.getName()==null) {getViewport().setBackground(Color.white); return;}
        this.fifo = fifo;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fifo.getName());
        setChildNode((Instance)fifo,root,binToHex);
        Ftree = new JTree(root);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) Ftree.getCellRenderer();
//        System.out.println(fifo.getName()+" "+fifo.getBit());
        if(status ==0 ) {
            getViewport().setBackground(Color.pink);
            setBorder(new LineBorder(Color.red,5));
            return;
        }else if(status ==1) {
            getViewport().setBackground(Color.pink);
            setBorder(new LineBorder(Color.blue,5));
            return;
        } else if(!(fifo.full)||fifo.empty){
            getViewport().setBackground(Color.PINK);
            return;
        }else if((!fifo.deq)&&(!fifo.enq)){
            getViewport().setBackground(Color.gray);
//            setBorder(new LineBorder(Color.black));
            renderer.setBackgroundNonSelectionColor(Color.gray);
        }else{
            getViewport().setBackground(Color.WHITE);
        }

//        System.out.println(fifo.asm);
        Ftree.setOpaque(false);
        setViewportView(Ftree);
    }

    static void setChildNode(Instance parent,DefaultMutableTreeNode nodeP,Boolean binToHex){
        if(parent.getChildren()==null)  return;
        for(Instance child: parent.getChildren()){
//            if(child.getName().equals("inst")) System.out.println(decoder.decode(child.getBit()));
            DefaultMutableTreeNode nodeC = new DefaultMutableTreeNode(child.getName()+" "+ BlueVisualizer.convertBin(child.getBit(),binToHex));
            nodeP.add(nodeC);
            setChildNode(child,nodeC,binToHex);
        }
        return;
    }



}
