package rs.luka.upisstats.desktop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by luka on 25.1.17..
 */
public final class Utils {
    private Utils() {throw new IllegalAccessError();}

    private static final Pattern DOUBLE_REGEX = Pattern.compile("[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    private static Dimension screen;
    static {
        screen = Toolkit.getDefaultToolkit().getScreenSize();
    }
    public static int getScreenHeight() {
        return screen.height;
    }
    public static int getScreenWidth() {
        return screen.width;
    }
    public static boolean equal(double a, double b) {
        return Math.abs(a-b)<0.001;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     *
     * @return 0 - jednina, 1 - množina, između 1 i 4 (za glagole), 2 - množina, 0 ili veće od 4
     */
    public static int pickPlurality(int number) {
        int ld = number%10;
        if(ld == 1) return 0;
        if(ld > 1 && ld < 5) return 1;
        return 2;
    }

    public static String toString(Color color) {
        if(color == null) return "null";
        /*String s;
        return "#" + (((s=Integer.toString(color.getRed(), 16)).length()<2)? "0" + s : s)
                + (((s=Integer.toString(color.getGreen(), 16)).length()<2)? "0" + s : s)
                + (((s=Integer.toString(color.getBlue(), 16)).length()<2)? "0" + s : s);*/ //ok this is a mess
        return "#" + to2HexString(color.getRed()) + to2HexString(color.getGreen()) + to2HexString(color.getBlue());
    }
    private static String to2HexString(int number) {
        if(number < 16) return "0"+Integer.toString(number, 16);
        else            return Integer.toString(number, 16);
    }

    public static JButton generateIconButton(String iconName, int size, String alternativeText) {
        try {
            Image image = ImageIO.read(new File(Main.RES_DIR, iconName));
            image = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new JButton(new ImageIcon(image));
        } catch (IOException e) {
            System.err.println("error while loading iconimage");
            e.printStackTrace();
            return new JButton(alternativeText);
        }
    }

    public static boolean isDouble(String val) {
        return DOUBLE_REGEX.matcher(val).matches();
    }

    //optimized for space and speed
    public static String toGenericLatin(String string) {
        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars) {
            if (c == '-' || c == '"') continue;
                //note to self: preskakanje razmaka dovodi do nekih majndfak rezultata
            if (c == 'Š' || c == 'Đ' || c == 'Ž' || c == 'Č' || c == 'Ć' ||
                c == 'š' || c == 'đ' || c == 'ž' || c == 'č' || c == 'ć') {
                switch (c) {
                    case 'Š':case 'š': sb.append('s');break;
                    case 'Đ':case 'đ': sb.append("dj");break; //ok ovo zapravo radi
                    case 'Ž':case 'ž': sb.append('z');break;
                    case 'Č':case 'č': sb.append('c');break;
                    case 'Ć':case 'ć': sb.append('c');break;
                }
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public static InputStream wrapStream(String contentEncoding, InputStream inputStream)
            throws IOException {
        if (contentEncoding == null || "identity".equalsIgnoreCase(contentEncoding)) {
            return inputStream;
        }
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(inputStream);
        }
        if ("deflate".equalsIgnoreCase(contentEncoding)) {
            return new InflaterInputStream(inputStream, new Inflater(false), 512);
        }
        System.err.println("Unknown contentEncoding " + contentEncoding);
        return inputStream;
    }

    public static Point[] generateRandomPoints(int count, int width, int height) {
        Point[] points = new Point[count];
        Random rnd = new Random();
        for(int i=0; i<count; i++) {
            points[i] = new Point(rnd.nextInt(width), rnd.nextInt(height));
        }
        return points;
    }

    public static<T, E> void mapToArray(Map<T, E> map, T[] keys, E[] values) {
        int i=0;
        for(Map.Entry<T, E> e : map.entrySet()) {
            keys[i] = e.getKey();
            values[i] = e.getValue();
            i++;
        }
    }
}
