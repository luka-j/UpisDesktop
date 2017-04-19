package rs.luka.upisstats.desktop.ui;

import rs.luka.upisstats.desktop.Utils;
import rs.luka.upisstats.desktop.model.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static rs.luka.upisstats.desktop.ui.MainFrame.DEFAULT_ORANGE;

//todo mouseEntered/mouseExited za hover
/**
 * Panel na kom se iscrtava plot. Rezultati mu se dodaju kroz {@link #setResult(Result, int)}
 * Mora biti kvadratan (tj. on će se sam pobrinuti da bude kvadratan).
 * Created by luka on 24.1.17.
 */
public class Plot extends JPanel implements MouseMotionListener {
    private static final int AXIS_LINE_MARGIN = 20;
    private static final int TOOLTIP_DELAY = 300;
    private static final int AXIS_LABELS = 8;
    private static final double SIZE_RATIO = 330.0;
    private static final Color MINOR_AXES_COLOR = new Color(220, 220, 220);
    private static final Color AXIS_LABEL_COLOR = new Color(100, 100, 100);
    private static final DecimalFormat AXIS_LABEL_FORMAT = new DecimalFormat("0.##");

    private List<QueryPoints> queries = new ArrayList<>();
    private QuickSpatialMap hoverMap;

    private volatile boolean isLoading;
    private Timer loadingTimer = new Timer(300, e -> Plot.this.repaint());
    private Hover hover = new Hover();
    private Thread hoveringDelayThread = new Thread(hover);
    private QueryPoints.PlotPoint hoveredPoint;

    /**
     * True if each query should preserve its own scale and has independent axis values (no axis labeling),
     * used for relative results
     * False if each query should adjust its scale in accordance to other queries' values (global axis labeling)
     */
    private boolean independentQueryScaling = true;

    private double scaleX = Double.POSITIVE_INFINITY, scaleY = Double.POSITIVE_INFINITY;
    private double minX = Double.POSITIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;
    private double scaleSize;

    public void setIndependentQueryScaling(boolean independentQueryScaling) {
        if(independentQueryScaling != this.independentQueryScaling) {
            this.independentQueryScaling = independentQueryScaling;
            rescale(getWidth(), getHeight());
            repaint();
        }
    }

    public Plot(boolean independentQueryScaling) {
        super();
        this.setLayout(null);
        this.independentQueryScaling = independentQueryScaling;
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
    }

    public boolean isUsingPerQueryScaling() {
        return independentQueryScaling;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public double getMinX() {
        return minX == Double.POSITIVE_INFINITY ? 0 : minX;
    }

    public double getMinY() {
        return minY == Double.POSITIVE_INFINITY ? 0 : minY;
    }

    public double getMaxX() {
        return maxX == Double.NEGATIVE_INFINITY ? 10 : maxX;
    }

    public double getMaxY() {
        return maxY == Double.NEGATIVE_INFINITY ? 10 : maxY;
    }

    public void notifyOnScreen() {
        hoverMap = new QuickSpatialMap(getWidth(), getHeight());
        scaleSize = getWidth()/SIZE_RATIO;
    }

    /**
     * {@inheritDoc}
     *
     * Fairly inexpensive. Puts prerendered images on screen and draws hover overlay if necessary.
     * Draws random points if {@link #isLoading} is set.
     * @param graphics
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        //System.out.println("painting plot panel");
        Graphics2D g2d = (Graphics2D) graphics;
        labelAxes(g2d);

        if(isLoading) {
            Point[] points = Utils.generateRandomPoints(getWidth()/4, getWidth(), getHeight());
            g2d.setPaint(DEFAULT_ORANGE);
            for(Point p : points) g2d.fillRect(p.x, p.y, 3, 3);
        } else {
            for(QueryPoints points : queries)
                if(points != null)
                    g2d.drawImage(points.getImage(), 0, 0, null);

            if (hoveredPoint != null) {
                g2d.setPaint(hoveredPoint.getColor());
                g2d.fill(hoveredPoint);
                g2d.setPaint(AXIS_LABEL_COLOR);
                g2d.drawLine((int) hoveredPoint.getCenterX(), 0, (int) hoveredPoint.getCenterX(), getHeight());
                g2d.drawLine(0, (int) hoveredPoint.getCenterY(), getWidth(), (int) hoveredPoint.getCenterY());
                //lepše izgleda kad je tekst preko hover axis-a
                g2d.setPaint(Color.BLACK);
                g2d.drawString(hoveredPoint.toString(), pickLabelX(hoveredPoint), pickLabelY(hoveredPoint));
            }
        }
    }

    private int pickLabelX(QueryPoints.PlotPoint point) {
        return point.getMinX() < 0 ? (int) point.getMaxX() : (int) point.getMinX();
    }
    private int pickLabelY(QueryPoints.PlotPoint point) {
        return point.getMinY() < 0 ? (int) point.getMaxY() : (int) point.getMinY();
    }


    public void stopLoading() {
        isLoading = false;
        loadingTimer.stop();
    }

    public void startLoading() {
        isLoading = true;
        loadingTimer.start();
    }

    private double firstMultipleAbove(double limit, double multiple) {
        if(Double.isInfinite(limit)) throw new ArithmeticException("Infinite limit!");
        double val=multiple;
        while(val<limit) val+=multiple;
        return val;
    }
    private void labelAxes(Graphics2D graphics) {
        //round to int only in for loop - rounding problems (tested)
        double stepX = pickStep(getMinX(), getMaxX(), AXIS_LABELS);
        double stepY = pickStep(getMinY(), getMaxY(), AXIS_LABELS);
        double dx = independentQueryScaling ? getWidth()/AXIS_LABELS : (stepX*scaleX);
        double dy = independentQueryScaling ? getHeight()/AXIS_LABELS : (stepY*scaleY);
        double x = firstMultipleAbove((int)getMinX(),stepX);
        double startX = independentQueryScaling ? AXIS_LINE_MARGIN + dx : ((x-getMinX()) * scaleX + AXIS_LINE_MARGIN);
        double y = firstMultipleAbove((int)getMinY(),stepY);
        double startY = independentQueryScaling ? AXIS_LINE_MARGIN + dy : ((y-getMinY()) * scaleY + AXIS_LINE_MARGIN);

        for(double px = startX; px < getWidth(); px+=dx, x+=stepX) {
            graphics.setPaint(MINOR_AXES_COLOR);
            graphics.drawLine((int)px, 0, (int)px, getHeight());
            if(!independentQueryScaling) {
                graphics.setPaint(AXIS_LABEL_COLOR);
                graphics.drawString(AXIS_LABEL_FORMAT.format(x), (int)px - 10, getHeight() - 5);
            }
        }
        for(double py = startY; py < getHeight(); py+=dy, y+=stepY) {
            graphics.setPaint(MINOR_AXES_COLOR);
            graphics.drawLine(0, getHeight()-(int)py, getWidth(), getHeight()-(int)py);
            if(!independentQueryScaling) {
                graphics.setPaint(AXIS_LABEL_COLOR);
                graphics.drawString(AXIS_LABEL_FORMAT.format(y), 5, getHeight() - (int)py - 5);
            }
        }
        graphics.setPaint(AXIS_LABEL_COLOR);
        graphics.drawLine(0, getHeight(), getWidth(), 0);

        graphics.setColor(Color.BLACK);
        graphics.drawLine(AXIS_LINE_MARGIN, 0, AXIS_LINE_MARGIN, getHeight());
        graphics.drawLine(0, getHeight() - AXIS_LINE_MARGIN, getWidth(), getHeight() - AXIS_LINE_MARGIN);
    }

    private double pickStep(double min, double max, int count) { //todo test
        if((max-min)/count < 0.1)
            return Utils.round((max-min)/count, 2);
        if(max-min < count)
            return Utils.round((max-min)/count, 1);
        int ideal = (int)(max-min)/count;
        if(ideal < 5) return ideal;
        if(ideal < 50) return (ideal/5)*5;
        int div = 1;
        while(div<ideal) div*=10;
        div/=10;
        return (ideal/div)*div;
    }


    public void setResult(Result res, int position) {
        int w = getWidth(), h = getHeight();
        double scaleX = (w - AXIS_LINE_MARGIN) / (res.getMaxX() - res.getMinX());
        double scaleY = (h - AXIS_LINE_MARGIN) / (res.getMaxY() - res.getMinY());

        QueryPoints points = new QueryPoints(res.getColor(), scaleX, scaleY, scaleSize, AXIS_LINE_MARGIN, this, res);
        if(position >= queries.size())
            this.queries.add(points);
        else
            this.queries.set(position, points);

        rescale(w, h);
        resetHoverMap();
        repaint();
    }

    public void removeResult(int position) {
        if(position>=0 && position<queries.size()) {
                queries.set(position, null);
            rescale(getWidth(), getHeight());
            repaint();
        }
    }

    private void rescale(int w, int h) {
        if(hoverMap == null) return; //class still uninitialized
        scaleSize = w/SIZE_RATIO;
        resetExtremes();
        for(QueryPoints points : queries) {
            if(points != null) {
                Result res = points.getValues();
                double scaleX = (w - AXIS_LINE_MARGIN) / (res.getMaxX() - res.getMinX());
                double scaleY = (h - AXIS_LINE_MARGIN) / (res.getMaxY() - res.getMinY());
                points.setScales(scaleX, scaleY, scaleSize);
                if(independentQueryScaling)
                    adjustScales(res, w, h);
                else
                    adjustExtremes(res);
            }
        }
        if(!independentQueryScaling && areThereQueries())
            adjustScales(null, w, h);
        resetHoverMap();

        for(QueryPoints query : queries)
            if(query != null) {
                query.renderImage();
            }
    }

    private boolean areThereQueries() {
        for(QueryPoints points : queries)
            if(points != null)
                return true;
        return false;
    }

    private void resetExtremes() {
        minY = minX = Double.POSITIVE_INFINITY;
        maxY = maxX = Double.NEGATIVE_INFINITY;
    }

    //note to self:
    //naredne dve metode predstavljaju crnu magiju koju ne treba dirati ni pod kojim uslovima
    //nemam objašnjenje kako i zašto, samo znam da napokon rade kako treba.
    //idi nađi nešto pametnije

    private boolean adjustExtremes(Result res) {
        boolean adjusted = false;
        if (res.getMinX() < minX) {
            minX = res.getMinX();
            adjusted = true;
        }
        if (res.getMaxX() > maxX) {
            maxX = (res.getMaxX());
            adjusted = true;
        }
        if (res.getMinY() < minY) {
            minY = (res.getMinY());
            adjusted = true;
        }
        if (res.getMaxY() > maxY) {
            maxY = (res.getMaxY());
            adjusted = true;
        }
        return adjusted;
    }
    private boolean adjustScales(Result res, int w, int h) {
        boolean scaleChanged = false;
        if(res != null && adjustExtremes(res)) scaleChanged = true;
        double scaleX, scaleY;
        if(!independentQueryScaling) {
            scaleX = (w - AXIS_LINE_MARGIN) / (maxX - minX);
            scaleY = (h - AXIS_LINE_MARGIN) / (maxY - minY);
            if(scaleX != this.scaleX) {
                this.scaleX = scaleX;
                scaleChanged = true;
            }
            if(scaleY != this.scaleY) {
                this.scaleY = scaleY;
                scaleChanged = true;
            }
        } else {
            scaleX = (w - AXIS_LINE_MARGIN) / (res.getMaxX() - res.getMinX());
            scaleY = (h - AXIS_LINE_MARGIN) / (res.getMaxY() - res.getMinY());
            if (scaleX < this.scaleX) {
                this.scaleX = scaleX;
                scaleChanged = true;
            }
            if (scaleY < this.scaleY) {
                this.scaleY = scaleY;
                scaleChanged = true;
            }
        }

        return scaleChanged;
    }

    public void clear() {
        queries.clear();
        hoverMap.clear();
        scaleX = scaleY = Double.POSITIVE_INFINITY;
        resetExtremes();
        repaint();
    }

    /* max 100ms */
    private void resetHoverMap() { //todo move to background thread ?
        hoverMap.clear();
        for (QueryPoints point : this.queries)
            if(point != null)
                hoverMap.addAll(point);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //change perspective
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (hoverMap.hasPointOn(x, y)) {
            QueryPoints.PlotPoint p = hoverMap.getPoint(x, y);
            if(p != hover.point) {
                if(hoveredPoint!=null) hoveredPoint.setInactive();
                hoveringDelayThread.interrupt();
                hover.point = p;
                hoveringDelayThread = new Thread(hover);
                hoveringDelayThread.start();
            }
        } else if(hoveredPoint != null) {
            hoveringDelayThread.interrupt();
            hoveredPoint.setInactive();
            hoveredPoint = null;
            repaint();
        }
    }

    @Override
    public void reshape(int x, int y, int w, int h) { //force square
        int s = Math.min(w, h);
        super.reshape(x, y, s, s);
        rescale(s, s);
    }

    /**
     * Purpose: provide timeout for hover. This Runnable spends most of its life sleeping.
     * When, and more importantly if, it wakes up, it marks given point as hovered and triggers repaint.
     * Usually it'll be interrupted before it gets the chance to be useful.
     * Sometimes I identify with it.
     */
    private class Hover implements Runnable {
        private QueryPoints.PlotPoint point;
        private boolean asleep = true; //šansa da će ova promenljiva biti korisna: <0.0001%
        //razlog postojanja: perfection

        @Override
        public void run() {
            try {
                Thread.sleep(TOOLTIP_DELAY);
                asleep = false;
                doHover();
            } catch (InterruptedException e) {
                if(!asleep) doHover();
            }
        }
        private void doHover() { //idempotent operation
            hoveredPoint = point;
            point.setActive();
            EventQueue.invokeLater(Plot.this::repaint);
        }
    }
}
