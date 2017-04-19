package rs.luka.upisstats.desktop.ui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.luka.upisstats.desktop.model.Prop;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by luka on 29.1.17..
 */
public class PropSelectBox extends JButton {
    private static final List<Prop> TOPLEVEL = Prop.getToplevelProps();
    private int id;
    private String emptyText;
    private boolean allowStrings;
    private boolean allowEmpty;
    private boolean buildContainers;
    private JPopupMenu menu;
    private @NotNull Prop selectedProp;
    private @Nullable Prop selectedPropContainer;
    private PropCheckbox roundInt;
    private PropCheckbox round1;
    //note to self: ako dodajes round2, pregledaj usages ovoga, pojavljuje se na vise mesta
    private Prop.Target subject;
    private boolean showPopup = false;
    private boolean isShowingPopup = false;
    private Callbacks callbacks;

    public PropSelectBox(int id, String text, Prop.Target subject, Callbacks callbacks, boolean allowStrings, boolean allowEmpty) {
        this.id = id;
        this.allowStrings = allowStrings;
        this.allowEmpty = true;
        this.buildContainers = subject == Prop.Target.UCENIK;
        this.emptyText = text;
        this.subject = subject;
        selectedProp = Prop.EMPTY;
        setText(text);

        this.callbacks = callbacks;
        menu = new JPopupMenu();
        menu.setVisible(false);
        setupListeners();
        buildMenuFor(subject);
    }

    public Prop.Target getSubject() {
        return subject;
    }

    @Nullable
    public Prop getSelectedPropContainer() {
        return selectedPropContainer;
    }

    @NotNull
    public Prop getSelectedRounding() {
        if (roundInt.isSelected()) return roundInt.prop;
        if (round1.isSelected()) return round1.prop;
        return Prop.EMPTY;
    }

    public Prop getSelectedProp() {
        if (selectedProp == null) return Prop.EMPTY;
        return selectedProp;
    }

    //bunch of ugly hacks, really
    private void setupListeners() {
        addActionListener(e -> {
            /*if(menu.getX() == 0) {
                Point loc = getLocationOnScreen();
                menu.setLocation((int)loc.getX(), (int) loc.getY()+getHeight());
            }*/
            //System.out.println("menu visible: " + showPopup);
            if (!showPopup || !isShowingPopup) {
                showPopup = true;
                menu.show(PropSelectBox.this, getX(), getY() + getHeight() - 5);
                menu.setLocation((int) getLocationOnScreen().getX(), (int) getLocationOnScreen().getY() + getHeight());
                // ^^ workaround to fix broken layout and account for window moving
                menu.requestFocus();
            } else {
                showPopup = false;
                menu.setVisible(false);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isShowingPopup) {
                    showPopup = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup = true;
            }
        });
        menu.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                isShowingPopup = false;
            }

            @Override
            public void focusGained(FocusEvent e) {
                isShowingPopup = true;
            }
        });
    }

    public void changeTarget(Prop.Target newTarget, boolean buildContainers) {
        if (newTarget != subject) {
            this.buildContainers = buildContainers;
            buildMenuFor(newTarget);
            if(!subject.isApplicableTo(newTarget)) {
                selectedProp=Prop.EMPTY;
                selectedPropContainer=null;
                setText(emptyText);
            }
            subject = newTarget;
        }
    }

    public void changeTarget(Prop.Target newTarget) {
        changeTarget(newTarget, newTarget == Prop.Target.UCENIK);
    }

    private void buildMenuFor(Prop.Target subject) {
        menu.removeAll();
        if (allowEmpty) {
            menu.add(new PropMenuItem(Prop.EMPTY, null));
        }
        generateItemsFor(subject, null).forEach(p -> menu.add(PropItem.cast(p)));
        if (buildContainers) {
            PropItem osContainer = constructItem(Prop.CONTAINER_OS, null);
            generateItemsFor(Prop.Target.OSNOVNA, Prop.CONTAINER_OS)
                    .forEach(osContainer::add);
            menu.add(PropItem.cast(osContainer));
            PropItem smerContainer = constructItem(Prop.CONTAINER_SMER, null);
            generateItemsFor(Prop.Target.SMER, Prop.CONTAINER_SMER)
                    .forEach(smerContainer::add);
            menu.add(PropItem.cast(smerContainer));
        }

        roundInt = new PropCheckbox(Prop.ROUND_INT);
        round1 = new PropCheckbox(Prop.ROUND_1);
        roundInt.addItemListener(new RoundingCheckboxListener(round1));
        menu.add(roundInt);
        round1.addItemListener(new RoundingCheckboxListener(roundInt));
        menu.add(round1);
    }

    private List<PropItem> generateItemsFor(Prop.Target subject, Prop container) {
        Set<Prop> allItems = Arrays.stream(Prop.values())
                .filter(p -> determineApplicability(p, subject))
                .collect(Collectors.toSet());
        List<PropItem> items = new ArrayList<>(), topLevel = new ArrayList<>();
        TOPLEVEL.stream().filter(p -> determineApplicability(p, subject)).forEach(p -> {
            PropItem item = constructItem(p, container);
            items.add(item);
            topLevel.add(item);
        });
        for (int i = 0; i < items.size(); i++) {
            PropItem curr = items.get(i);
            if (allItems.contains(curr.getProp())) {
                for (Prop childProp : curr.getProp().getChildren()) {
                    if (!allItems.contains(childProp)) continue;
                    PropItem newItem = constructItem(childProp, container);
                    items.add(newItem);
                    PropItem.cast(curr).add(PropItem.cast(newItem));
                }

                allItems.remove(curr.getProp());
            }
        }

        return topLevel;
    }

    //needed fixing multiple times. also easier for lambdas.
    @Contract(pure = true)
    private boolean determineApplicability(Prop prop, Prop.Target subject) {
        if (!allowStrings && prop.type == Prop.Type.STRING) return false;
        //if(!buildContainers && prop.type == Prop.Type.CONTAINER) return false;
        return prop.target.isApplicableTo(subject);
    }

    private void setProp(PropMenuItem propItem) {
        boolean roundIntSel = roundInt.isSelected(), round1Sel = round1.isSelected();
        roundInt.setSelected(false);
        round1.setSelected(false);
        Prop prop = propItem.prop;
        this.selectedProp = prop;
        if (prop == Prop.EMPTY)
            setText(emptyText);
        else if (propItem.getContainer() == null)
            setText(prop.name);
        else
            setText(propItem.getContainer().name + prop.name);
        roundInt.setSelected(roundIntSel);
        round1.setSelected(round1Sel);
        selectedPropContainer = propItem.container;
        callbacks.onPropSelected(id, prop);
    }

    private PropItem constructItem(Prop prop, Prop container) {
        if (prop.isCategory())
            return new PropMenu(prop, container);
        else
            return new PropMenuItem(prop, container);
    }

    interface Callbacks {
        void onPropSelected(int id, Prop prop);
    }

    private interface PropItem extends MenuElement {
        static JMenuItem cast(PropItem item) {
            return (JMenuItem) item;
        }

        Prop getProp();

        PropItem add(PropItem p);
    }


    private class RoundingCheckboxListener implements ItemListener {
        private PropCheckbox[] otherBoxes;

        /**
         * @param otherBoxes PropCheckBoxovi koji će biti uncheck-ovani kad se ovaj box checkuje
         */
        public RoundingCheckboxListener(PropCheckbox... otherBoxes) {
            this.otherBoxes = otherBoxes;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            PropCheckbox src = (PropCheckbox) e.getItem();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (PropCheckbox b : otherBoxes) b.setSelected(false); //potentially changes propbox text
                String btnText = PropSelectBox.this.getText();
                if (!btnText.equals(emptyText))
                    PropSelectBox.this.setText(src.prop.name + btnText);
            } else {
                String btnText = PropSelectBox.this.getText();
                if (btnText.startsWith(src.prop.name))
                    PropSelectBox.this.setText(btnText.substring(src.prop.name.length()));
            }
        }
    }

    private class PropMenuItem extends JMenuItem implements PropItem {
        private Prop prop;
        private Prop container;

        private PropMenuItem(Prop prop, Prop container) {
            super(prop.description);
            if (container != null && container.type != Prop.Type.CONTAINER)
                throw new IllegalArgumentException("Attempting to set " + container + " as container");
            this.prop = prop;
            this.container = container;
            addActionListener(e -> {
                PropSelectBox.this.setProp(this); //todo continue
            });
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PropItem && ((PropItem) obj).getProp().equals(prop);
        }

        @Override
        public int hashCode() {
            return prop.hashCode();
        }

        @Override
        public Prop getProp() {
            return prop;
        }

        @Override
        public PropItem add(PropItem p) {
            return (PropItem) super.add(PropItem.cast(p));
        }

        public Prop getContainer() {
            return container;
        }

        @Override
        public String toString() {
            return prop.description;
        }
    }

    private class PropMenu extends JMenu implements PropItem {
        private Prop prop;
        private Prop container;

        private PropMenu(Prop prop, Prop container) {
            super(prop.description);
            this.prop = prop;
            this.container = container;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PropItem && ((PropItem) obj).getProp().equals(prop);
        }

        @Override
        public int hashCode() {
            return prop.hashCode();
        }

        @Override
        public Prop getProp() {
            return prop;
        }

        @Override
        public PropItem add(PropItem p) {
            return (PropItem) super.add(PropItem.cast(p));
        }

        public Prop getContainer() {
            return container;
        }

        @Override
        public String toString() {
            return prop.description;
        }
    }

    private class PropCheckbox extends JCheckBoxMenuItem implements PropItem {
        private Prop prop;

        private PropCheckbox(Prop prop) {
            super(prop.description);
            this.prop = prop;
        }

        @Override
        public Prop getProp() {
            return prop;
        }

        @Override
        public PropItem add(PropItem p) {
            throw new UnsupportedOperationException("one does not simply add stuff to checkbox");
        }

        //sprecava zatvaranje popupa kada se pritisne checkbox
        @Override
        protected void processMouseEvent(MouseEvent evt) { //ima u ovom projektu i gorih stvari od ovoga
            if (evt.getID() == MouseEvent.MOUSE_PRESSED) return;
            if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                doClick();
                setArmed(true);
            } else {
                super.processMouseEvent(evt);
            }
        }
    }
}
