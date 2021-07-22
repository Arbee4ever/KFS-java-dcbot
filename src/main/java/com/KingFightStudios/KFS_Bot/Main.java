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
import java.util.List;

public class Main {
    private final static JFrame window = new JFrame("KFS-Bot");
    private final static Image image = window.getToolkit().getImage("src/main/resources/icon.png");
    private final static Image bot_on = window.getToolkit().getImage("src/main/resources/bot_on.png");
    private final static Image bot_off = window.getToolkit().getImage("src/main/resources/bot_off.png");
    private final static JTextArea console = new JTextArea();
    private final static JCheckBox power = new JCheckBox();
    private final static JTextPane time = new JTextPane();
    private final static JPanel mainPanel = new JPanel();
    private static JPanel membersHolder = new JPanel();
    private static JScrollPane membersPanel;
    private final static JPanel leftPanel = new JPanel();
    private final static JPanel controlsPanel = new JPanel();
    private static File file;
    public static Process process;
    private final static ActionListener tickEvent = evt -> {
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
    };
    private final static Timer tick = new Timer(0, tickEvent);

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        window.getContentPane().add(mainPanel);

        mainPanel.setLayout(new GridLayout(0, 2));
        leftPanel.setLayout(new GridLayout(2, 0));
        controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        console.setEditable(false);
        console.setLineWrap(true);
        console.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane consolePanel = new JScrollPane(console);
        consolePanel.setBorder(new TitledBorder("Console"));
        consolePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        membersHolder = new JPanel();
        membersPanel = new JScrollPane(membersHolder);
        membersPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        power.setSelected(false);
        power.setIcon(new ImageIcon(bot_off));
        power.setSelectedIcon(new ImageIcon(bot_on));
        power.addActionListener(e -> {
            if (power.isSelected()) {
                try {
                    secondJVM();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                process.destroyForcibly();
                tick.stop();
                console.append("Bot successfully stopped!");
            }
        });

        time.setText("Uptime:\r00:00:00");
        time.setBackground(new Color(0xFFEEEEEE));
        controlsPanel.add(power);
        controlsPanel.add(time);
        leftPanel.add(controlsPanel);
        leftPanel.add(membersPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(consolePanel);
        window.pack();
        WindowListener windowListener = new WindowControls();
        window.setBounds(0, 0, 1000, 600);
        window.setExtendedState(Frame.MAXIMIZED_BOTH);
        window.setIconImage(image);
        window.addWindowListener(windowListener);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void members(List<Member> m) {
        String[] memberInfo = new String[m.size()];
        for(int i = 0; i < m.size(); i++) {
            memberInfo[i] = m.get(i).getUser().getName();
            JLabel member = new JLabel(memberInfo[i]);
            membersHolder.add(member);
        }
    }

    public static Process secondJVM() throws Exception {
        tick.start();
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        file = new File("logs/" + LocalDate.now() + "_" + LocalTime.ofSecondOfDay(LocalTime.now().toSecondOfDay()).toString().replace(":", "-") + ".log");

        ProcessBuilder pb = new ProcessBuilder(path, "-cp", classpath, Bot.class.getName());
        pb.redirectErrorStream(true);
        pb.redirectOutput(file);
        process = pb.start();
        assert pb.redirectInput() == ProcessBuilder.Redirect.PIPE;
        assert pb.redirectOutput().file() == file;
        assert process.getInputStream().read() == -1;
        return process;
    }
}