package rs.luka.upisstats.desktop.ui;

import rs.luka.upisstats.desktop.model.Result;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Predstavlja sve tačke jednog query-ja i radi njihov rendering.
 * Rendering se radi ovde kad je to neophodno kako paint na panelu ne bi bio skup: panel se repaintuje pri
 * svakom hoveru tako da crtanje elipsi pri svakom pozivu funkcije paintComponents traje mnogo više nego što
 * bi trebalo (testirano na primeru od 65k tačaka, lag je i više nego primetan i može premašiti 1s)
 * Created by luka on 26.1.17.
 */
public class QueryPoints {
    private static final int POINT_OPACITY = 90;
    private static final Color TRANSPARENT = new Color(0,0,0,0);

    private List<PlotPoint> points;
    private Color translucent;
    private Color solid;

    private int margin;
    private Plot plot;
    private double scaleX, scaleY;
    private double scaleSize;
    private double lastScaleX = Double.NaN, lastScaleY = Double.NaN;
    private Result values;


    private BufferedImage image;

    public QueryPoints(Color color, double scaleX, double scaleY, double scaleSize,
                       int margin, Plot plot, Result values) {
        this.margin = margin;
        this.plot = plot;
        if(Double.isFinite(scaleX)) this.scaleX = scaleX;
        if(Double.isFinite(scaleY)) this.scaleY = scaleY;
        if(Double.isFinite(scaleSize)) this.scaleSize = scaleSize;
        this.values = values;

        int r = color.getRed(), g = color.getGreen(), b = color.getBlue();
        this.translucent = new Color(r,g,b,POINT_OPACITY);
        this.solid = new Color(r,g,b);


        points = new ArrayList<>(values.getDistinct());
        for (int i = 0; i < values.getDistinct(); i++) {
            float x = values.getX(i), y=values.getY(i);
            points.add(new PlotPoint(x- values.getMinX(), y-values.getMinY(), (float) (values.getSize(i)*scaleSize), i));
        }

    }

    /**
     * Renderuje sliku koja bi se prikazivala na ekranu ako je to potrebno.
     * !! NEOPHODNO JE PODESITI SVE SKALE NA PLOTU PRVO !!
     * Ovo je skupa operacija ako su skale promenjene od prethodnog puta, inače se vraća odmah
     */
    protected void renderImage() {
        if(haveScalesChanged()) {
            image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2d = image.createGraphics();
            g2d.setBackground(TRANSPARENT);
            g2d.clearRect(0, 0, plot.getWidth(), plot.getHeight());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(translucent);
            for (PlotPoint p : points)
                g2d.fill(p);
        }
    }
    private boolean haveScalesChanged() {
        double sx, sy;
        if(!plot.isUsingPerQueryScaling()) {
            sx = plot.getScaleX();
            sy = plot.getScaleY();
        } else {
            sx = scaleX;
            sy = scaleY;
        }

        if(sx != lastScaleX || sy != lastScaleY) {
            lastScaleX = sx;
            lastScaleY = sy;
            return true;
        } else {
            return false;
        }
    }


    public Result getValues() {
        return values;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setScales(double scaleX, double scaleY, double scaleSize) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleSize = scaleSize;
    }

    public Iterator<PlotPoint> getPoints() {
        return points.listIterator();
    }





    public class PlotPoint extends Ellipse2D.Float {

        private boolean isActive;
        private int pointIndex;

        public PlotPoint(float x, float y, float size, int index) {
            super(x-size/2, y-size/2, size, size);
            this.pointIndex = index;
        }
        public float getRadius() {
            return (float)getWidth()/2;
        }

        public Color getColor() {
            if(isActive) return solid;
            return translucent;
        }

        public void setActive() { isActive = true; }
        public void setInactive() { isActive = false; }

        ///evil dynamic positioning and scaling hacks

        @Override
        public double getX() {
            double scale = scaleX, margin = QueryPoints.this.margin;
            if(!plot.isUsingPerQueryScaling()) {
                scale = plot.getScaleX();
                if(plot.getMinX() < values.getMinX()) {
                    margin += (values.getMinX()-plot.getMinX())*scale;
                }
            }

            return (super.getX()+getWidth()/2)*scale+margin-getWidth()/2;
        }

        @Override
        public double getY() {
            double scale = scaleY, margin = QueryPoints.this.margin;
            if(!plot.isUsingPerQueryScaling()) {
                scale = plot.getScaleY();
                if(plot.getMinY() < values.getMinY()) {
                    margin += (values.getMinY()-plot.getMinY())*scale;
                }
            }
            return plot.getHeight()-(super.getY()+getWidth()/2)*scale-margin-getWidth()/2;
        }

        /*@Override
        public double getWidth() {
            return super.getWidth()*scaleSize;
        }

        @Override
        public double getHeight() {
            return super.getHeight()*scaleSize;
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(x, y, (float)getWidth(), (float)getHeight());
        }*/ //todo figure out why dynamic size scaling doesn't work

        @Override
        public String toString() {
            float x = values.getX(pointIndex), y = values.getY(pointIndex);
            int c = values.getCount(pointIndex);
            return "(" + x + ", " + y + ") - " + c;
        }
    }
}
