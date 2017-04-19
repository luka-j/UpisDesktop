package rs.luka.upisstats.desktop.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import rs.luka.upisstats.desktop.Utils;
import rs.luka.upisstats.desktop.io.Network;
import rs.luka.upisstats.desktop.model.Query;
import rs.luka.upisstats.desktop.model.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luka on 24.1.17..
 */
public class MainFrame implements QueryBuilder.Callbacks, Network.Callbacks, RawQueryEditor.Callbacks {
    static final Color DEFAULT_ORANGE = new Color(0xDE, 0x66, 0x00);
    static final Color BACKGROUND = new Color(225, 225, 230);

    private static final boolean QUERY_HIDE_ON_REMOVE = false;

    private static final double WIDTH = 0.9;
    private static final double HEIGHT_RATIO = 0.6;

    private static final double HGAP = 0.01;
    private static final double VGAP = 0.02;
    private static final double QUERY_WIDTH = 0.58;
    private static final double QUERY_HEIGHT = 1 - 2.5*VGAP;
    private static final double PLOT_WIDTH = 1-QUERY_WIDTH-3*HGAP;

    private JFrame frame;
    private JPanel contentPane;
    private QueryBuilder buildQueryPane;
    private DetailsPanel detailsPane;
    private Plot plotPane;
    private RawQueryEditor rawQueryPane;
    private JCheckBox scalingModeBox;
    private JLabel rawQueryLabel;

    public MainFrame() {
        frame = new JFrame();
        int w = (int) (Utils.getScreenWidth() * WIDTH);
        int h = (int) (w*HEIGHT_RATIO);
        frame.setSize(w, h);
        frame.setTitle("UpisStats");

        initComponents(w, h);

        frame.setContentPane(contentPane);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        plotPane.notifyOnScreen();

        //Result res1 = new Result("{\"type\":0,\"color\":\"ffff0000\",\"factor\":0.6024803877700613,\"xname\":\"zaokruzi#1.prosek.srpski\",\"yname\":\"zaokruzi#1.prosek.matematika\",\"data\":[[66,9,2,40,74,32,6,2,1,30,7,45,1,47,3,2,20,2,3,1,1,1,8,26,4,4,24,6,6,1,7,22,22,1,3,14,1,1,4,4,1,2,6,2,1,8,12,1,11,1,10,1,8,12,13,24,4,3,17,6,5,6,2,3,4,3,3,4,4,2,2,2,1,4,4,4,1,4,12,1,1,7,12,9,7,1,8,6,1,11,3,3,11,2,7,6,20,32,3,3,9,11,4,20,3,3,2,1,1,10,2,2,3,2,2,4,3,2,4,5,9,4,13,2,1,13,8,5,6,2,9,2,1,1,8,7,7,9,20,7,13,4,5,21,12,8,1,7,13,4,1,1,4,7,3,1,7,9,2,1,1,6,5,3,7,3,1,8,8,2,10,5,7,1,4,1,1,8,7,1,2,13,7,18,5,2,18,16,3,15,8,2,3,5,1,3,5,2,1,1,1,2,3,1,1,7,9,1,1,9,1,1,1,6,11,8,5,2,10,1,3,9,8,2,15,1,1,14,5,8,13,9,4,16,11,5,12,5,1,1,1,8,3,2,5,1,6,2,3,2,5,1,6,2,10,2,13,4,4,2,4,4,1,5,2,3,2,6,2,3,8,7,7,3,6,4,2,1,3,9,3,1,2,1,14,4,1,1,2,1,1,6,5,2,6,2,6,5,6,4,2,7,1,16,3,21,3,1,3],[2.0,2.0,2.0,2.2,2.1,2.1,2.1,2.1,2.1,2.3,3.2,2.3,2.0,2.2,4.0,3.1,2.2,3.1,2.2,3.1,2.2,3.1,2.4,2.4,4.2,3.3,2.4,3.3,3.0,3.0,3.2,5.0,2.3,4.1,4.1,2.3,3.2,3.0,2.3,3.0,4.1,3.2,3.0,3.2,2.3,3.0,3.0,4.1,3.0,2.3,3.0,2.7,2.5,3.4,2.5,2.5,3.4,4.3,2.5,4.3,3.4,3.1,4.0,4.0,4.2,4.0,3.3,2.4,2.4,3.3,4.2,4.0,3.3,2.4,4.0,3.1,4.2,4.0,3.1,4.2,2.4,4.0,3.1,4.0,3.1,4.2,3.1,4.0,2.4,3.1,4.0,2.6,2.6,4.4,3.5,3.5,2.6,2.6,3.5,4.4,3.5,2.6,4.4,5.0,5.0,4.1,4.3,4.1,3.4,2.5,3.2,3.4,4.3,2.5,2.5,3.2,4.1,3.4,4.3,4.1,3.2,2.5,3.2,4.1,5.0,3.2,4.1,4.1,3.2,5.0,4.1,3.2,5.0,3.8,2.7,3.6,2.7,3.6,2.7,4.5,2.7,4.5,3.6,2.7,4.5,3.6,4.3,4.5,2.7,3.6,4.3,4.0,3.3,3.5,4.4,4.2,2.6,4.4,2.6,4.2,3.3,4.2,3.3,4.4,3.3,4.2,3.7,4.2,3.3,4.2,3.3,3.3,4.2,4.0,4.2,4.0,2.8,3.7,4.6,3.9,2.8,3.7,2.8,2.8,4.6,3.7,2.8,3.7,4.6,2.8,4.6,3.7,3.7,2.8,3.5,4.6,2.7,4.5,3.6,3.4,4.3,2.7,3.4,3.6,4.5,4.3,3.4,3.6,2.7,3.4,4.5,3.6,4.3,4.3,3.4,3.4,4.3,4.1,4.3,4.9,3.8,3.8,2.9,4.7,2.9,4.7,3.8,2.9,3.8,4.7,2.9,3.8,4.7,2.9,3.8,4.7,2.9,4.7,4.5,3.8,1.9,4.6,3.7,4.4,2.8,3.5,4.6,3.7,3.5,2.8,4.4,3.7,3.5,4.6,3.5,4.4,3.5,4.4,4.4,3.9,3.9,3.9,4.8,3.9,4.8,4.8,3.9,3.9,4.8,4.8,3.9,4.8,3.9,3.6,4.7,2.9,4.5,2.9,3.6,4.7,3.8,4.7,3.6,4.5,3.6,4.7,4.5,4.9,4.9,4.9,3.8,4.9,4.9,3.9,4.8,3.7,3.7,4.8,3.7,4.6,4.6,4.8,4.8,4.9,3.8,4.9,3.8,4.7,3.9],[2.0,2.1,2.2,2.0,2.0,2.1,2.2,2.3,2.6,2.0,3.0,2.1,1.9,2.1,4.0,3.0,2.2,3.1,2.3,3.3,2.4,3.6,2.0,2.1,4.0,3.0,2.2,3.1,2.8,2.9,3.1,5.0,2.2,4.1,4.0,2.3,3.2,2.1,2.4,2.2,4.3,3.3,2.3,3.4,2.5,2.4,2.5,4.4,2.6,2.8,2.7,3.0,2.0,3.0,2.1,2.2,3.1,4.1,2.3,4.0,3.2,2.9,3.9,3.8,4.2,3.1,3.2,2.3,2.4,3.3,4.1,3.0,3.4,2.5,3.3,2.3,4.4,3.2,2.4,4.3,2.6,3.5,2.5,3.4,2.6,4.5,2.7,3.7,2.9,2.8,3.6,2.0,2.1,4.0,3.0,3.1,2.2,2.3,3.2,4.2,3.3,2.4,4.1,4.9,4.8,3.9,4.3,3.2,3.3,2.4,2.2,3.4,4.2,2.5,2.6,2.4,3.4,3.5,4.4,3.3,2.5,2.7,2.6,3.6,4.5,2.7,3.5,3.8,2.8,4.7,3.7,2.9,4.6,4.0,2.0,3.0,2.1,3.1,2.2,4.1,2.3,4.0,3.2,2.4,4.3,3.3,3.2,4.2,2.5,3.4,3.1,2.9,2.3,3.4,4.4,3.3,2.5,4.3,2.6,3.2,2.4,3.5,2.5,4.5,2.6,3.4,4.8,3.7,2.7,3.6,2.8,2.9,3.9,2.8,3.8,2.7,2.0,3.0,4.0,4.1,2.1,3.1,2.2,2.3,4.2,3.2,2.4,3.3,4.1,2.5,4.4,3.4,3.5,2.6,2.4,4.3,2.6,4.5,3.5,2.4,3.4,2.7,2.5,3.6,4.4,3.6,2.6,3.7,2.8,2.7,4.6,3.8,3.5,3.8,2.8,2.9,3.7,2.9,3.9,5.0,3.0,3.1,2.2,4.1,2.3,4.0,3.2,2.4,3.3,4.3,2.5,3.4,4.2,2.6,3.5,4.5,2.7,4.4,3.3,3.6,1.9,4.6,3.6,3.5,2.7,2.5,4.5,3.7,2.6,2.8,3.7,3.8,2.7,4.8,2.8,3.6,2.9,3.9,3.8,3.0,3.1,3.2,4.2,3.3,4.1,4.4,3.4,3.5,4.3,4.6,3.6,4.5,3.7,2.6,4.7,2.8,3.6,2.9,2.7,4.6,3.8,4.9,2.8,3.8,2.9,4.8,3.9,4.1,4.5,4.4,2.5,4.7,4.6,3.8,4.8,2.7,2.8,4.7,2.9,3.9,3.8,4.9,3.7,4.9,2.8,4.8,2.9,3.9,2.9]]}");
        //Result res2 = new Result("{\"type\":0,\"color\":\"ff00ff00\",\"factor\":3.3342643285695392,\"xname\":\"zaokruzi#1.bodovi.srpski\",\"yname\":\"zaokruzi#1.bodovi.matematika\",\"data\":[[1,1,2,1,2,1,2,2,1,2,1,2,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1,1,1,2,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,2,1,1,4,1,3,1,1,1,2,2,1,1,3,1,1,1,1,1,2,1,2,1,1,2,1,1,1,1,2,1,2,1,3,1,1,1,1,1,1,2,1,1,1,1,1,1,1,3,1,1,1,1,3,1,1,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,1,2,1,1,2,2,1,1,1,2,1,1,1,1,2,1,1,1,1,3,1,1,1,1,3,1,1,1,1,1,1,1,1,2,1,2,1,1,1,1,2,1,1,1,1,3,1,2,1,2,2,1,1,3,3,1,1,2,1,4,1,1,1,2,1,1,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,5,1,1,1,3,1,2,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1,1,1,2,1,2,1,1,2,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1],[6.9,8.5,7.6,7.6,6.9,8.9,8.5,8.7,6.9,7.8,7.8,7.6,9.4,8.9,8.3,9.2,8.9,7.6,7.8,8.7,7.6,9.6,8.5,8.9,7.8,9.4,8.5,7.8,8.7,8.3,7.6,7.7,8.4,7.5,8.8,8.4,7.5,9.1,8.6,7.5,9.1,8.2,8.4,8.2,7.5,8.6,9.3,8.2,8.6,8.8,9.7,7.7,7.9,7.9,7.7,7.9,7.9,8.8,7.7,7.7,9.7,8.8,7.7,7.9,8.4,7.9,9.0,7.6,8.5,8.3,7.6,8.3,8.3,8.7,8.7,8.5,7.8,7.8,7.8,8.7,7.8,7.8,8.7,8.9,7.8,8.9,8.7,8.6,8.4,8.4,9.7,8.6,9.7,8.6,8.8,7.9,7.9,8.8,8.6,8.6,7.9,8.8,7.9,8.8,7.8,8.7,8.9,8.7,8.9,8.7,8.9,8.9,8.1,7.0,8.3,8.1,8.1,9.0,6.3,8.3,8.5,7.0,8.5,8.0,8.0,8.2,8.2,7.1,8.2,8.0,9.0,7.0,9.0,8.1,8.5,8.1,7.0,9.0,7.2,9.0,9.2,8.1,7.4,6.5,8.5,7.2,8.3,8.7,7.1,9.3,8.0,8.0,8.0,7.1,8.0,7.1,7.1,8.0,8.2,8.0,7.1,8.8,6.6,9.5,8.0,7.3,8.4,8.6,6.6,7.3,9.3,9.5,8.2,7.3,9.5,7.5,8.1,8.3,9.2,8.1,9.0,6.5,8.1,9.2,8.1,9.0,8.3,7.4,9.2,7.2,9.2,9.0,8.3,6.7,7.4,8.5,6.7,8.1,6.7,6.9,7.4,9.4,7.4,9.2,8.3,8.4,7.3,9.3,8.0,7.5,8.0,7.3,8.2,9.1,8.0,7.3,8.2,8.4,8.2,8.0,9.1,8.0,8.0,8.2,8.8,6.8,8.6,7.5,7.7,8.4,8.8,6.8,9.3,8.6,9.1,7.5,7.7,8.4,8.4,7.5,8.2,8.4,8.2,8.8,8.6,6.8,8.2,7.4,8.1,8.5,9.2,7.4,7.6,9.2,8.3,9.4,8.1,7.6,9.0,9.0,8.1,7.4,9.4,8.5,9.2,8.1],[5.3,6.0,5.1,5.2,5.4,8.2,6.1,7.0,5.1,6.1,6.6,5.5,7.4,8.7,5.3,6.3,8.6,5.6,6.7,7.4,5.3,8.3,6.2,8.5,6.4,7.2,6.3,6.5,7.3,5.2,5.4,6.9,6.7,5.8,8.9,6.8,5.9,6.5,7.8,5.6,6.4,5.4,6.6,5.5,5.7,7.6,7.9,5.9,6.1,7.3,8.2,5.2,6.3,6.4,5.3,6.1,6.2,7.7,5.6,5.7,8.5,7.5,5.4,6.6,5.3,6.0,5.6,5.9,6.6,5.5,5.8,5.6,5.9,6.2,6.3,5.2,5.4,5.1,5.2,6.1,5.7,5.8,6.7,7.6,5.5,7.5,6.5,6.9,5.9,5.6,8.7,6.8,7.3,5.3,6.1,5.2,5.3,6.2,5.6,5.5,5.7,6.6,5.0,6.0,5.9,6.9,6.5,5.7,6.8,5.8,6.1,5.7,8.2,6.1,9.1,8.7,8.4,9.3,5.1,8.1,9.1,5.0,9.0,7.2,7.1,8.5,7.0,5.0,7.1,6.0,8.9,5.3,8.2,7.1,9.4,7.2,5.7,8.6,5.1,7.0,8.1,6.0,6.3,5.1,8.1,5.0,7.0,9.1,5.4,9.5,6.3,6.4,6.1,5.2,6.7,5.9,5.6,6.5,7.6,6.6,5.7,9.1,5.0,9.3,5.1,5.3,7.0,8.2,5.2,5.0,8.0,9.1,6.0,5.1,9.0,6.2,6.4,7.5,8.4,6.5,7.3,5.8,6.2,8.3,6.3,7.1,7.4,6.5,8.2,5.9,8.9,7.7,7.7,5.1,5.0,7.0,5.0,5.1,5.5,6.6,5.4,8.2,5.1,7.0,6.1,7.6,5.6,8.6,5.4,6.8,5.5,5.7,6.3,7.3,5.3,5.5,6.4,7.5,6.9,5.9,7.8,5.6,5.7,6.8,8.2,5.2,7.1,5.1,6.2,6.0,8.0,5.1,7.3,7.4,6.2,5.4,6.5,6.3,6.4,5.5,5.3,6.1,5.0,8.3,7.3,5.5,5.1,5.7,5.5,7.8,7.5,5.8,6.6,7.3,6.5,8.4,5.4,6.7,6.9,6.8,5.7,5.9,8.9,7.9,7.8,5.8]]}");
        //plotPane.setResult(res1);
        //plotPane.setResult(res2);
        //detailsPane.addResult(res1);
        //detailsPane.addResult(res2);
        //plotPane.setResult(new Result("{\"type\":0,\"color\":\"ff0000ff\",\"factor\":1.8700846044349204,\"xname\":\"zaokruzi#1.prosek.srpski\",\"yname\":\"zaokruzi#1.prosek.matematika\",\"data\":[[5,2,3,3,2,2,2,5,1,1,1,1,1,1,1,1,1,2,2,2,1,6,1,1,2,2,2,4,1,3,1,4,1,1,1,1,2,2,2,4,1,1,4,3,1,2,1,5,1,3,1,1,3,1,1,1,1,2,1,2,1,1,2,6,1,3,1,1,1,5,2,2,3,3,1,7,1,1,1,2,1,1,3,15,2,8,8,1,1,3,1,1,1,1,3,1,1,1,1,2,3,1,3,2,2,4,1,2,8,1,2,3,6,1,1,1,1,5,1,1,1],[4.5,3.6,2.7,4.5,3.6,4.5,2.7,4.5,4.4,3.5,4.4,3.5,4.4,3.3,4.2,3.3,4.2,4.2,4.2,3.3,4.2,4.6,2.8,3.7,4.6,2.8,3.7,4.6,2.8,4.6,2.8,4.5,4.5,4.3,4.5,3.4,3.4,4.3,4.3,4.3,4.9,2.9,4.7,4.7,2.9,4.7,2.9,4.7,4.6,4.6,2.8,3.7,4.4,4.6,3.5,4.4,4.4,3.9,3.9,4.8,3.9,4.8,4.8,4.8,3.9,4.7,4.5,2.1,3.8,4.7,4.5,4.7,4.5,4.9,2.3,4.8,2.2,2.2,4.8,4.6,4.8,2.4,2.4,5.0,4.1,4.9,4.9,2.3,3.0,3.0,3.4,2.5,2.5,3.4,4.3,3.4,4.0,4.0,4.2,4.0,4.0,4.0,4.0,3.1,4.0,4.4,3.5,2.6,4.4,3.5,2.6,4.4,5.0,5.0,4.3,3.2,4.1,4.1,3.2,4.1,3.2],[4.1,3.1,2.3,4.0,3.2,4.3,2.4,4.2,4.4,3.4,4.3,3.5,4.6,2.5,3.5,2.6,3.4,3.7,3.6,2.8,3.8,4.2,2.3,3.2,4.1,2.4,3.4,4.4,2.5,4.3,2.6,4.5,4.4,3.5,4.6,2.7,2.8,3.8,3.7,3.9,5.0,2.3,4.3,4.2,2.5,4.5,2.6,4.4,4.6,4.5,2.8,3.7,3.7,4.7,2.9,3.9,3.8,3.3,3.4,4.4,3.5,4.3,4.6,4.5,3.7,4.7,3.6,2.0,3.8,4.6,3.7,4.8,3.9,4.7,2.1,4.8,2.1,2.2,4.7,3.9,4.9,2.1,2.2,5.0,4.1,4.9,4.8,2.3,2.5,2.7,3.0,2.1,2.2,3.1,4.0,3.2,3.9,3.1,4.1,3.3,3.5,3.4,3.7,2.7,3.6,4.0,3.0,2.2,4.2,3.2,2.4,4.1,4.9,4.8,4.5,2.6,3.6,3.8,2.8,3.7,2.9]]}"));
        //plotPane.setResult(new Result("{\"type\":0,\"color\":\"ff00ff00\",\"factor\":3.5899737118380552,\"xname\":\"zaokruzi#1.bodovi.srpski\",\"yname\":\"zaokruzi#1.bodovi.matematika\",\"data\":[[2,1,1,1,1,1,2,1,2,1,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,2,2,1,4,2,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,2,1,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],[8.9,6.7,7.6,8.9,8.7,7.6,8.5,8.9,8.7,6.7,5.6,8.9,8.7,7.8,7.4,7.6,4.9,6.5,7.8,8.2,9.3,7.3,7.1,7.3,8.4,8.2,6.6,7.7,6.8,7.9,7.5,8.8,7.7,7.7,5.9,8.6,5.7,6.6,7.7,5.9,6.6,8.8,8.6,5.7,5.9,7.5,5.6,7.4,7.4,8.5,9.4,5.6,8.5,7.4,7.8,7.8,7.8,8.5,6.9,6.9,7.8,7.8,7.6,6.7,8.9,6.9,4.9,6.7,8.5,7.8,7.8,6.7,7.7,8.8,7.9,6.6,8.4,7.5,7.9,7.9,5.9,7.7,8.6,7.7,7.9,7.8,8.7,7.8,6.9,7.8,7.8,7.9,8.9,4.0,4.2,7.1,4.8,8.2,8.0,9.3,5.3,4.1,7.0,5.0,7.0,5.0,4.1,5.0,9.0,7.4,7.0,9.2,8.1,8.3,9.4,5.2,8.1,6.3,8.3,6.5,9.1,6.2,8.0,5.1,8.0,9.1,6.0,6.4,7.1,8.2,6.0,8.0,7.1,9.1,8.0,6.0,9.3,7.5,3.7,8.4,9.5,5.3,9.5,8.0,7.3,8.2,8.4,7.3,5.3,5.5,8.2,8.3,6.1,7.2,8.3,7.2,9.0,9.2,8.1,9.2,9.0,8.3,7.2,4.3,7.0,8.1,6.3,9.0,8.1,7.6,4.7,7.4,8.7,4.7,4.7,8.3,3.8,7.4,8.3,9.6,9.4,6.5,7.1,8.0,7.5,8.0,8.2,6.4,8.2,8.4,7.3,7.1,5.3,7.1,8.0,9.1,8.0,7.1,6.4,8.2,7.7,8.6,6.6,7.9,5.7,9.3,7.7,8.2,6.8,7.3,8.4,8.6,6.6,8.4,6.4,8.4,7.7,9.2,8.3,8.1,7.4,8.5,9.4,8.1,6.5,5.4,8.1,8.3,5.2,9.4,8.1,7.0,7.2],[8.3,4.2,5.1,8.2,7.1,5.2,6.1,8.1,7.0,4.0,2.2,8.0,7.6,6.6,4.4,5.6,2.6,3.3,6.5,5.6,7.5,4.5,3.4,4.9,6.9,5.9,3.2,5.2,4.4,6.4,4.2,7.1,5.0,5.1,3.4,6.5,2.6,3.6,5.7,3.6,3.7,7.5,6.3,2.8,3.9,4.3,2.9,4.8,4.9,6.9,7.7,2.8,6.6,4.7,6.9,5.3,5.4,5.2,4.2,4.3,5.2,5.7,4.6,3.7,7.8,4.8,1.8,3.5,5.3,5.5,5.0,3.0,5.8,7.9,6.9,3.8,5.7,4.8,5.5,5.2,2.6,4.7,5.6,4.5,5.6,5.9,6.9,4.8,3.7,4.7,4.1,4.9,5.7,2.1,2.0,6.0,4.2,7.0,6.0,9.0,3.0,1.4,5.3,2.2,5.1,2.5,1.5,2.4,8.6,6.2,4.0,8.1,6.0,7.2,9.1,2.0,6.1,4.1,7.0,5.2,8.4,4.5,6.3,2.4,6.4,8.3,3.5,5.7,5.2,7.2,3.3,6.2,5.8,8.8,6.8,3.6,9.7,6.0,1.3,7.2,9.3,2.2,9.2,5.1,5.3,6.2,7.0,5.0,2.3,3.4,6.0,7.5,3.5,5.5,7.6,5.6,7.3,8.3,6.3,8.2,7.8,7.9,5.9,1.9,4.6,6.6,4.8,7.5,6.7,6.0,2.1,5.0,8.0,2.3,2.2,6.2,1.5,5.4,6.3,9.2,8.1,4.3,4.5,5.4,6.7,5.5,6.6,4.8,6.3,7.4,5.5,4.4,2.7,4.9,5.8,7.8,5.9,4.7,4.9,6.7,6.1,7.1,4.2,7.3,3.2,7.3,6.5,5.2,5.6,4.3,6.4,7.5,4.6,6.1,3.2,6.2,6.4,7.6,6.6,5.5,5.7,7.8,8.6,5.6,4.9,2.9,5.4,6.5,1.9,8.9,5.7,3.8,4.9]]}"));
    }

    private MouseAdapter buildMethodSwitcher = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(buildQueryPane.isShowing()) {
                contentPane.remove(buildQueryPane);
                contentPane.add(rawQueryPane);
                rawQueryPane.setText(buildQueryPane.getRawQueries());
                contentPane.revalidate();
                contentPane.repaint();
                rawQueryLabel.setText("<html><u>Vrati se na grafičko sastavljanje upita");
            } else {
                contentPane.remove(rawQueryPane);
                plotPane.clear();
                detailsPane.clear();
                contentPane.add(buildQueryPane);
                contentPane.revalidate();
                contentPane.repaint();
                rawQueryLabel.setText("<html><u>Izmeni upite ručno");
            }
        }
    };

    private void initComponents(int w, int h) {
        contentPane = new JPanel() {
            @Override
            public void reshape(int x, int y, int w, int h) {
                super.reshape(x, y, w, h);
                setLayoutSizes(w, h);
            }
        };
        contentPane.setBackground(BACKGROUND);
        contentPane.setLayout(null); //no layout manager can fix this mess

        buildQueryPane = new QueryBuilder(this);
        contentPane.add(buildQueryPane);

        plotPane = new Plot(false);
        contentPane.add(plotPane);

        detailsPane = new DetailsPanel();
        contentPane.add(detailsPane);

        scalingModeBox = new JCheckBox("Preklapaj upite na plotu");
        scalingModeBox.setBackground(contentPane.getBackground());
        scalingModeBox.addItemListener(e -> plotPane.setIndependentQueryScaling(scalingModeBox.isSelected()));
        contentPane.add(scalingModeBox);

        rawQueryLabel = new JLabel("<html><u>Izmeni upite ručno");
        rawQueryLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rawQueryLabel.addMouseListener(buildMethodSwitcher);
        contentPane.add(rawQueryLabel);

        rawQueryPane = new RawQueryEditor(this);
        contentPane.add(rawQueryPane);

        setLayoutSizes(w, h);
    }

    private void setLayoutSizes(int w, int h) {
        int checkboxFont = scalingModeBox.getFontMetrics(scalingModeBox.getFont()).getHeight()+5;
        int labelFont = rawQueryLabel.getFontMetrics(rawQueryLabel.getFont()).getHeight()+5;
        int hgap = (int) (w* HGAP), vgap = (int) (h* VGAP);
        int queryW = (int) (w*QUERY_WIDTH), queryH = (int)(h*QUERY_HEIGHT)-labelFont;
        int plotSize = (int)(w*PLOT_WIDTH);

        scalingModeBox.setBounds(queryW+2*hgap, (int)(plotSize+1.5*h*VGAP), plotSize, checkboxFont);
        rawQueryLabel.setBounds(2*hgap, queryH+(int)(1.5*h*VGAP), queryW, labelFont);
        buildQueryPane.setBounds(hgap, vgap, queryW, queryH);
        rawQueryPane.setBounds(hgap, vgap, queryW, queryH);
        plotPane.setBounds(queryW + 2*hgap, vgap, plotSize, plotSize);
        detailsPane.setBounds(queryW+2*hgap, plotSize+2*vgap+checkboxFont, plotSize, h-3*vgap-plotSize-checkboxFont);
    }

    //Query ignoriše boju u zahtevu kako ne bi remetio cache (pošto je boja samo eho sa servera)
    //tako da kasnije moram ručno da je postavljam u Result-u. Ovde čuvam boje iz Query-ja
    //i a kada dobijem Result od servera vraćam je
    private Map<Integer, Color> colors = new HashMap<>(2);
    @Override
    public void onQuerySubmit(int id, Query query) {
        try {
            plotPane.startLoading();
            colors.put(id, query.getColor());
            Network.submitQuery(id, query.buildQuery(), this);
        } catch (Query.BuildException e) {
            e.printStackTrace(); //todo handle error
            JOptionPane.showMessageDialog(null, "Došlo je do greške pri sastavljanju upita: "
            + e.getLocalizedMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            plotPane.stopLoading();
            plotPane.repaint();
        }
    }

    @Override
    public void onQueryRemove(int id) {
        plotPane.removeResult(id);
        detailsPane.removeResult(id);
        if(!QUERY_HIDE_ON_REMOVE)
            buildQueryPane.removePanel(id);
    }

    @Override
    public void onRequestCompleted(int id, String result) {
        boolean rawQuery = (id & RAW_MASK)!=0;
        JsonParser parser = new JsonParser();
        JsonArray parsedResults = parser.parse(result).getAsJsonArray();

        Result[] res = new Result[parsedResults.size()];
        for(int i=0; i<res.length; i++)
            res[i] = new Result(parsedResults.get(i).getAsJsonObject());
        if(!rawQuery)
            for(Result r : res) r.setColor(colors.get(id));

        EventQueue.invokeLater(() -> {
            plotPane.stopLoading();
            for(Result r : res) { //todo make clearer that res.length>1 => rawQuery
                //if(r.hasResults())
                plotPane.setResult(r, id);
                detailsPane.setResult(r, id);
            }
        });
    }

    @Override
    public void onRequestError(int id, int code, String message) {
        JOptionPane.showMessageDialog(null, "Server prijavljuje grešku " + code +
                        "\nProveri upit i pokušaj ponovo",
                "Greška", JOptionPane.ERROR_MESSAGE);
        System.err.println("request error, "  + code + ": " + message);
        plotPane.stopLoading();
    }

    @Override
    public void onExceptionThrown(int id, Throwable throwable) {
        if(throwable instanceof Error) throw (Error)throwable;
        if(throwable instanceof RuntimeException) throw (RuntimeException)throwable;
        JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom :/" +
                "\nPokušaj ponovo malo kasnije", throwable.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        throwable.printStackTrace();
    }

    private static final int RAW_MASK = 1 << 30; //needs to be high, see onRequestCompleted
    @Override
    public void onRawQueriesSubmit(String query) {
        String[] queries = query.split("\\s*\\n\\n\\s*");
        plotPane.clear();
        detailsPane.clear();
        plotPane.startLoading();
        for(int i=0; i<queries.length; i++)
            Network.submitQuery(RAW_MASK|i, queries[i], this);
    }
}
