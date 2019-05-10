package org.clay.task;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.clay.task.entity.Task;
import org.clay.task.mapper.TaskMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class MainWindow extends JFrame {


    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_SIMPLE_DATE_FORMAT = ThreadLocal.withInitial(()->
            new SimpleDateFormat("yyyy-MM-dd"));
    private static final SqlSessionFactory sessionFactory;

    static {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        sessionFactory = builder.build(MainWindow.class.getClassLoader().getResourceAsStream("mybatis.xml"));

    }

    private String cachedId;
    private String cachedMarkTime;
    /**
     * 构造方法，初始化窗口内容
     *
     */
    public MainWindow() throws HeadlessException {
        setTitle("周计划");
        setIconImage(getImageIcon("/image/avenger.ico").getImage());
        setDefaultLookAndFeelDecorated(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String content = "";
        //  从数据库中查询出当前的数据
        try(SqlSession session = sessionFactory.openSession(true)){
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            Task task = new Task();
            SimpleDateFormat dateFormat = THREAD_LOCAL_SIMPLE_DATE_FORMAT.get();
            task.setMarkTime(dateFormat.format(new Date()));
            List<Task> tasks = taskMapper.findAll(task);
            if (tasks != null && tasks.size() == 1) {
                content = tasks.get(0).getContent();
                cachedId = tasks.get(0).getId();
            }
        }



        JPanel panel1 = new JPanel();
        JLabel dateLabel = new JLabel("日期:");
        JComboBox<String> comboBox = new JComboBox<>(new Vector<String>(){
            {
                add("周一");
                add("周二");
                add("周三");
                add("周四");
                add("周五");
                add("周六");
                add("周日");
            }
        });
        cachedMarkTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int todayIndex = LocalDate.now().getDayOfWeek().getValue() - 1;
        comboBox.setSelectedIndex(todayIndex);

        panel1.add(dateLabel);
        panel1.add(comboBox);

        JPanel panel2 = new JPanel();
        JLabel taskLabel = new JLabel("任务:");
        taskLabel.setBounds(0, 0, 20, 20);
        panel2.add(taskLabel);
        JTextArea textArea = new JTextArea(content,20, 30);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        Dimension size = textArea.getPreferredSize();
        scrollPane.setSize(size.width, size.height);
        panel2.add(scrollPane);


        JPanel panel3 = new JPanel();
        JButton saveButton = new JButton("保存");

        JButton exitButton = new JButton("退出");
        exitButton.addActionListener((event)->{
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        panel3.add(saveButton);
        panel3.add(exitButton);

        add(panel1, BorderLayout.NORTH);
        add(panel2, BorderLayout.CENTER);
        add(panel3, BorderLayout.SOUTH);
        // 给下拉框绑定监听器
        comboBox.addItemListener((itemEvent)->{
            if (ItemEvent.SELECTED == itemEvent.getStateChange()){
                // 计算出下拉框中数据对应的实际日期
                cachedMarkTime = LocalDateTime.now().plusDays(comboBox.getSelectedIndex() - todayIndex).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Task task = new Task();
                task.setMarkTime(cachedMarkTime);
                // 根据实际日期到数据库中查询出对应的记录，并设置文本域中的值
                try (SqlSession session = sessionFactory.openSession(true)){
                    TaskMapper taskMapper = session.getMapper(TaskMapper.class);
                    List<Task> tasks = taskMapper.findAll(task);
                    if (tasks != null && tasks.size() == 1) {
                        textArea.setText(tasks.get(0).getContent());
                        cachedId = tasks.get(0).getId();
                    } else {
                        textArea.setText("");
                        cachedId = null;
                    }
                }
            }
        });


        saveButton.addActionListener((event)->{
            try{
                // 新增
                if (cachedId == null) {
                    Task task = new Task();
                    task.setId(UUID.randomUUID().toString());
                    task.setMarkTime(cachedMarkTime);
                    task.setContent(textArea.getText());
                    task.setCreateTime(new Date());
                    task.setModifyTime(new Date());
                    try (SqlSession session = sessionFactory.openSession(true)){
                        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
                        taskMapper.insert(task);
                    }
                } else {
                    // 修改
                    Task task = new Task();
                    task.setId(cachedId);
                    task.setModifyTime(new Date());
                    task.setContent(textArea.getText());
                    try (SqlSession session = sessionFactory.openSession(true)){
                        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
                        taskMapper.modify(task);
                    }
                }
                JOptionPane.showMessageDialog(this, "保存成功!", "提示", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "保存失败!", "提示", JOptionPane.ERROR_MESSAGE);
            }

        });

        setJMenuBar(new MenuBar());
        // 自适应各组件尺寸
        pack();
        // 设置窗口位置在屏幕中间
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private ImageIcon getImageIcon(String path, String description) {
        URL url = getClass().getResource(path);
        if (url != null)
            return new ImageIcon(url, description);
        else
            return null;
    }
    private ImageIcon getImageIcon(String path) {
        URL url = getClass().getResource(path);
        if (url != null)
            return new ImageIcon(url);
        else
            return null;
    }
}
