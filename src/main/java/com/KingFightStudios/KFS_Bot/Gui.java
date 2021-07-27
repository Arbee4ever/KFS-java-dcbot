package com.KingFightStudios.KFS_Bot;

import net.dv8tion.jda.api.entities.Member;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class Gui extends JFrame {
    private final Image image = getToolkit().getImage("src/main/resources/icon.png");
    private final Image bot_on = getToolkit().getImage("src/main/resources/bot_on.png");
    private final Image bot_off = getToolkit().getImage("src/main/resources/bot_off.png");
    private JTextArea console;
    private JCheckBox power;
    private JTextPane time;
    private JPanel mainPanel;
    private JPanel membersHolder;
    private JScrollPane membersPanel;
    private JPanel leftPanel;
    private JPanel controlsPanel;
    private File file;
    public Process process;
    private ActionListener tickEvent;
    private Timer tick;

    public Gui() {
        super("KFS-Bot");
        console = new JTextArea();
        power = new JCheckBox();
        time = new JTextPane();
        mainPanel = new JPanel();
        leftPanel = new JPanel();
        controlsPanel = new JPanel();
        membersHolder = new JPanel();
        membersPanel = new JScrollPane(membersHolder);
        tickEvent = evt -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(process != null) {
                    try {
                        if(!console.getText().equals(Files.readString(file.toPath()))) {
                            console.setText(Files.readString(file.toPath()));
                        }
                        if(process != null) {
                            int startTimeOfDay = process.info().startInstant().get().atZone(ZoneId.of("+2")).toLocalTime().toSecondOfDay();
                            int nowTimeOfDay = LocalTime.now().toSecondOfDay();
                            int uptime = nowTimeOfDay - startTimeOfDay;
                            time.setText("Uptime:\r" + LocalTime.ofSecondOfDay(uptime));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tick = new Timer(0, tickEvent);
    }

    public void init() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        getContentPane().add(mainPanel);

        mainPanel.setLayout(new GridLayout(0, 2));
        leftPanel.setLayout(new GridLayout(2, 0));
        controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        console.setEditable(false);
        console.setLineWrap(false);
        console.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane consolePanel = new JScrollPane(console);
        consolePanel.setBorder(new TitledBorder("Console"));
        consolePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        consolePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        membersPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        power.setSelected(false);
        power.setIcon(new ImageIcon(bot_off));
        power.setSelectedIcon(new ImageIcon(bot_on));
        power.addActionListener(e -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(power.isSelected())

                {
                    try {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    secondJVM();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else

                {
                    process.destroyForcibly();
                    tick.stop();
                    console.append("Bot successfully stopped!");
                }
            }
        }));

        time.setText("Uptime:\r00:00:00");
        time.setBackground(new Color(0xFFEEEEEE));
        controlsPanel.add(power);
        controlsPanel.add(time);
        leftPanel.add(controlsPanel);
        leftPanel.add(membersPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(consolePanel);
        pack();
        WindowListener windowListener = new WindowControls();
        setBounds(0, 0, 1000, 600);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setIconImage(image);
        addWindowListener(windowListener);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void members(java.util.List<Member> m) {
        String[] memberInfo = new String[m.size()];
        for(int i = 0; i < m.size(); i++) {
            memberInfo[i] = m.get(i).getUser().getName();
            System.out.println(memberInfo[i] + Thread.currentThread());
        }
        JComboBox<String> member = new JComboBox<>(memberInfo);
        membersHolder.add(member);
    }

    public void secondJVM() throws Exception {
        tick.start();
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        file = new File("logs/" + LocalDate.now() + "_" + LocalTime.ofSecondOfDay(LocalTime.now().toSecondOfDay()).toString().replace(":", "-") + ".log");

        ProcessBuilder pb = new ProcessBuilder(path, "-cp", classpath, Bot.class.getName());
        pb.redirectErrorStream(true);
        pb.redirectOutput(file);
        process = pb.start();
    }
}
