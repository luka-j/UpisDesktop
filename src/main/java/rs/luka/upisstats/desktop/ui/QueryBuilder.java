package rs.luka.upisstats.desktop.ui;

import rs.luka.upisstats.desktop.Utils;
import rs.luka.upisstats.desktop.io.Suggestions;
import rs.luka.upisstats.desktop.model.Prop;
import rs.luka.upisstats.desktop.model.Query;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static rs.luka.upisstats.desktop.ui.MainFrame.DEFAULT_ORANGE;

/**
 * Created by luka on 28.1.17..
 */
public class QueryBuilder extends JScrollPane {

    private static final float ALIGNMENT = 0.02f;

    private JPanel panel;
    private JButton addQuery;
    private Callbacks callbacks;
    private List<SingleQueryPanel> queries = new ArrayList<>(4);

    public QueryBuilder(Callbacks callbacks) {
        super();
        panel = new JPanel();
        this.callbacks = callbacks;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setBackground(MainFrame.BACKGROUND);
        setViewportView(panel);

        addQuery = Utils.generateIconButton("add.png", 20, "Dodaj");
        addQuery.addActionListener(e -> {
            lastQuery().run.doClick(0); //execute last query
            addQuery();
            //probably the ugliest code around here following:
            //PropSelectBox dirtyHack = queries.get(0).expressions.get(0).propBox;
            //dirtyHack.setText(dirtyHack.getText() + '\uFEFF');
            /*
                 ok, ovo zahteva objašnjenje: paneli koji se koriste za query-je iz nekog razloga imaju
                 height postavljen tako da filluje ceo glavni JPanel i deli tu visinu s ostalom decom.
                 Minimum i preferred sizes su u redu, WrapLayout (LayoutManager za child panele) vraća ok
                 vrednosti, međutim height tih panela je i dalje toliki da filluje roditelja. Ali, to se
                 menja kada se nešto na tim panelima promeni što valjda triggeruje neko preračunavanje
                 veličina. I sad bi normalan čovek mislio "ok, znači repaintujem panele i sve bude ok",
                 što se u praksi ne dešava. Probao sam i repaint i invalidate svih child panela u petlji
                 ovde, zajedno sa repaintom i invalidateom glavnog panela i to nije imalo nikakvog uspeha.

                 Ove dve linije gore simuliraju promenu na dugmetu tako što na postojeći tekst dodaju
                 byte-order mark char, iliti zero-with nonbreaking space, odnosno nevidljiv karakter.
             */

            panel.revalidate(); //nvm, revalidate, ne invalidate ffs
        });
        addQuery.setAlignmentX(ALIGNMENT);
        panel.add(addQuery);

        SingleQueryPanel first = new SingleQueryPanel(0);
        first.setAlignmentX(ALIGNMENT);
        queries.add(first);
        panel.add(first, 0);
        panel.add(Box.createVerticalStrut(10), 1);
    }

    private SingleQueryPanel lastQuery() {
        for (int i = queries.size() - 1; i >= 0; i--)
            if (queries.get(i) != null)
                return queries.get(i);
        return null;
    }

    private void disableAddingQuery() {
        addQuery.setVisible(false);
    }

    private void enableAddingQuery() {
        addQuery.setVisible(true);
    }

    /**
     * Removes query panel from main panel and sets its entry to the queries list to null
     *
     * @param index
     */
    void removePanel(int index) {
        //queries.get(index).setVisible(false);
        panel.remove(queries.get(index));
        queries.set(index, null);
        if (!areTherePanels()) addQuery();
        panel.revalidate();
    }

    /**
     * Removes query panel both from main panel and list (might screw up external ids)
     *
     * @param queryPanel
     */
    private void purgePanel(SingleQueryPanel queryPanel) {
        panel.remove(queryPanel);
        queries.remove(queryPanel);
        if (!areTherePanels()) addQuery();
        panel.revalidate();
    }

    private void addQuery() {
        SingleQueryPanel query = new SingleQueryPanel(queries.size());
        query.setAlignmentX(ALIGNMENT);
        queries.add(query);
        panel.add(query, panel.getComponentCount() - 1);
        panel.add(Box.createVerticalStrut(10), panel.getComponentCount() - 1);
    }

    private boolean areTherePanels() {
        for (SingleQueryPanel p : queries)
            if (p != null)
                return true;
        return false;
    }

    public String getRawQueries() {
        StringBuilder allQueries = new StringBuilder(512);
        for (SingleQueryPanel query : queries) {
            allQueries.append(query.buildQuery().buildUnchecked(false)).append("\n\n");
        }
        return allQueries.toString();
    }

    public interface Callbacks {
        void onQuerySubmit(int id, Query query);

        void onQueryRemove(int id);
    }



    private class SingleQueryPanel extends JPanel implements PropSelectBox.Callbacks {

        private static final int ID_PROP_X = 0;
        private static final int ID_PROP_Y = 1;
        private final Color DEFAULT_COLOR = DEFAULT_ORANGE;
        private final int index;
        //ako korisnik obriše query pre nego što ga submituje i MainFrame registruje da on postoji,
        //poremeti ostale id-ove (pošto su sve liste a ne mape kako je trebalo da bude)
        private boolean isKnown = false;
        private WrapLayout layout;
        private List<Expression> expressions = new ArrayList<>(8);
        private boolean addedActionButtons = false;
        private JButton run, remove;
        private Prop.Target subject = Prop.Target.NONE;
        private int year = 2016;
        private Color color = DEFAULT_COLOR;
        private HashSet<Integer> propsInitialized = new HashSet<>(8);
        private JComboBox<Prop.Target> subjectBox;
        private Prop.Target[] possibleSubjects = new Prop.Target[]{Prop.Target.UCENIK, Prop.Target.SMER, Prop.Target.OSNOVNA};

        private SingleQueryPanel(int index) {
            this.index = index;
            setBackground(MainFrame.BACKGROUND);
            layout = new WrapLayout(WrapLayout.LEFT);
            setLayout(layout);

            ColorChooserButton colorChooser = new ColorChooserButton(DEFAULT_COLOR);
            colorChooser.addColorChangedListener(newColor -> color = newColor);
            add(colorChooser);

            add(new JLabel("x:"));
            PropSelectBox x = new PropSelectBox(ID_PROP_X, "Na x osi biće...", Prop.Target.SVI, this, false, false);
            expressions.add(new Expression(x));
            add(x);
            add(new JLabel("  y:"));
            PropSelectBox y = new PropSelectBox(ID_PROP_Y, "Na y osi biće...", Prop.Target.SVI, this, false, false);
            expressions.add(new Expression(y));
            add(y);
            disableAddingQuery();
        }

        private void addActionButtons() {
            int h = expressions.get(0).propBox.getHeight() - 8;
            run = Utils.generateIconButton("run.png", h, "Crtaj");
            run.addActionListener(e -> {
                callbacks.onQuerySubmit(index, buildQuery());
                isKnown = true;
            });

            remove = Utils.generateIconButton("redx.png", h, "Izbaci");
            remove.addActionListener(e -> {
                if (isKnown)
                    callbacks.onQueryRemove(index);
                else //shh, they don't have to know
                    purgePanel(this);
            });
            add(run);
            add(remove);
            addedActionButtons = true;
            enableAddingQuery();

        }

        private void addSubjectCombobox() {
            subjectBox = new JComboBox<>();
            subjectBox.addItemListener(e -> {
                if (e.getItem() != subject && e.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 2; i < expressions.size(); i++)
                        if (expressions.get(i) != null)
                            expressions.get(i).changeTarget((Prop.Target) e.getItem());
                    subject = (Prop.Target) e.getItem();
                }
            });
            JComboBox<Integer> yearBox = new JComboBox<>(new Integer[]{2016, 2015});
            yearBox.addItemListener(e -> year = (int) e.getItem());
            add(new JLabel(", crtaj "));
            add(yearBox);
            add(subjectBox);
            add(new JLabel("takve da"));
            addCondition();
        }

        private void addCondition() {
            PropSelectBox box = new PropSelectBox(expressions.size(), "Odaberi uslov", subject, this, true, true);
            Expression expr = new Expression(box);
            expressions.add(expr);
            add(expr);
        }

        private Query buildQuery() {
            Query query = new Query();
            PropSelectBox xBox = expressions.get(ID_PROP_X).propBox, yBox = expressions.get(ID_PROP_Y).propBox;
            query.setX(xBox.getSelectedRounding(), xBox.getSelectedProp());
            query.setY(yBox.getSelectedRounding(), yBox.getSelectedProp());
            query.setColor(color);
            query.setYear(year);
            query.setSubject(subject);
            for (int i = ID_PROP_Y + 1; i < expressions.size(); i++) {
                Expression expr = expressions.get(i);
                if (expr != null && expr.isInitialized()) {
                    Prop rounding = expr.propBox.getSelectedRounding();
                    Prop container = expr.propBox.getSelectedPropContainer();
                    Prop selected = expr.propBox.getSelectedProp();
                    String op = expr.op.getSelectedItem().toString(), value = expr.getValue();
                    if (container == null) {
                        query.setSubjectProp(rounding, selected, op, value);
                    } else if (container == Prop.CONTAINER_OS) {
                        query.setOsnovnaProp(rounding, selected, op, value);
                    } else if (container == Prop.CONTAINER_SMER) {
                        query.setSmerProp(rounding, selected, op, value);
                    }
                }
            }

            return query;
        }

        @Override
        public void onPropSelected(int id, Prop prop) {
            if (id == ID_PROP_X || id == ID_PROP_Y) {
                if (!addedActionButtons) {
                    addActionButtons();
                    addSubjectCombobox();

                }
                Prop.Target commonTarget = expressions.get(ID_PROP_X).propBox.getSelectedProp().target
                        .and(expressions.get(ID_PROP_Y).propBox.getSelectedProp().target);
                changeAllowedSubjects(commonTarget);
                //if(id == ID_PROP_X) expressions.get(ID_PROP_Y).changeAxisTarget(commonTarget);
                //else                expressions.get(ID_PROP_X).changeAxisTarget(commonTarget);
            } else {
                Expression expr = expressions.get(id);
                if (!propsInitialized.contains(id)) {
                    expr.initComponents();
                    addCondition();
                    propsInitialized.add(id);
                } else {
                    expr.updateComponents();
                    if (prop == Prop.EMPTY && id != expressions.size() - 1) {
                        remove(expr);
                        expressions.set(id, null); //remove() remeti id-ove
                        revalidate();
                    }
                }
            }
        }

        private void changeAllowedSubjects(Prop.Target target) {
            Prop.Target oldSubject = subject;

            subjectBox.removeAllItems();
            for (Prop.Target sub : possibleSubjects) {
                if (sub.isApplicableTo(target))
                    subjectBox.addItem(sub);
            }
            if (oldSubject.isApplicableTo(target)) //if subject is impossible, just pick the first one
                subject = oldSubject;
            for (int i = 2; i < expressions.size(); i++)
                if (expressions.get(i) != null)
                    expressions.get(i).changeTarget(subject);
            subjectBox.setSelectedItem(subject); //in case subject wasn't changed, set it to selected in subjectBox
        }

        @Override
        public Component add(Component comp) {
            if (addedActionButtons) {
                return super.add(comp, getComponentCount() - 2);
            } else {
                return super.add(comp);
            }
        }

        //yeah, let's have 3 levels deep nested classes, what could possibly go wrong?

        /**
         * Non-breaking
         */
        private class Expression extends JPanel {
            private PropSelectBox propBox;
            private JComboBox<String> op;
            private JComponent value;
            private JLabel conjunction = new JLabel("i");

            public Expression(PropSelectBox propBox) {
                this.propBox = propBox;
                super.getInsets().left = super.getInsets().right = 0;
                add(propBox);
                setBackground(MainFrame.BACKGROUND);
            }

            private void initComponents() {
                Prop selected = propBox.getSelectedProp();
                if (selected.type.getApplicableOperators().length > 0) {
                    op = new JComboBox<>(selected.type.getApplicableOperators());
                    add(op);
                }

                if (selected.type == Prop.Type.NUMERIC) {
                    value = new JNumberField(4);
                    add(value);
                    add(conjunction);
                } else if (selected.type == Prop.Type.STRING) {
                    JComboBox<String> value = new JComboBox<>(
                            Suggestions.getSuggestionsFor(
                                    Prop.getSuggestionType(
                                            propBox.getSelectedProp(), propBox.getSelectedPropContainer()
                                    )
                            ));
                    this.value = value;
                    value.setEditable(true);
                    new AutoCompletition(value);
                    add(value);
                    add(conjunction);
                } else if (selected.type == Prop.Type.EMPTY) {
                    removeComponents();
                } else {
                    System.err.println("Unknown type: " + selected.type);
                }
                //value.setMinimumSize(new Dimension(op.getWidth(), propBox.getHeight()));
            }

            private void updateComponents() {
                removeComponents();
                initComponents();
            }

            private void removeComponents() {
                if (op != null) {
                    remove(op);
                    op = null;
                }
                if (value != null) {
                    remove(value);
                    value = null;
                }
                remove(conjunction);
                //propBox.setText("Odaberi uslov");
            }

            private String getValue() {
                if (propBox.getSelectedProp().type == Prop.Type.NUMERIC) {
                    return ((JNumberField) value).getText();
                } else if (propBox.getSelectedProp().type == Prop.Type.STRING) {
                    return ((JComboBox<String>) value).getSelectedItem().toString();
                } else {
                    return null;
                }
            }

            private boolean isInitialized() {
                return value != null;
            }

            private void changeTarget(Prop.Target newTarget) {
                if (isInitialized()) {
                    Prop container = propBox.getSelectedPropContainer();
                    if (container != null && newTarget != Prop.Target.UCENIK)
                        removeComponents();
                    else if (container == null && !propBox.getSelectedProp().target.isApplicableTo(newTarget))
                        removeComponents();
                }
                propBox.changeTarget(newTarget);
            }

            private void changeAxisTarget(Prop.Target newTarget) {
                propBox.changeTarget(newTarget, false);
            }
        }
    }
}
