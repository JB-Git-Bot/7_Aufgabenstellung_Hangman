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

    // Root panel from your .form (make sure it’s bound correctly)
    private JPanel mainPanel;

    // -- Game Logic Fields --
    private final ArrayList<String> wordList = new ArrayList<>(Arrays.asList(
            "JAVA", "COMPUTER", "PROGRAMMING", "HANGMAN", "KEYBOARD", "OBJECT"
    ));

    private String solutionWord;       // The word to guess
    private char[] displayedWord;      // Array for underscores and revealed letters
    private int mistakes;              // Count of wrong guesses so far
    private int maxMistakes = 9;       // Default maximum mistakes allowed
    private final HashSet<Character> guessedLetters = new HashSet<>();

    // Hangman images – load 10 images: hangman1.png to hangman10.png
    private final ImageIcon[] hangmanImages = new ImageIcon[10];

    public Hangman() {
        // 1) Load the hangman images.
        loadImages();

        // 2) Make the word text field read-only.
        word.setEditable(false);

        // 3) Set up the combo box for max attempts.
        setupMaxAttemptsComboBox();

        // 4) Wire up all letter buttons.
        setupLetterButtons();

        // Ensure the checkbox starts unselected and hide the letter history area.
        showAlreadyWrittenLettersCheckBox.setSelected(false);
        AlreadyWrittenLetters.setVisible(false);

        // 5) Listener for the checkbox to show/hide guessed letters.
        showAlreadyWrittenLettersCheckBox.addActionListener(e -> {
            boolean visible = showAlreadyWrittenLettersCheckBox.isSelected();
            AlreadyWrittenLetters.setVisible(visible);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        // 6) Restart button listener.
        restartbutton.addActionListener(e -> startNewGame());

        // 7) Hint button listener (reveals one random missing letter).
        hintButton.addActionListener(e -> giveHint());

        // 8) Start a new game.
        startNewGame();
    }

    /**
     * Loads 10 hangman images.
     * Assumes file names "hangman1.png" ... "hangman10.png".
     * Adjust the file path as necessary.
     */
    private void loadImages() {
        for (int i = 0; i < 10; i++) {
            hangmanImages[i] = new ImageIcon(
                    "C:\\Users\\ENTW1\\Desktop\\Schule\\2.Klasse\\INF\\bilder_ausgabe\\hangman\\hangman" + (i + 1) + ".png"
            );
        }
    }

    /**
     * Initializes the combo box for selecting max mistakes.
     * If no items are in the combo box, adds values "5" through "10".
     */
    private void setupMaxAttemptsComboBox() {
        if (maxNumberofAttempts.getItemCount() == 0) {
            maxNumberofAttempts.addItem("5");
            maxNumberofAttempts.addItem("6");
            maxNumberofAttempts.addItem("7");
            maxNumberofAttempts.addItem("8");
            maxNumberofAttempts.addItem("9");
            maxNumberofAttempts.addItem("10");
            maxNumberofAttempts.setSelectedItem("9");
        }
        // Update maxMistakes when the user selects a new value.
        maxNumberofAttempts.addActionListener(e -> {
            String selected = (String) maxNumberofAttempts.getSelectedItem();
            try {
                maxMistakes = Integer.parseInt(selected);
                commentatorArea.setText("Max attempts set to: " + maxMistakes);
            } catch (NumberFormatException ex) {
                commentatorArea.setText("Invalid number for attempts.");
            }
        });
    }

    /**
     * Wires up each letter button so that clicking it calls handleGuess with that letter.
     */
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

    /**
     * Starts or restarts a new game.
     * It also reads the current max mistakes from the combo box.
     */
    private void startNewGame() {
        // Update maxMistakes from the combo box at the start of a new game.
        String selected = (String) maxNumberofAttempts.getSelectedItem();
        try {
            maxMistakes = Integer.parseInt(selected);
        } catch (NumberFormatException ex) {
            maxMistakes = 9;
        }

        // Choose a random word.
        Random rand = new Random();
        solutionWord = wordList.get(rand.nextInt(wordList.size())).toUpperCase();

        // Initialize displayedWord with underscores.
        displayedWord = new char[solutionWord.length()];
        for (int i = 0; i < solutionWord.length(); i++) {
            displayedWord[i] = '_';
        }

        // Reset mistakes and guessed letters.
        mistakes = 0;
        guessedLetters.clear();
        AlreadyWrittenLetters.setText("");
        commentatorArea.setText("New game started. Good luck!");
        hangmanpics.setIcon(null);

        // Re-enable letter buttons.
        enableAllLetterButtons();

        // Update UI.
        updateWordDisplay();
        updateHangmanImage();
    }

    /**
     * Called when a letter button is clicked.
     * If a letter is clicked again, it counts as an extra mistake.
     */
    private void handleGuess(char letter) {
        letter = Character.toUpperCase(letter);

        // If letter was already guessed, count an extra mistake.
        if (guessedLetters.contains(letter)) {
            mistakes++;
            commentatorArea.setText("You already guessed: " + letter + ". Mistake counted!");
            updateHangmanImage();
            if (mistakes >= maxMistakes) {
                commentatorArea.setText("Game Over! The word was: " + solutionWord);
                disableAllLetterButtons();
            }
            return;
        }

        // Otherwise, record the guess.
        guessedLetters.add(letter);
        AlreadyWrittenLetters.append(letter + " ");

        // Check if the letter is in the solution.
        boolean found = false;
        for (int i = 0; i < solutionWord.length(); i++) {
            if (solutionWord.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
        }

        if (!found) {
            mistakes++;
            commentatorArea.setText("Wrong guess: " + letter);
        } else {
            commentatorArea.setText("Good guess: " + letter);
        }

        updateWordDisplay();
        updateHangmanImage();

        // Check for game over or win.
        if (mistakes >= maxMistakes) {
            commentatorArea.setText("Game Over! The word was: " + solutionWord);
            disableAllLetterButtons();
        } else if (isWordGuessed()) {
            commentatorArea.setText("You guessed the word: " + solutionWord + "!");
            disableAllLetterButtons();
        }
    }

    /**
     * Optional hint: reveal one random missing letter.
     */
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

    /**
     * Updates the word text field to display the current state (underscores and revealed letters).
     */
    private void updateWordDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : displayedWord) {
            sb.append(c).append(' ');
        }
        word.setText(sb.toString().trim());
    }

    /**
     * Updates the hangman image based on the number of mistakes.
     * Mapping: with 10 images available, if maxMistakes = M,
     * we want the first mistake to show image at index = (10 - M) and the Mth mistake to show hangman10.png (index 9).
     *
     * Calculation:
     *   Let pStart = 10 - M.
     *   Then for mistake m (1 <= m <= M), use image index = pStart + m - 1.
     */
    private void updateHangmanImage() {
        if (mistakes == 0) {
            hangmanpics.setIcon(null);
        } else if (mistakes > 0 && mistakes <= maxMistakes) {
            int pStart = 10 - maxMistakes; // For M=5, pStart = 5.
            int imageIndex = pStart + mistakes - 1;
            // Clamp imageIndex to 0...9.
            if (imageIndex < 0) imageIndex = 0;
            if (imageIndex > 9) imageIndex = 9;
            hangmanpics.setIcon(hangmanImages[imageIndex]);
        }
    }

    /**
     * Checks if the entire word has been guessed.
     */
    private boolean isWordGuessed() {
        for (char c : displayedWord) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Disables all letter buttons.
     */
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

    /**
     * Re-enables all letter buttons.
     */
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

    // -----------------------------------------------------------------------
    // Teil 2: Loading words from a file (optional)
    // -----------------------------------------------------------------------
    /**
     * Loads words from a text file (one word per line).
     */
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

    // -----------------------------------------------------------------------
    // Main method to run the application.
    // -----------------------------------------------------------------------
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
