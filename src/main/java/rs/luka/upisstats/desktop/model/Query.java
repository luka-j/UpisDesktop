package rs.luka.upisstats.desktop.model;

import rs.luka.upisstats.desktop.Utils;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luka on 28.1.17..
 */
public class Query {
    private Prop.Target subject;
    private Prop x, y;
    private Prop roundX, roundY;
    private Color color;
    private List<Expr> subjectProps = new LinkedList<>();
    private List<Expr> smerProps = new LinkedList<>();
    private List<Expr> osProps = new LinkedList<>();
    private int year = 2016;

    public Query() {
    }

    private static String singleQuote(String str) {
        return '\'' + str + '\'';
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Prop.Target getSubject() {
        return subject;
    }

    public void setSubject(Prop.Target subject) {
        this.subject = subject;
    }

    public Prop getX() {
        return x;
    }

    public void setX(Prop rounding, Prop x) {
        if (!rounding.isOption()) throw new IllegalArgumentException("impossible rounding " + rounding);
        roundX = rounding;
        this.x = x;
    }

    public Prop getY() {
        return y;
    }

    public void setY(Prop rounding, Prop y) {
        if (!rounding.isOption()) throw new IllegalArgumentException("impossible rounding " + rounding);
        roundY = rounding;
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSubjectProp(Prop round, Prop prop, String op, String value) {
        if (prop.type == Prop.Type.NUMERIC) {
            setSubjectProp(round, prop, op, Double.parseDouble(value.trim()));
        } else {
            verifyApplicabilityToSubject(prop.target);
            subjectProps.add(new Expr<>(round, prop, op, singleQuote(value), "i"));
        }
    }

    public void setSubjectProp(Prop round, Prop prop, String op, double value) {
        verifyApplicabilityToSubject(prop.target);
        subjectProps.add(new Expr<>(round, prop, op, value, "i"));
    }

    public void setSmerProp(Prop round, Prop prop, String op, String value) {
        if (prop.type == Prop.Type.NUMERIC) {
            setSmerProp(round, prop, op, Double.parseDouble(value.trim()));
        } else {
            verifyObjectsAllowed();
            verifyApplicability(prop.target, Prop.Target.SMER);
            smerProps.add(new Expr<>(round, prop, op, singleQuote(value), "i"));
        }
    }

    public void setSmerProp(Prop round, Prop prop, String op, double value) {
        verifyObjectsAllowed();
        verifyApplicability(prop.target, Prop.Target.SMER);
        smerProps.add(new Expr<>(round, prop, op, value, "i"));
    }

    public void setOsnovnaProp(Prop round, Prop prop, String op, String value) {
        if (prop.type == Prop.Type.NUMERIC) {
            setOsnovnaProp(round, prop, op, Double.parseDouble(value.trim()));
        } else {
            verifyObjectsAllowed();
            verifyApplicability(prop.target, Prop.Target.OSNOVNA);
            osProps.add(new Expr<>(round, prop, op, singleQuote(value), "i"));
        }
    }

    public void setOsnovnaProp(Prop round, Prop prop, String op, double value) {
        verifyObjectsAllowed();
        verifyApplicability(prop.target, Prop.Target.OSNOVNA);
        osProps.add(new Expr<>(round, prop, op, value, "i"));
    }


    private void verifyApplicabilityToSubject(Prop.Target target) {
        verifyApplicability(target, subject);
    }

    private void verifyApplicability(Prop.Target of, Prop.Target to) {
        if (!of.isApplicableTo(to))
            throw new IllegalArgumentException(of + " not applicable to " + to);
    }

    private void verifyObjectsAllowed() {
        if (subject != Prop.Target.UCENIK)
            throw new IllegalArgumentException("Can't set object props to subjects other than Ucenik");
    }

    private Prop getDefaultAxis() {
        switch (subject) {
            case UCENIK:
                return Prop.SIFRA;
            case SMER:
                return Prop.SMER_KVOTA;
            case OSNOVNA:
                return Prop.BODOVI_SKOLA;
            default:
                throw new IllegalArgumentException("Nemoguć subjekt");
        }
    }

    public String buildQuery() throws BuildException {
        if (subject.isCategory()) throw new BuildException("Nemoguć subjekt");
        if ((x == Prop.EMPTY) && (y == Prop.EMPTY))
            throw new BuildException("Ose nisu postavljene");
        //if (x == Prop.EMPTY) x = getDefaultAxis();
        //if (y == Prop.EMPTY) y = getDefaultAxis();
        if (x.type != Prop.Type.NUMERIC) roundX = Prop.EMPTY;
        if (y.type != Prop.Type.NUMERIC) roundY = Prop.EMPTY;

        return buildUnchecked(true);
    }

    public String buildUnchecked(boolean ignoreColor) {
        StringBuilder sb = new StringBuilder(256);
        if(x != Prop.EMPTY) sb.append("x: ").append(roundX.name).append(x.name);
        if(x != Prop.EMPTY && y != Prop.EMPTY) sb.append(", ");
        if(y != Prop.EMPTY) sb.append("y: ").append(roundY.name).append(y.name);
        sb.append("\ncrtaj ");
        if(!ignoreColor && this.color != null) sb.append(Utils.toString(this.color));

        if (subject == null) subject = Prop.Target.UCENIK;
        sb.append(": ").append(year).append(".").append(subject.getName()).append(" ");

        subjectProps.forEach(sb::append);
        if (!smerProps.isEmpty()) {
            //todo fix i/ili upisao/pohadjao u parseru
            removeConjunction(sb);
            sb.append(" upisao ");
            smerProps.forEach(sb::append);
        }
        if (!osProps.isEmpty()) {
            removeConjunction(sb);
            sb.append(" pohadjao ");
            osProps.forEach(sb::append);
        }

        removeConjunction(sb);
        return sb.toString();
    }

    private void removeConjunction(StringBuilder sb) {
        int l = sb.length();
        if (sb.charAt(l - 1) == ' ' && sb.charAt(l - 2) == 'i')
            sb.delete(l - 2, l);
    }


    public static class BuildException extends Exception {
        private BuildException(String msg) {
            super(msg);
        }
    }

    private static class Expr<T> {
        private Prop round;
        private Prop prop;
        private String op;
        private T value;
        private String followingJunction; //"i" ili "ili" todo fix junctions (first/last, mixed object/subject order)

        private Expr(Prop round, Prop prop, String op, T value, String followingJunction) {
            this.round = round;
            this.prop = prop;
            this.op = op;
            this.value = value;
            this.followingJunction = followingJunction;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            if (round != null && prop.type == Prop.Type.NUMERIC) {
                sb.append(round.name);
            }
            sb.append(prop.name).append(op).append(value);
            if (followingJunction != null) sb.append(" ").append(followingJunction);
            sb.append(" ");
            return sb.toString();
        }
    }
}
