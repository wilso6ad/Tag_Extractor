import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagExtractorGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private JButton chooseFileButton, chooseStopWordButton, extractButton, saveButton;
    private File textFile;
    private File selectedFile, stopWordsFile;
    private Map<String, Integer> tagFrequencyMap;

    public TagExtractorGUI() {
        setTitle("Tag Extractor");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        JButton openFileButton = new JButton("Open Text File");
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    textFile = fileChooser.getSelectedFile();
                    extractTags();
                }
            }
        });

        JButton openStopWordsButton = new JButton("Open Stop Words File");
        openStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    stopWordsFile = fileChooser.getSelectedFile();
                    extractTags();
                }
            }
        });

        JButton saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTagsToFile();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(openFileButton);
        buttonPanel.add(openStopWordsButton);
        buttonPanel.add(saveTagsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void extractTags() {
        if (textFile == null || stopWordsFile == null) {
            return;
        }

        textArea.setText("");
        textArea.append("Extracting tags from: " + textFile.getName() + "\n");

        Map<String, Integer> tagFrequency = new HashMap<>();
        Set<String> stopWords = loadStopWords();

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                for (String word : words) {
                    if (!stopWords.contains(word)) {
                        tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private Set<String> loadStopWords() {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String word;
            while ((word = reader.readLine()) != null) {
                stopWords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void saveTagsToFile() {
        if (textArea.getText().isEmpty()) {
            return;
        }

        JFileChooser saveFileChooser = new JFileChooser();
        int returnValue = saveFileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File outputFile = saveFileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                writer.println(textArea.getText());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagExtractorGUI().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
