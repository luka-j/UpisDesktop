package rs.luka.upisstats.desktop;

import rs.luka.upisstats.desktop.io.Suggestions;
import rs.luka.upisstats.desktop.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by luka on 24.1.17.
 */
public class Main {
    public static final File RES_DIR;
    static {
        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        RES_DIR = new File(jarPath.substring(0, jarPath.lastIndexOf('/')), "res");
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            JOptionPane.showMessageDialog(null,
                    "Ups, došlo je do neočekivane greške!\n"
                            + e.getClass().getName() + ": " + e.getLocalizedMessage(),
                    "Greška", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        });

        EventQueue.invokeLater(MainFrame::new);

        try {
            Suggestions.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
