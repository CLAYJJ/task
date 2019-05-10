package org.clay.task;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.ProgressBar;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.clay.task.util.CommonUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class KafkaWindow extends JDialog {
    public KafkaWindow(JFrame frame, boolean modal) {
        super(frame, modal);
        setTitle("kafka");
        // 下面两个语句的顺序不能颠倒，否则对话框不会居中到屏幕中间
        setSize(800, 300);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel panel1 = new JPanel();
        JLabel zkLabel = new JLabel("zookeeper address: ");
        JTextField zkInput = new JTextField(20);
        JButton connectButton = new JButton("连接");

        panel1.add(zkLabel);
        panel1.add(zkInput);
        panel1.add(connectButton);

        JPanel panel2 = new JPanel();
        JLabel brokersLabel = new JLabel("broker list: ");
        JList<String> list = new JList<>();
        panel2.add(brokersLabel);
        panel2.add(list);
        connectButton.addActionListener((event)->{
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // 拿到zkInput中的值，连接zk，然后获取/brokers中的值，并且放到brokerList框中展示
            Vector<String> brokers = new Vector<>();
            String zkAddress = zkInput.getText();
            if (zkAddress == null || "".equals(zkAddress)) {
                setCursor(null);
                JOptionPane.showMessageDialog(this, "请填写zookeeper地址");
                return;
            }
            CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new RetryOneTime(1000));
            try {
                client.start();

                // TODO 此处添加进度条
                List<String> ids = client.getChildren().forPath("/brokers/ids");
                if (ids != null) {
                    ids.forEach((id)->{
                        try {
                            byte[] bytes = client.getData().forPath("/brokers/ids/" + id);
                            JSONObject parse = (JSONObject)JSONObject.parse(bytes);
                            String endpoints = parse.getString("endpoints");
                            brokers.add(endpoints);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "提示", JOptionPane.WARNING_MESSAGE, CommonUtils.createImageIcon("/image/spiderman.ico"));
            }finally {
                setCursor(null);
                if (client != null)
                    client.close();
            }
            list.setListData(brokers);
        });
        add(panel1, BorderLayout.NORTH);
        add(panel2, BorderLayout.SOUTH);
        setVisible(true);
    }


    public KafkaWindow(Frame owner, String title) {
        super(owner, title);

    }
}
