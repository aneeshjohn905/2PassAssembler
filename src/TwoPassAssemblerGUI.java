import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TwoPassAssemblerGUI extends JFrame {

    private JTextField inputFileField, optabFileField;
    private JTextArea intermediateArea, symtabArea, outputArea;
    private JButton assembleBtn, quitBtn;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel welcomePanel;

    public TwoPassAssemblerGUI() {
        setTitle("Two-Pass Assembler");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        welcomePanel = createWelcomePanel();
        add(welcomePanel, "Welcome");

        mainPanel = createMainPanel();
        add(mainPanel, "Main");

        cardLayout.show(getContentPane(), "Welcome");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Two Pass Assembler", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setBounds(0, 100, 1920, 50);
        panel.add(welcomeLabel);

        JButton startButton = new JButton("Start");
        startButton.setBackground(new Color(144, 238, 144));
        startButton.setBounds(790, 200, 300, 50);
        startButton.addActionListener(e -> {
            cardLayout.show(getContentPane(), "Main");
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setBackground(new Color(255, 99, 71));
        quitButton.setBounds(790, 270, 300, 50);
        quitButton.addActionListener(e -> {
            System.exit(0);
        });

        panel.add(startButton);
        panel.add(quitButton);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1920, 1080));

        Font font = new Font("Arial", Font.PLAIN, 16);
        Color bgColor = new Color(245, 245, 245);
        Color buttonColor = new Color(60, 179, 113);

        JLabel inputLabel = new JLabel("Input File:");
        inputLabel.setFont(font);
        inputLabel.setBounds(50, 50, 120, 30);
        panel.add(inputLabel);

        inputFileField = new JTextField(30);
        inputFileField.setBounds(180, 50, 400, 30);
        panel.add(inputFileField);

        JButton browseInputBtn = new JButton("Browse");
        browseInputBtn.setFont(font);
        browseInputBtn.setBounds(600, 50, 120, 30);
        browseInputBtn.addActionListener(e -> browseFile(inputFileField));
        panel.add(browseInputBtn);

        JLabel optabLabel = new JLabel("Optab File:");
        optabLabel.setFont(font);
        optabLabel.setBounds(50, 100, 150, 30);
        panel.add(optabLabel);

        optabFileField = new JTextField(30);
        optabFileField.setBounds(180, 100, 400, 30);
        panel.add(optabFileField);

        JButton browseOptabBtn = new JButton("Browse");
        browseOptabBtn.setFont(font);
        browseOptabBtn.setBounds(600, 100, 120, 30);
        browseOptabBtn.addActionListener(e -> browseFile(optabFileField));
        panel.add(browseOptabBtn);

        assembleBtn = new JButton("Assemble");
        assembleBtn.setFont(new Font("Arial", Font.BOLD, 16));
        assembleBtn.setBackground(buttonColor);
        assembleBtn.setForeground(Color.WHITE);
        assembleBtn.setBounds(300, 150, 200, 40);
        assembleBtn.addActionListener(e -> runAssembler());
        panel.add(assembleBtn);

        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        quitBtn.setBackground(new Color(255, 99, 71));
        quitBtn.setForeground(Color.WHITE);
        quitBtn.setBounds(550, 150, 200, 40);
        quitBtn.addActionListener(e -> System.exit(0));
        panel.add(quitBtn);

        JLabel intermediateLabel = new JLabel("Intermediate File Output:");
        intermediateLabel.setFont(font);
        intermediateLabel.setBounds(800, 50, 250, 30);
        panel.add(intermediateLabel);

        JLabel symtabLabel = new JLabel("SymTab Output:");
        symtabLabel.setFont(font);
        symtabLabel.setBounds(1250, 50, 250, 30);
        panel.add(symtabLabel);

        intermediateArea = new JTextArea(8, 70);
        intermediateArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane intermediateScroll = new JScrollPane(intermediateArea);
        intermediateScroll.setBounds(800, 80, 375, 600);
        panel.add(intermediateScroll);

        symtabArea = new JTextArea(8, 70);
        symtabArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane symtabScroll = new JScrollPane(symtabArea);
        symtabScroll.setBounds(1250, 80, 375, 600);
        panel.add(symtabScroll);

        JLabel outputLabel = new JLabel("Object Code Output:");
        outputLabel.setFont(font);
        outputLabel.setBounds(800, 700, 250, 30);
        panel.add(outputLabel);

        outputArea = new JTextArea(7, 70);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBounds(800, 740, 750, 200);
        panel.add(outputScroll);

        panel.setBackground(bgColor);
        return panel;
    }

    private void browseFile(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            field.setText(file.getPath());
        }
    }

    private void runAssembler() {
        String inputFile = inputFileField.getText();
        String optabFile = optabFileField.getText();

        if (inputFile.isEmpty() || optabFile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide both the input file and opcode table file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        assembleBtn.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                TwoPassAssembler assembler = new TwoPassAssembler(inputFile, optabFile);
                try {
                    assembler.loadOptab();
                    assembler.passOne();
                    assembler.passTwo();

                    displayFileContent("intermediate.txt", intermediateArea);
                    displayFileContent("symtab.txt", symtabArea);
                    displayFileContent("output.txt", outputArea);

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(TwoPassAssemblerGUI.this, "Error running the assembler: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            protected void done() {
                assembleBtn.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void displayFileContent(String filename, JTextArea textArea) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        textArea.setText("");
        String line;
        while ((line = reader.readLine()) != null) {
            textArea.append(line + "\n");
        }
        reader.close();
    }


}
