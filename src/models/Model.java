package models;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Klass mudel (Üldine)
 */
public class Model {
    /**
     * Siin on kõik unikaalsed kategooriad mis andmebaasi failist leiti
     */
    private String[] categories;
    /**
     * Andmeaasi nimi kettal
     */
    private String dbName = "words.db";
    /**
     *  Andmebaasi ühenduse jaoks
     */
    private String dbUrl = "jdbc:sqlite:" + dbName;
    /**
     * Andmebaasi tabeli scores sisu (Edetabel)
     */
    private List<DataScores> dataScores;
    /**
     * Andmebaasi tabeli words sisu Sõnad
     */
    private List<DataWords> dataWords;
    /**
     * Andmebaasi ühendust algselt pole
     */
    /**
     * Missed words variable comes here <----
     */
    private List<String> missedLetters = new ArrayList<>();
    /**
     * List to random words
     */
    private List<String> wordsFromDatabase = new ArrayList<>();
    /**
     * String variable to guess the word
     */
    private String wordToGuess;
    private StringBuilder wordNewOfLife;
    private String wordGuessed;
    private int countMissedWords;

    Connection connection = null;
    private char[] correct;
    private String playerName;



    /**
     * Konstruktor
     */
    public Model() {
        dataScores = new ArrayList<>(); // Teeme tühja edetabeli listi
        dataWords = new ArrayList<>(); // Teeme tühja sõnade listi
        //categories = new String[]{"Kõik kategooriad", "Kategooria 1", "Kategooria 2"}; // TESTIKS!
        scoreSelect(); // Loeme edetabeli dataScores listi, kui on!
        wordsSelect(); // Loeme sõnade tabeli dataWords listi.
    }
    // ANDMEBAASI ASJAD
    /**
     * Andmebaaasi ühenduseks
     * @return tagastab ühenduse või rakendus lõpetab töö
     */
    private Connection dbConnection() throws SQLException {
        if (connection != null) { // Kui ühendus on püsti
            connection.close(); // Sulge ühendus
        }
        connection = DriverManager.getConnection(dbUrl); // Tee ühendus
        return connection; // Tagasta ühendus
    }
    /**
     * SELECT lause edetabeli sisu lugemiseks ja info dataScores listi lisamiseks
     */
    public void scoreSelect() {
        String sql = "SELECT * FROM scores ORDER BY playertime DESC";
        try {
            Connection conn = this.dbConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            dataScores.clear(); // Tühjenda dataScores list vanadest andmetest
            while (rs.next()) {
                //int id = rs.getInt("id");
                String datetime = rs.getString("playertime");
                LocalDateTime playerTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String playerName = rs.getString("playername");
                String guessWord = rs.getString("guessword");
                String wrongCharacters = rs.getString("wrongcharacters");
                // Lisame tabeli kirje dataScores listi
                dataScores.add(new DataScores(playerTime, playerName, guessWord, wrongCharacters));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * SELECT lause tabeli words sisu lugemiseks ja info dataWords listi lisamiseks
     */
    public void wordsSelect() {
        String sql = "SELECT * FROM words ORDER BY category, word";
        List<String> categories = new ArrayList<>(); // NB! See on meetodi sisene muutuja categories!
        try {
            Connection conn = this.dbConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            dataWords.clear(); // Tühjenda dataScores list vanadest andmetest
            while (rs.next()) {
                //int id = rs.getInt("id");
                int id = rs.getInt("id");
                String word = rs.getString("word");
                String category = rs.getString("category");
                dataWords.add(new DataWords(id, word, category)); // Lisame tabeli kirje dataWords listi
                categories.add(category);
            }
            // https://howtodoinjava.com/java8/stream-find-remove-duplicates/
            List<String> unique = categories.stream().distinct().collect(Collectors.toList());
            setCorrectCategoryNames(unique); // Unikaalsed nimed Listist String[] listi categories

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SETTERS
    /**
     * Paneb unikaalsed kategooriad ComboBox-i jaoks muutujasse
     * @param unique unikaalsed kategooriad
     */
    private void setCorrectCategoryNames(List<String> unique) {
        categories = new String[unique.size()+1]; // Vali kategooria. See on klassi sisene muutuja!
        categories[0] = "Kõik kategooriad";
        for(int x = 0; x < unique.size(); x++) {
            categories[x+1] = unique.get(x);
            //System.out.println("Another test to find category names on list: " + unique);
        }
    }

    // GETTERS
    /**
     * Tagasta kategooriad
     * @return tagastab String[] listi kategooria nimedega
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * Tagastab edetabeli listi
     * @return tagastab List&lt;DataScores&gt; listi edetabeli tabelis sisuga
     */
    public List<DataScores> getDataScores() {
        return dataScores;
    }
    /**
     * Tagastab sõnade listi
     * @return List
     */
    public List<DataWords> getDataWords() {
        return dataWords;
    }

    public StringBuilder getWordNewOfLife() {
        return wordNewOfLife;
    }



    public int getCountMissedWords() {
        return countMissedWords;
    }
    public void setCountMissedWords(int countMissedWords) {
        this.countMissedWords = countMissedWords;
    }

    /**
     * Returns missed words
     * @return list
     */

    public List<String> getMissedLetters(){
        return missedLetters;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public void randomWordsFromCategoriesList (String selectedCategory){
        Random random = new Random();
        System.out.println("For a test to see current category: " + selectedCategory);
        List<String> guessWordsToList = new ArrayList<>();
        if (selectedCategory.equals("Kõik kategooriad")){
            wordToGuess = dataWords.get(random.nextInt(dataWords.size())).getWord();
            System.out.println("Test for random word: " + wordToGuess.toUpperCase());
        } else {
            for (DataWords word : dataWords){
                if (selectedCategory.equals(word.getCategory())) {
                    guessWordsToList.add(word.getWord());

                }
            }
            wordToGuess = guessWordsToList.get(random.nextInt(guessWordsToList.size()));
            System.out.println("Test for random word from current category: " + wordToGuess.toUpperCase());
        }
        this.wordToGuess = wordToGuess.toUpperCase();
        hideLetters();
    }


    private void hideLetters() {
        StringBuilder correct = new StringBuilder(wordToGuess);
        for (int i = 0; i < wordToGuess.length(); i++) {
            correct.setCharAt(i, '_');
        }

        this.wordNewOfLife = correct;
        addSpaceBetween();
        System.out.println("Test to see is word hidden: " + wordNewOfLife);

    }


    private void addSpaceBetween() {
        String s = String.valueOf(wordNewOfLife);
        s = String.join(" ", s.split(""));
        System.out.println(s);
        this.wordNewOfLife = new StringBuilder(s);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void askPlayerName() {
        playerName = JOptionPane.showInputDialog("Enter your name");
        if (playerName.length() < 2) {
            askPlayerName();
        }
    }





}
