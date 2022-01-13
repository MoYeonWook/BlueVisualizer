package com.company.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class InfoMatching extends JPanel {// step1.fifo matching.
    Integer[] numFifo = {2,3,4};
    Panel upperPannel = new Panel();
    Panel middlePanel = new Panel();
    List<String> posName = new ArrayList<String>();

    public List<String> getPosName() {
        return posName;
    }

    public InfoMatching(String name,HashSet<String> set){
        String[] choice = set.toArray(new String[set.size()]);
        Arrays.sort(choice);
        //declare
        JLabel content = new JLabel(name);
        JComboBox<Integer> numInput = new JComboBox<>(numFifo);


        // option
        //numInput.setSize(20,10);
        upperPannel.setLayout(new FlowLayout(FlowLayout.LEFT,10,20));
        upperPannel.setSize(250,150);
        middlePanel.setLayout(new GridLayout(4,1));
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(240,300));
        setVisible(true);

        //add
        upperPannel.add(content);
        upperPannel.add(numInput);
        add(upperPannel);
        add(middlePanel);

        redraw(numFifo[0],choice);

        numInput.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int num = (int)numInput.getSelectedItem();
                redraw(num,choice);
            }
        });

    }

    public void redraw(int num, String[] choice){
        middlePanel.removeAll();

        posName = new ArrayList<>();
        for(int i=0; i<num; i++) {
            Panel entry = new Panel();
            String labelName = String.valueOf(i + 1) + " ";
            entry.add(new JLabel(labelName));
            posName.add(choice[0]);
            entry.setPreferredSize(new Dimension(250, 50));
            JComboBox<String> choiceList = new JComboBox<>(choice);
            int current = i;

            choiceList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    posName.set(current, (String) choiceList.getSelectedItem());
                }
            });
            entry.add(choiceList);
            middlePanel.add(entry);
        }
        revalidate();
    }
}
