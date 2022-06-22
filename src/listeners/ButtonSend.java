package listeners;

import models.DataScores;
import models.Model;
import views.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

/**
 * Klass nupu Saada täht jaoks
 */
public class ButtonSend implements ActionListener {
    /**
     * Mudel
     */
    private Model model;
    /**
     * View
     */
    private View view;

    /**
     * Konstuktor
     * @param model Model
     * @param view View
     */
    public ButtonSend(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Kui kliikida nupul Saada täht
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //JOptionPane.showMessageDialog(null, "Kes vajutas nuppu/Enter: " + view.getTxtChar().getText().toUpperCase());

        view.getTxtChar().requestFocus(); // Peale selle nupu klikkimist anna fookus tekstikastile
        String enteredChars = view.getTxtChar().getText().toUpperCase();
        char guessedLetter = enteredChars.charAt(0);
        String guessWord = model.getWordToGuess();
        String[] guessList = guessWord.split("");
        //System.out.println("GuessList to see whats inside: " + guessWord + guessList);
        boolean correct = true;
        for (int i = 0; i < guessList.length; i++){
            if (guessList[i].equals(enteredChars)){
                model.getWordNewOfLife().setCharAt(i, guessedLetter);
                //view.getLblGuessWord().setText(model.getWordNewOfLife().toString());
                view.getLblGuessWord().setText(model.addSpaceBetween(model.getWordNewOfLife().toString()));
                //System.out.println(model.getWordNewOfLife());
                //System.out.println("What index of:  " + i);

                correct = false;
            }
        }

        if (correct){
            model.getMissedLetters().add(enteredChars);
        }


        model.setCountMissedWords(model.getMissedLetters().size());
        view.getLblWrongInfo().setText("Valesti: " + model.getCountMissedWords() + " täht(e) " + model.getMissedLetters());
        view.getTxtChar().setText("");
        if (!model.getWordNewOfLife().toString().contains("_")) {
            model.askPlayerName();
            model.getPlayerName();
            model.insertScoreToTable();
            view.setEndGame();

        }
        if (!(model.getCountMissedWords() < 7)) {
            System.out.println("counter: " + model.getCountMissedWords());
            JOptionPane.showMessageDialog(null, "Game over", "Lost the game", JOptionPane.PLAIN_MESSAGE);
            view.setEndGame();

        }
    }
}
