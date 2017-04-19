package rs.luka.upisstats.desktop.model;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.luka.upisstats.desktop.io.Suggestions;

import java.util.ArrayList;
import java.util.List;

import static rs.luka.upisstats.desktop.model.Prop.Target.*;
import static rs.luka.upisstats.desktop.model.Prop.Type.*;

/**
 * ...
 * Created by luka on 28.1.17.
 */
//bukvalno mrzim ovaj enum i sve u njemu
public enum Prop {
    EMPTY(SVI, Type.EMPTY, 0, 0, "(ukloni)", "", null),

    SIFRA(UCENIK, 100000, 999999, "Šifra učenika", "sifra", null),
    ID(SVI, 1, 130549, "Sekvencijalni id", "id", null),
    KRUG(UCENIK, 0, 2, "Krug upisa", "krug", null),

    SKOLA_INFO(SKOLA, "Podaci o školi/smeru", null),
    SMER_SIFRA(SMER, "Šifra smera", "sifra", SKOLA_INFO),
    SKOLA_IME(SKOLA, "Naziv škole", "skola.ime", SKOLA_INFO),
    SKOLA_MESTO(SKOLA, "Mesto škole", "skola.mesto", SKOLA_INFO),
    SKOLA_OKRUG(SKOLA, "Okrug škole", "skola.okrug", SKOLA_INFO),
    SMER_PODRUCJE(SMER, "Područje rada", "smer.podrucje", SKOLA_INFO),
    SMER_NAZIV(SMER, "Naziv smera", "smer.ime", SKOLA_INFO),
    SMER_KVOTA(SMER, 6, 270, "Kvota", "smer.kvota", SKOLA_INFO),
    SKOLA_BR_UCENIKA(SKOLA, 1, 270, "Broj učenika", "skola.ucenika", SKOLA_INFO),

    ZELJE(UCENIK, "Lista želja", null),
    ZELJE_BROJ(UCENIK, 1, 20, "Broj želja na listi", "zelje.broj", ZELJE),
    ZELJE_UPISANA(UCENIK, 1, 20, "Redni broj upisane želje", "zelje.upisana", ZELJE),

    BODOVI(SVI, "Bodovi", null),
    BODOVI_UKUPNO(SVI, 0, 100, "Ukupno bodova", "bodovi.ukupno", BODOVI),
    BODOVI_ZAVRSNI(SVI, 0, 30, "Bodovi na završnom ispitu", "bodovi.zavrsni.ukupno", BODOVI),
    BODOVI_SKOLA(SVI, 0, 70, "Bodovi iz škole", "bodovi.skola", BODOVI),
    BODOVI_BEZ_PRIJEMNOG(SVI, 0, 100, "Bodovi bez prijemnog (škola+završni)", "bodovi.skola+bodovi.zavrsni.ukupno", BODOVI),
    BODOVI_PRIJEMNI(SVI, 0, 480, "Bodovi na prijemnom (za spec. škole)", "bodovi.prijemni", BODOVI),
    BODOVI_ZAVRSNI_MATEMATIKA(SVI, 1, 10, "Završni - matematika", "bodovi.zavrsni.matematika", BODOVI),
    BODOVI_ZAVRSNI_SRPSKI(SVI, 1, 10, "Završni - srpski jezik", "bodovi.zavrsni.srpski", BODOVI),
    BODOVI_ZAVRSNI_KOMBINOVANI(SVI, 1, 10, "Završni - kombinovani", "bodovi.zavrsni.kombinovani", BODOVI),


    OCENE(SVI, "Ocene", null),

    PROSEK(SVI, 1, 5, "Prosek ocena 6-8. razred", "prosek.ukupno", OCENE),

    PROSEK_PREDMETI(SVI, "Prosek po predmetima", OCENE),
    PROSEK_SRPSKI(SVI, 1, 5, "Srpski jezik", "prosek.srpski", PROSEK_PREDMETI),
    PROSEK_MATEMATIKA(SVI, 1, 5, "Matematika", "prosek.matematika", PROSEK_PREDMETI),
    PROSEK_FIZIKA(SVI, 1, 5, "Fizika", "prosek.fizika", PROSEK_PREDMETI),
    PROSEK_HEMIJA(SVI, 1, 5, "Hemija", "prosek.hemija", PROSEK_PREDMETI),
    PROSEK_ENGLESKI(SVI, 1, 5, "Engleski jezik", "prosek.engleski", PROSEK_PREDMETI),
    PROSEK_DRUGISTRANI(SVI, 1, 5, "Drugi strani jezik", "prosek.drugiStrani", PROSEK_PREDMETI),
    PROSEK_GEOGRAFIJA(SVI, 1, 5, "Geografija", "prosek.geografija", PROSEK_PREDMETI),
    PROSEK_BIOLOGIJA(SVI, 1, 5, "Biologija", "prosek.biologija", PROSEK_PREDMETI),
    PROSEK_ISTORIJA(SVI, 1, 5, "Istorija", "prosek.istorija", PROSEK_PREDMETI),
    PROSEK_LIKOVNO(SVI, 1, 5, "Likovno", "prosek.likovno", PROSEK_PREDMETI),
    PROSEK_TEHNICKO(SVI, 1, 5, "Tehničko", "prosek.tehnicko", PROSEK_PREDMETI),
    PROSEK_MUZICKO(SVI, 1, 5, "Muzičko", "prosek.muzicko", PROSEK_PREDMETI),
    PROSEK_FIZICKO(SVI, 1, 5, "Fizičko", "prosek.fizciko", PROSEK_PREDMETI),
    PROSEK_SPORT(SVI, 1, 5, "Izabrani sport", "prosek.sport", PROSEK_PREDMETI),
    PROSEK_VLADANJE(SVI, 1, 5, "Vladanje", "prosek.vladanje", PROSEK_PREDMETI),

    OCENE_8R(SVI, "Osmi razred", OCENE),
    _8R_PROSEK(SVI, 1, 5, "Prosek", "8r.prosek", OCENE_8R),
    _8R_SRPSKI(SVI, 1, 5, "Srpski jezik", "8r.srpski", OCENE_8R),
    _8R_MATEMATIKA(SVI, 1, 5, "Matematika", "8r.matematika", OCENE_8R),
    _8R_FIZIKA(SVI, 1, 5, "Fizika", "8r.fizika", OCENE_8R),
    _8R_HEMIJA(SVI, 1, 5, "Hemija", "8r.hemija", OCENE_8R),
    _8R_ENGLESKI(SVI, 1, 5, "Engleski jezik", "8r.engleski", OCENE_8R),
    _8R_DRUGISTRANI(SVI, 1, 5, "Drugi strani jezik", "8r.drugiStrani", OCENE_8R),
    _8R_GEOGRAFIJA(SVI, 1, 5, "Geografija", "8r.geografija", OCENE_8R),
    _8R_BIOLOGIJA(SVI, 1, 5, "Biologija", "8r.biologija", OCENE_8R),
    _8R_ISTORIJA(SVI, 1, 5, "Istorija", "8r.istorija", OCENE_8R),
    _8R_LIKOVNO(SVI, 1, 5, "Likovno", "8r.likovno", OCENE_8R),
    _8R_TEHNICKO(SVI, 1, 5, "Tehničko", "8r.tehnicko", OCENE_8R),
    _8R_MUZICKO(SVI, 1, 5, "Muzičko", "8r.muzicko", OCENE_8R),
    _8R_FIZICKO(SVI, 1, 5, "Fizičko", "8r.fizicko", OCENE_8R),
    _8R_SPORT(SVI, 1, 5, "Izabrani sport", "8r.sport", OCENE_8R),
    _8R_VLADANJE(SVI, 1, 5, "Vladanje", "8r.vladanje", OCENE_8R),

    OCENE_7R(SVI, "Sedmi razred", OCENE),
    _7R_PROSEK(SVI, 1, 5, "Prosek", "7r.prosek", OCENE_7R),
    _7R_SRPSKI(SVI, 1, 5, "Srpski jezik", "7r.srpski", OCENE_7R),
    _7R_MATEMATIKA(SVI, 1, 5, "Matematika", "7r.matematika", OCENE_7R),
    _7R_FIZIKA(SVI, 1, 5, "Fizika", "7r.fizika", OCENE_7R),
    _7R_HEMIJA(SVI, 1, 5, "Hemija", "7r.hemija", OCENE_7R),
    _7R_ENGLESKI(SVI, 1, 5, "Engleski jezik", "7r.engleski", OCENE_7R),
    _7R_DRUGISTRANI(SVI, 1, 5, "Drugi strani jezik", "7r.drugiStrani", OCENE_7R),
    _7R_GEOGRAFIJA(SVI, 1, 5, "Geografija", "7r.geografija", OCENE_7R),
    _7R_BIOLOGIJA(SVI, 1, 5, "Biologija", "7r.biologija", OCENE_7R),
    _7R_ISTORIJA(SVI, 1, 5, "Istorija", "7r.istorija", OCENE_7R),
    _7R_LIKOVNO(SVI, 1, 5, "Likovno", "7r.likovno", OCENE_7R),
    _7R_TEHNICKO(SVI, 1, 5, "Tehničko", "7r.tehnicko", OCENE_7R),
    _7R_MUZICKO(SVI, 1, 5, "Muzičko", "7r.muzicko", OCENE_7R),
    _7R_FIZICKO(SVI, 1, 5, "Fizičko", "7r.fizicko", OCENE_7R),
    _7R_SPORT(SVI, 1, 5, "Izabrani sport", "7r.sport", OCENE_7R),
    _7R_VLADANJE(SVI, 1, 5, "Vladanje", "7r.vladanje", OCENE_7R),

    OCENE_6R(SVI, "Šesti razred", OCENE),
    _6R_PROSEK(SVI, 1, 5, "Prosek", "6r.prosek", OCENE_6R),
    _6R_SRPSKI(SVI, 1, 5, "Srpski jezik", "6r.srpski", OCENE_6R),
    _6R_MATEMATIKA(SVI, 1, 5, "Matematika", "6r.matematika", OCENE_6R),
    _6R_FIZIKA(SVI, 1, 5, "Fizika", "6r.fizika", OCENE_6R),
    //ha, gotcha!
    _6R_ENGLESKI(SVI, 1, 5, "Engleski jezik", "6r.engleski", OCENE_6R),
    _6R_DRUGISTRANI(SVI, 1, 5, "Drugi strani jezik", "6r.drugiStrani", OCENE_6R),
    _6R_GEOGRAFIJA(SVI, 1, 5, "Geografija", "6r.geografija", OCENE_6R),
    _6R_BIOLOGIJA(SVI, 1, 5, "Biologija", "6r.biologija", OCENE_6R),
    _6R_ISTORIJA(SVI, 1, 5, "Istorija", "6r.istorija", OCENE_6R),
    _6R_LIKOVNO(SVI, 1, 5, "Likovno", "6r.likovno", OCENE_6R),
    _6R_TEHNICKO(SVI, 1, 5, "Tehničko", "6r.tehnicko", OCENE_6R),
    _6R_MUZICKO(SVI, 1, 5, "Muzičko", "6r.muzicko", OCENE_6R),
    _6R_FIZICKO(SVI, 1, 5, "Fizičko", "6r.fiziko", OCENE_6R),
    _6R_SPORT(SVI, 1, 5, "Izabrani sport", "6r.sport", OCENE_6R),
    _6R_VLADANJE(SVI, 1, 5, "Vladanje", "6r.vladanje", OCENE_6R),

    //kontejneri imaju sopstvene targete i baš su sranje, nmp kako da ih lepo implementiram
    CONTAINER_SMER(UCENIK, CONTAINER, 0, 0, "Upisani smer", "smer.", null),
    CONTAINER_OS(UCENIK, CONTAINER, 0, 0, "Osnovna škola", "osnovna.", null),

    ROUND_INT(SVI, OPTION, 0, 0, "Zaokruži na ceo broj", "ceobroj.", null),
    ROUND_1(SVI, OPTION, 0, 0, "Zaokruži na 1 decimalu", "zaokruzi#1.", null);

    public static List<Prop> getToplevelProps() {
        return TOPLEVEL;
    }

    private static final List<Prop> TOPLEVEL = new ArrayList<>();
    static {
        Prop[] vals = values();
        for(Prop p : vals) if(p.isTopLevel && p.type!=CONTAINER && p!=EMPTY && p.type!=OPTION) TOPLEVEL.add(p);
        //containers can't be top level because... well it's complicated, okay?
    }

    public final Target target;
    public final Type type;
    public final boolean isTopLevel;
    public final String name, description;
    public final Prop parent;
    public final double min, max;
    private final List<Prop> children = new ArrayList<>();
    Prop(Target target, double min, double max, String description, String name, Prop parent) {
        this(target, NUMERIC, min, max, description, name, parent);
    }

    Prop(Target target, String description, String name, Prop parent) {
        this(target, STRING, 0, 0, description, name, parent);
    }
    Prop(Target target, String description, Prop parent) {
        this(target, CATEGORY, 0, 0, description, null, parent);
    }
    Prop(Target target, Type type, double min, double max, String description, String name, Prop parent) {
        this.target = target;
        this.min = min;
        this.max = max;
        this.isTopLevel = parent == null;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.type = type;

        //if(isTopLevel) TOPLEVEL.add(this); http://stackoverflow.com/questions/443980/why-cant-enums-constructor-access-static-fields
        if(!isTopLevel) parent.children.add(this);
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }
    @Contract(pure = true)
    public boolean isOption() { return type == OPTION || type == Type.EMPTY; } //empty _is_ an option !

    public List<Prop> getChildren() {
        /*if(type == Type.CONTAINER) {
            List<Prop> children = new ArrayList<>(8);
            for(Prop top : TOPLEVEL)
                if(top.target.isApplicableTo(SKOLA))
                    children.add(top);
            return children;
        }*/
        return children;
    }

    @Contract(pure = true)
    public static @MagicConstant int getSuggestionType(Prop prop, Prop container) {
        switch (prop) {
            case SMER_SIFRA: return Suggestions.SIFRE_SMEROVA;
            case SKOLA_OKRUG: return Suggestions.OKRUZI;
            case SMER_PODRUCJE: return Suggestions.PODRUCJA_RADA;
            case SMER_NAZIV: return Suggestions.NAZIVI_SMEROVA;
            case SKOLA_IME: return (container == CONTAINER_OS) ? Suggestions.NAZIVI_OS : Suggestions.NAZIVI_SS;
            case SKOLA_MESTO: return (container == CONTAINER_OS) ? Suggestions.MESTO_OS : Suggestions.MESTO_SS;
            default: return -1;
        }
    }

    @Contract(pure = true)
    public boolean isCategory() {
        return type == CATEGORY || type == CONTAINER;
    }


    public enum Target {
        NONE(0) {
            @Override
            public String getName() {
                return "";
            }
        }, UCENIK(0b001, "ucenik"), SKOLA(0b110), SMER(0b100, "smer"), OSNOVNA(0b010, "osnovna"), SVI(0b111);

        /*private static final int TYPE_UCENIK = 1;
        private static final int TYPE_OSNOVNA = 2;
        private static final int TYPE_SMER = 4;*/

        private final int target;
        private final String name;
        private final boolean isCategory;

        Target(int target) {
            this.target = target;
            name = null;
            isCategory = true;
        }
        Target(int target, String name) {
            this.target = target;
            this.name = name;
            isCategory = false;
        }

        /**
         * Voleo bih da mogu da napišem javadoc za ovo najviše da sebe ne zbunjujem više,
         * ali ne znam, zaista ne znam
         *
         * v2: komutativna funkcija, iz praktičnih razloga. Vraća false za potpuno
         * disjunktne Targete (kombinacije SMER-UCENIK-OSNOVNA i SKOLA-UCENIK), u suprotnom true.
         * @param target
         * @return
         */
        @Contract(pure = true)
        public boolean isApplicableTo(Target target) {
            if(target == null) return false;
            boolean ret = /*this.target >= target.target &&*/ (target.target & this.target) > 0;
            //System.out.println(this + " applicable to " + target + ": " + ret);
            return ret;
        }
        public String getName() {
            return name;
        }
        public boolean isCategory() {
            return isCategory;
        }
        @NotNull
        @Contract(pure = true)
        public Target and(@Nullable Target other) {
            /*if(this == SVI || this == other) return other;
            if(other == SVI) return this;
            if(!isApplicableTo(other) || this == NONE || other == NONE) return NONE;

            if(this == SKOLA) return other;
            return this;*/
            if(other == null) return NONE;
            int target = this.target & other.target;
            for(Target t : values())
                if(t.target == target)
                    return t;
            throw new RuntimeException("Can't perform conjunction on Prop; you messed up");
        }

        @Override
        public String toString() {
            switch (this) {
                case UCENIK: return "učenike";
                case OSNOVNA: return "osnovne škole";
                case SMER: return "smerove";
                default: return getName();
            }
        }
    }

    public enum Type {
        OPTION(),
        CATEGORY(),
        CONTAINER(),
        EMPTY(),
        STRING ("=", "!="),
        NUMERIC("=", "!=", ">", ">=", "<", "<=");

        private final String[] operators;

        Type(String... operators) {
            this.operators = operators;
        }

        public String[] getApplicableOperators() {
            return operators;
        }
    }
}
