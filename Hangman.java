import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Hangman extends JFrame {
    // Fields from your .form
    private JTextField word;
    private JButton aButton;
    private JButton cButton;
    private JButton bButton;
    private JButton dButton;
    private JButton eButton;
    private JButton fButton;
    private JButton gButton;
    private JButton hButton;
    private JButton iButton;
    private JButton lButton;
    private JButton kButton;
    private JButton jButton;
    private JButton mButton;
    private JButton nButton;
    private JButton oButton;
    private JButton pButton;
    private JButton qButton;
    private JButton rButton;
    private JButton sButton;
    private JButton tButton;
    private JButton uButton;
    private JButton vButton;
    private JButton wButton;
    private JButton xButton;
    private JButton yButton;
    private JButton zButton;
    private JButton hintButton;
    private JCheckBox showAlreadyWrittenLettersCheckBox;
    private JLabel hangmanpics;
    private JTextArea AlreadyWrittenLetters;
    private JButton restartbutton;
    private JComboBox<String> maxNumberofAttempts;
    private JTextArea commentatorArea;

    // Neue Felder für Fehleranzeige
    private JTextField mistakescount;
    private JLabel mistakescountLabel;

    // Root panel from your .form
    private JPanel mainPanel;

    // -- Game Logic Fields --
    private final ArrayList<String> wordList = new ArrayList<>();
    private String solutionWord;
    private char[] displayedWord;
    private int mistakes;
    private int maxMistakes = 9;
    private final HashSet<Character> guessedLetters = new HashSet<>();

    private final ImageIcon[] hangmanImages = new ImageIcon[10];

    public Hangman() {
        // 1) Hangman-Bilder laden
        loadImages();

        // 2) Textfeld word nur lesbar
        word.setEditable(false);

        // 3) ComboBox für Versuche einrichten
        setupMaxAttemptsComboBox();

        // 4) Wörter aus Datei laden
        String filePath = "C:\\Users\\ENTW1\\Desktop\\Schule\\2.Klasse\\INF\\Wordlist.txt";
        loadWordsFromFile(filePath);
        if (wordList.isEmpty()) {
            commentatorArea.setText("Keine Wörter geladen! Verwende Standardwortliste.");
            wordList.addAll(Arrays.asList("JAVA", "COMPUTER", "PROGRAMMING", "HANGMAN", "KEYBOARD", "OBJECT"));
        }

        // 5) Buchstaben-Buttons einrichten
        setupLetterButtons();

        // 6) Checkbox für bereits geratene Buchstaben
        showAlreadyWrittenLettersCheckBox.setSelected(false);
        AlreadyWrittenLetters.setVisible(false);
        showAlreadyWrittenLettersCheckBox.addActionListener(e -> {
            boolean visible = showAlreadyWrittenLettersCheckBox.isSelected();
            AlreadyWrittenLetters.setVisible(visible);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        // 7) Restart-Button
        restartbutton.addActionListener(e -> startNewGame());

        // 8) Hint-Button
        hintButton.addActionListener(e -> giveHint());

        // 9) Fehler-Anzeige initialisieren
        mistakescount.setEditable(false);
        mistakescount.setText("0");

        // 10) Spiel starten
        startNewGame();
    }

    /**
     * Nur Werte 1 bis 9 für die ComboBox zulassen, Voreinstellung auf 9.
     */
    private void setupMaxAttemptsComboBox() {
        // Nur ausfüllen, wenn noch nichts drin ist
        if (maxNumberofAttempts.getItemCount() == 0) {
            for (int i = 1; i <= 9; i++) {
                maxNumberofAttempts.addItem(String.valueOf(i));
            }
            maxNumberofAttempts.setSelectedItem("9");
        }
        maxNumberofAttempts.addActionListener(e -> {
            String selected = (String) maxNumberofAttempts.getSelectedItem();
            try {
                int val = Integer.parseInt(selected);
                // Falls jemand manuell was anderes reinschreibt, clampen wir den Wert
                if (val < 1) val = 1;
                if (val > 9) val = 9;
                maxMistakes = val;
                commentatorArea.setText("Max attempts set to: " + maxMistakes);
            } catch (NumberFormatException ex) {
                commentatorArea.setText("Invalid number for attempts.");
            }
        });
    }

    // Lädt Hangman Bilder ins Array hangmanImages
    private void loadImages() {
        for (int i = 0; i < 10; i++) {
            hangmanImages[i] = new ImageIcon(
                    "C:\\Users\\ENTW1\\Desktop\\Schule\\2.Klasse\\INF\\bilder_ausgabe\\hangman\\hangman" + (i + 1) + ".png"
            );
        }
    }

    // Wörter aus der Wordlist.txt werden zur Porgramm-wordlist hinzugefügt
    private void loadWordsFromFile(String filePath) {
        wordList.clear();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim().toUpperCase();
                if (!line.isEmpty()) {
                    wordList.add(line);
                }
            }
            commentatorArea.setText("Loaded " + wordList.size() + " words from file.");
        } catch (FileNotFoundException e) {
            commentatorArea.setText("File not found: " + filePath);
        }
    }

    private void setupLetterButtons() {
        aButton.addActionListener(e -> handleGuess('A'));
        bButton.addActionListener(e -> handleGuess('B'));
        cButton.addActionListener(e -> handleGuess('C'));
        dButton.addActionListener(e -> handleGuess('D'));
        eButton.addActionListener(e -> handleGuess('E'));
        fButton.addActionListener(e -> handleGuess('F'));
        gButton.addActionListener(e -> handleGuess('G'));
        hButton.addActionListener(e -> handleGuess('H'));
        iButton.addActionListener(e -> handleGuess('I'));
        jButton.addActionListener(e -> handleGuess('J'));
        kButton.addActionListener(e -> handleGuess('K'));
        lButton.addActionListener(e -> handleGuess('L'));
        mButton.addActionListener(e -> handleGuess('M'));
        nButton.addActionListener(e -> handleGuess('N'));
        oButton.addActionListener(e -> handleGuess('O'));
        pButton.addActionListener(e -> handleGuess('P'));
        qButton.addActionListener(e -> handleGuess('Q'));
        rButton.addActionListener(e -> handleGuess('R'));
        sButton.addActionListener(e -> handleGuess('S'));
        tButton.addActionListener(e -> handleGuess('T'));
        uButton.addActionListener(e -> handleGuess('U'));
        vButton.addActionListener(e -> handleGuess('V'));
        wButton.addActionListener(e -> handleGuess('W'));
        xButton.addActionListener(e -> handleGuess('X'));
        yButton.addActionListener(e -> handleGuess('Y'));
        zButton.addActionListener(e -> handleGuess('Z'));
    }

    private void startNewGame() {
        // Versuche aus ComboBox lesen
        String selected = (String) maxNumberofAttempts.getSelectedItem();
        try {
            int val = Integer.parseInt(selected);
            if (val < 1) val = 1;
            if (val > 9) val = 9;
            maxMistakes = val;
        } catch (NumberFormatException ex) {
            maxMistakes = 9;
        }

        // Zufälliges Wort aus der Wordlist nehmen
        Random rand = new Random();
        solutionWord = wordList.get(rand.nextInt(wordList.size())).toUpperCase();

        // displayedWord mit Unterstrichen
        displayedWord = new char[solutionWord.length()];
        for (int i = 0; i < solutionWord.length(); i++) {
            displayedWord[i] = '_';
        }

        // Fehler und geratene Buchstaben zurücksetzen
        mistakes = 0;
        guessedLetters.clear();
        AlreadyWrittenLetters.setText("");
        commentatorArea.setText("New game started. Good luck!");
        hangmanpics.setIcon(null);

        // Fehleranzeige zurücksetzen
        mistakescount.setText("0");

        // Buttons wieder aktivieren
        enableAllLetterButtons();

        // Anzeige aktualisieren
        updateWordDisplay();
        updateHangmanImage();
    }

    private void handleGuess(char letter) {
        letter = Character.toUpperCase(letter);

        // Wenn schon geraten, zählt es als Fehler
        if (guessedLetters.contains(letter)) {
            mistakes++;
            mistakescount.setText(String.valueOf(mistakes));
            commentatorArea.setText("You already guessed: " + letter + ". Mistake counted!");
            updateHangmanImage();
            if (mistakes >= maxMistakes) {
                commentatorArea.setText("Game Over! The word was: " + solutionWord);
                disableAllLetterButtons();
            }
            return;
        }

        guessedLetters.add(letter);
        AlreadyWrittenLetters.append(letter + " ");

        boolean found = false;
        // Iteriert durch solutionWord, ersetzt passende Unterstriche und setzt found auf true
        for (int i = 0; i < solutionWord.length(); i++) {
            if (solutionWord.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
        }

        // überprüfen des booleans found auf richtiger Versuch oder Falscher
        if (!found) {
            mistakes++;
            mistakescount.setText(String.valueOf(mistakes));
            commentatorArea.setText("Wrong guess: " + letter);
        } else {
            commentatorArea.setText("Good guess: " + letter);
        }

        updateWordDisplay();
        updateHangmanImage();

        // Sieg oder Niederlagenausgabe in der commentatorarea
        if (mistakes >= maxMistakes) {
            commentatorArea.setText("Game Over! The word was: " + solutionWord);
            disableAllLetterButtons();
        } else if (isWordGuessed()) {
            commentatorArea.setText("You guessed the word: " + solutionWord + "!");
            disableAllLetterButtons();
        }
    }

    // deckt einen zufälligen Unterstrich im Wort auf als Hinweis
    private void giveHint() {
        List<Integer> hiddenIndices = new ArrayList<>();
        for (int i = 0; i < displayedWord.length; i++) {
            if (displayedWord[i] == '_') {
                hiddenIndices.add(i);
            }
        }
        if (hiddenIndices.isEmpty()) {
            commentatorArea.setText("No hint needed; all letters revealed!");
            return;
        }
        Random rand = new Random();
        int randomIndex = hiddenIndices.get(rand.nextInt(hiddenIndices.size()));
        char letter = solutionWord.charAt(randomIndex);
        handleGuess(letter);
    }

    private void updateWordDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : displayedWord) {
            sb.append(c).append(' ');
        }
        word.setText(sb.toString().trim());
    }

    private void updateHangmanImage() {
        if (mistakes == 0) {
            hangmanpics.setIcon(null);
        } else if (mistakes > 0 && mistakes <= maxMistakes) {
            // Startindex = 10 - maxMistakes
            int pStart = 10 - maxMistakes;
            int imageIndex = pStart + mistakes - 1;
            if (imageIndex < 0) imageIndex = 0;
            if (imageIndex > 9) imageIndex = 9;
            hangmanpics.setIcon(hangmanImages[imageIndex]);
        }
    }

    // überprüft ob noch Unterstriche vorhanden sind, wenn nicht EQ TRUE (SIEG)
    private boolean isWordGuessed() {
        for (char c : displayedWord) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    // deaktivieren aller Buttons
    private void disableAllLetterButtons() {
        aButton.setEnabled(false);
        bButton.setEnabled(false);
        cButton.setEnabled(false);
        dButton.setEnabled(false);
        eButton.setEnabled(false);
        fButton.setEnabled(false);
        gButton.setEnabled(false);
        hButton.setEnabled(false);
        iButton.setEnabled(false);
        jButton.setEnabled(false);
        kButton.setEnabled(false);
        lButton.setEnabled(false);
        mButton.setEnabled(false);
        nButton.setEnabled(false);
        oButton.setEnabled(false);
        pButton.setEnabled(false);
        qButton.setEnabled(false);
        rButton.setEnabled(false);
        sButton.setEnabled(false);
        tButton.setEnabled(false);
        uButton.setEnabled(false);
        vButton.setEnabled(false);
        wButton.setEnabled(false);
        xButton.setEnabled(false);
        yButton.setEnabled(false);
        zButton.setEnabled(false);
    }

    // aktivieren aller Buttons
    private void enableAllLetterButtons() {
        aButton.setEnabled(true);
        bButton.setEnabled(true);
        cButton.setEnabled(true);
        dButton.setEnabled(true);
        eButton.setEnabled(true);
        fButton.setEnabled(true);
        gButton.setEnabled(true);
        hButton.setEnabled(true);
        iButton.setEnabled(true);
        jButton.setEnabled(true);
        kButton.setEnabled(true);
        lButton.setEnabled(true);
        mButton.setEnabled(true);
        nButton.setEnabled(true);
        oButton.setEnabled(true);
        pButton.setEnabled(true);
        qButton.setEnabled(true);
        rButton.setEnabled(true);
        sButton.setEnabled(true);
        tButton.setEnabled(true);
        uButton.setEnabled(true);
        vButton.setEnabled(true);
        wButton.setEnabled(true);
        xButton.setEnabled(true);
        yButton.setEnabled(true);
        zButton.setEnabled(true);
    }

    // Main-Methode (Start des Programmes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Hangman hangman = new Hangman();
            hangman.setContentPane(hangman.mainPanel);
            hangman.setTitle("Hangman");
            hangman.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            hangman.pack();
            hangman.setLocationRelativeTo(null);
            hangman.setVisible(true);
        });
    }
}