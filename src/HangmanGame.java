import controllers.Controller;
import models.Model;
import views.View;

import javax.swing.*;

/**
 * Mäng Hangman
 */
public class HangmanGame {
    /**
     * Konstruktor. Mängu Hangman loomine
     */
    public HangmanGame() {
        View view = null;
        Model model = new Model(); // Mudeli tegemine
        view = new View(model);
        new Controller(model, view); // Kontrolleri tegemine

        view.pack(); // Vaate kohandamine vastavalt valikutele
        view.setLocationRelativeTo(null); // JFrame paigutamine ekraani keskele
        view.setVisible(true); // Tee JFrame nähtavaks
    }

    /**
     * Mängu HangmanGame käivitamine
     * @param args käsurealt loetavad argumendid
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HangmanGame();
            }
        });
    }
}
