package com.KingFightStudios.KFS_Bot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private static File file;
    private ActionListener tickEvent;
    private Timer tick;
    private Thread bot;
    public static Bot botclass;

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
        tickEvent = evt -> {
            if(botclass != null) {
                try {
                    if(!console.getText().equals(Files.readString(file.toPath()))) {
                        console.setText(Files.readString(file.toPath()));
                    }
                    long uptimeInSeconds = botclass.getUptime() / 1000;
                    long numberOfHours = uptimeInSeconds / (60 * 60);
                    long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
                    long numberOfSeconds = uptimeInSeconds % 60;
                    time.setText(String.format("Uptime:\n%s:%s:%s", numberOfHours, numberOfMinutes, numberOfSeconds));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
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
        power.addActionListener(e -> {
            if (power.isSelected()) {
                try {
                    startBot();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                if(Bot.bot != null) {
                    botclass.shutdown();
                }
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
        membersHolder.removeAll();
        new Thread(() -> {
            for(int i = 0; i < m.size(); i++) {
                try {
                    String[] memberInfo = new String[m.size()];
                    memberInfo[0] = m.get(i).getUser().getName();
                    memberInfo[1] = m.get(i).getUser().getId();
                    memberInfo[2] = m.get(i).getUser().getAvatarUrl();
                    JComboBox<String> member = new JComboBox<>(memberInfo);
                    URL url = new URL(m.get(i).getUser().getAvatarUrl());
                    URLConnection conn = url.openConnection();
                    InputStream in = conn.getInputStream();
                    BufferedImage UserIcon = ImageIO.read(in);
                    membersHolder.add(member);
                    membersHolder.getGraphics().drawImage(UserIcon, i*128, 128, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startBot() throws Exception {
        tick.start();
        file = new File("logs/" + LocalDate.now() + "_" + LocalTime.ofSecondOfDay(LocalTime.now().toSecondOfDay()).toString().replace(":", "-") + ".log");
        PrintStream out = new PrintStream(new FileOutputStream(file));
        System.setErr(out);
        System.setOut(out);
        bot = new Thread(() -> {
            try {
                botclass = new Bot();
                botclass.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        bot.start();
    }
}