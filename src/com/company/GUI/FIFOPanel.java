package com.company.GUI;

import com.company.FIFO;
import com.company.Instance;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;

public class FIFOPanel extends JScrollPane {
    FIFO fifo;
    JTree Ftree;

    public FIFOPanel(FIFO fifo, int status, int num) {

        setPreferredSize(new Dimension(1000/num, 400));
        if (fifo == null||fifo.getName()==null) {
            getViewport().setBackground(Color.WHITE);
            return;
        }else if(status ==0 ) {
            getViewport().setBackground(Color.red);
            return;
        }else if(status ==1) {
            getViewport().setBackground(Color.blue);
            return;
        } else if(!(fifo.full)){
            getViewport().setBackground(Color.PINK);
            return;
        }else if((!fifo.deq)&&(!fifo.enq)){
            getViewport().setBackground(Color.gray);
        }else{
            getViewport().setBackground(Color.WHITE);
        }

        this.fifo = fifo;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fifo.getName());
        setChildNode((Instance)fifo,root);
        Ftree = new JTree(root);
//        Ftree.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//
//            }
//        })
        Ftree.setOpaque(false);
        setViewportView(Ftree);
         // background 설정 다시 필요.

//        getContentPane().setLayout(new BorderLayout());
//        getContentPane().add(new JScrollPane(xTree), "Center");
    }

    static void setChildNode(Instance parent,DefaultMutableTreeNode nodeP){
        if(parent.getChildren()==null) {System.out.println(parent.getName()); return;}
        for(Instance child: parent.getChildren()){
            DefaultMutableTreeNode nodeC = new DefaultMutableTreeNode(child.getName()+" "+child.getBit());
            nodeP.add(nodeC);
            setChildNode(child,nodeC);
        }
        return;
    }
}
