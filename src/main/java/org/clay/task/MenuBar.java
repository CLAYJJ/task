package org.clay.task;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    private KafkaWindow kafkaWindow;
    public MenuBar() {
        add(createToolBox());
    }



    private JMenu createToolBox() {
        JMenu menu = new JMenu("工具箱");
        JMenuItem kafkaItem = new JMenuItem("kafka");
        menu.add(kafkaItem);
        kafkaItem.addActionListener((event)->{
            if (kafkaWindow == null)
                kafkaWindow = new KafkaWindow();
            // 判断kafka window是否存在
            else if (!kafkaWindow.isDisplayable())
                kafkaWindow = new KafkaWindow();


        });

        return menu;
    }
}
