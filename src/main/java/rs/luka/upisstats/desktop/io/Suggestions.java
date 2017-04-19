package rs.luka.upisstats.desktop.io;

import org.intellij.lang.annotations.MagicConstant;
import rs.luka.upisstats.desktop.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 1.2.17..
 */
public class Suggestions {
    public static final int SIFRE_SMEROVA = 0;
    public static final int NAZIVI_OS = 1;
    public static final int NAZIVI_SS = 2;
    public static final int MESTO_OS = 3;
    public static final int MESTO_SS = 4;
    public static final int OKRUZI = 5;
    public static final int PODRUCJA_RADA = 6;
    public static final int NAZIVI_SMEROVA = 7;

    private static List<List<String>> suggestions = new ArrayList<>();
    private static final File DATA = new File(Main.RES_DIR, "suggestions");

    public static void init() throws IOException {
        List<String> data = Files.readAllLines(DATA.toPath());
        List<String> current = new ArrayList<>();
        for(String line : data) {
            if(line.startsWith("--")) {
                current.sort(new LatinicaComparator());
                suggestions.add(current);
                current = new ArrayList<>();
            } else {
                current.add(line);
            }
        }
    }

    public static String[] getSuggestionsFor(@MagicConstant int type) {
        return suggestions.get(type).toArray(new String[0]);
    }
}
