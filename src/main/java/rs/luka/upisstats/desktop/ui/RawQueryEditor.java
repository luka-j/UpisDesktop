package rs.luka.upisstats.desktop.ui;

import org.jetbrains.annotations.NotNull;
import rs.luka.upisstats.desktop.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by luka on 5.2.17..
 */
public class RawQueryEditor extends JPanel {

    private JScrollPane scroll;
    private JTextArea editor;
    private JButton submit;
    private Callbacks callbacks;

    public interface Callbacks {
        void onRawQueriesSubmit(String query);
    }

    public RawQueryEditor(@NotNull Callbacks callbacks) {
        this.callbacks = callbacks;

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        editor = new JTextArea();
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        editor.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

        scroll = new JScrollPane(editor);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setAlignmentX(0.5f);
        add(scroll);

        submit = Utils.generateIconButton("run.png", 20, "crtaj");
        submit.setAlignmentX(0.5f);
        submit.addActionListener(e -> callbacks.onRawQueriesSubmit(editor.getText()));
        add(submit);
    }

    public void setText(String text) {
        editor.setText(text);
    }
}
