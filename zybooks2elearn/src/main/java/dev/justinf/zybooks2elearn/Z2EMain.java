package dev.justinf.zybooks2elearn;

import javax.swing.*;
import java.awt.*;

public class Z2EMain {

    public static void main(String[] args) throws AWTException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException |
                ClassNotFoundException |
                InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        Z2EApp app = new Z2EApp();
        app.start();
    }
}