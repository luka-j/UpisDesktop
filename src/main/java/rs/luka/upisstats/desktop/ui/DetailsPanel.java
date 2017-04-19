package rs.luka.upisstats.desktop.ui;

import rs.luka.upisstats.desktop.Utils;
import rs.luka.upisstats.desktop.model.Result;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 28.1.17..
 */
public class DetailsPanel extends JScrollPane {
    private static final DecimalFormat DF = new DecimalFormat("0.##");
    private static final String[] REZULTAT = new String[]{"rezultat", "rezultata", "rezultata"};
    private static final String[] RAZLIČIT = new String[]{"različit", "različita", "različitih"};
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    private JPanel panel;
    private List<Result> results = new ArrayList<>(4);

    public DetailsPanel() {
        panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setBackground(new Color(210, 210, 210));

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(Box.createVerticalStrut(6));
        setViewportView(panel);
    }

    public void setResult(Result res, int index) {
        if(index >= results.size()) {
            results.add(res);
        } else {
            results.set(index, res);
        }
        drawResults();
    }

    public void removeResult(int index) {
        if(index < results.size())
            results.set(index, null);
        //results.remove(index);
    }

    public void clear() {
        results.clear();
        panel.removeAll();
        panel.repaint();
    }

    private void drawResults() {
        panel.removeAll();
        for(int i=0; i<results.size(); i++) {
            if(results.get(i) != null) {
                JLabel text = new JLabel(getDetailsText(i));
                text.setFont(FONT);
                panel.add(text);
                panel.revalidate();
                panel.add(Box.createVerticalStrut(6));
            }
        }
    }

    private String getDetailsText(int index) {
        Result res = results.get(index);
        return "<html> Upit " + (index+1)
                + ": x od " + DF.format(res.getMinX()) + " do " + DF.format(res.getMaxX())
                + ", prosečno " + DF.format(res.getxAvg())
                + "; y od " + DF.format(res.getMinY()) + " do " + DF.format(res.getMaxY())
                + ", prosečno " + DF.format(res.getyAvg()) + "<br>&emsp;&emsp;&emsp;&ensp;" //eksperimentalno utvrdjeno
                + res.getCount() + " " + REZULTAT[Utils.pickPlurality(res.getCount())]
                + " (" + res.getDistinct() + " " + RAZLIČIT[Utils.pickPlurality(res.getDistinct())] + ")";
    }
}
