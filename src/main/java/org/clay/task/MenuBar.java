package org.clay.task;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    private KafkaWindow kafkaWindow;
    public MenuBar(JFrame frame) {
        add(createToolBox(frame));
    }



    private JMenu createToolBox(JFrame frame) {
        JMenu menu = new JMenu("工具箱");
        JMenuItem kafkaItem = new JMenuItem("kafka");
        menu.add(kafkaItem);
        kafkaItem.addActionListener((event)->{
            if (kafkaWindow == null)
                kafkaWindow = new KafkaWindow(frame, true);
            // 判断kafka window是否存在
            else if (!kafkaWindow.isDisplayable())
                kafkaWindow = new KafkaWindow(frame, true);


        });

        return menu;
    }
}
