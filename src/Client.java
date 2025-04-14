
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;


public class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Client Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // TOP PANEL: Host / Port / Connect
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(163, 200, 243));

        JTextField hostField = new JTextField("localhost", 10);
        JTextField portField = new JTextField(Server.getport().trim(), 5);
        JButton connectButton = new JButton("Connecter");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectButton.setBackground(new Color(0, 173, 213));
        connectButton.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // CENTER: Chat messages area
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM: Input field + Send button
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton sendButton = new JButton("Envoyer");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(255, 255, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setEnabled(false); // Désactivé tant que la connexion n'est pas établie

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Composants réseau (wrapped in array to allow mutation inside listeners)
        final Socket[] client = new Socket[1];
        final BufferedReader[] buffer = new BufferedReader[1];
        final PrintWriter[] writer = new PrintWriter[1];

        // CONNECT BUTTON
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String host = hostField.getText().trim();
                    int port = Integer.parseInt(portField.getText().trim());

                    client[0] = new Socket(host, port);
                    buffer[0] = new BufferedReader(new InputStreamReader(client[0].getInputStream(), "UTF-8"));
                    writer[0] = new PrintWriter(client[0].getOutputStream(), true);

                    messageArea.append("-> Connecté au serveur " + host + " sur le port " + port + "\n");
                    sendButton.setEnabled(true);
                    sendButton.setBackground(new Color(0, 173, 213));
                    // Lancer un thread pour recevoir les messages du serveur
                    new Thread(() -> {
                        try {
                            String serverMessage;
                            while ((serverMessage = buffer[0].readLine()) != null) {
                                messageArea.append(serverMessage + "\n");
                            }
                        } catch (IOException ex) {
                            messageArea.append("-> Connexion perdue.\n");
                        }
                    }).start();

                } catch (Exception ex) {
                    messageArea.append("-> Erreur de connexion : " + ex.getMessage() + "\n");
                }
            }
        });

        // SEND BUTTON
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText().trim();

                if (!message.isEmpty()) {
                    if (message.startsWith("/prv")) {
                        String[] parts = message.split(" ", 3);
                        String id = parts[1];

                        if (parts.length < 3 || (parts[1].matches("[a-zA-Z]+")) ){
                            messageArea.append("-> Syntaxe invalide. Utilisez: /prv <id> <message>\n");

                        } else {

//                            if (Server.idMatchesSocket(id)==false) {
                            messageArea.append("Vous à " + parts[1] + " => " + parts[2] +"\n");
                            writer[0].println(message);
                        }

                    } else {
                        messageArea.append("Vous => " + message + "\n");
                        writer[0].println(message);
                    }
                    inputField.setText("");
                }
            }
        });
        hostField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectButton.doClick(); // Simulate clicking the connect button
                }
            }
        });

        portField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectButton.doClick(); // Simulate clicking the connect button
                }
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick(); // Simulate clicking the send button
                }
            }
        });

        // Show GUI
        frame.setVisible(true);

    }
}
