import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class YajisanKazusan {
    private record Pfeil(int x, int y, Richtung richtung, int zahl) { }
    private record Daten(int breite, int höhe, Set<Pfeil> pfeile) { }

    // https://www.janko.at/Raetsel/Yajisan-Kazusan/001.a.htm#
    private static final Daten RÄTSEL1 = new Daten(
            10, 10,
            Set.of(
                    new Pfeil(7, 2, Richtung.LINKS, 3),
                    new Pfeil(8, 2, Richtung.OBEN, 3),

                    new Pfeil(1, 4, Richtung.OBEN, 3),
                    new Pfeil(3, 4, Richtung.UNTEN, 3),

                    new Pfeil(1, 5, Richtung.UNTEN, 3),
                    new Pfeil(3, 5, Richtung.UNTEN, 3),
                    new Pfeil(5, 5, Richtung.RECHTS, 3),
                    new Pfeil(6, 5, Richtung.UNTEN, 3),

                    new Pfeil(5, 6, Richtung.OBEN, 3),
                    new Pfeil(6, 6, Richtung.LINKS, 3),
                    new Pfeil(9, 6, Richtung.OBEN, 3),

                    new Pfeil(2, 7, Richtung.RECHTS, 3),
                    new Pfeil(10, 7, Richtung.LINKS, 3),

                    new Pfeil(7, 9, Richtung.LINKS, 3),
                    new Pfeil(8, 9, Richtung.OBEN, 4),
                    new Pfeil(9, 9, Richtung.LINKS, 3),

                    new Pfeil(4, 10, Richtung.OBEN, 3)
            )

    );

    public static void main(String[] args) {
        lösen(RÄTSEL1);
    }

    private static void lösen(Daten daten) {
        Set<Position> B = daten.pfeile.stream().map(pfeil -> new Position(pfeil.x, pfeil.y)).collect(Collectors.toSet());
        Map<Position, Richtung> r = daten.pfeile.stream().collect(Collectors.toMap(pfeil -> new Position(pfeil.x, pfeil.y), Pfeil::richtung));
        Map<Position, Integer> l = daten.pfeile.stream().collect(Collectors.toMap(pfeil -> new Position(pfeil.x, pfeil.y), Pfeil::zahl));
        Formel formel = F(daten.breite, daten.höhe, B, r, l);

        Optional<Map<String, Boolean>> belegung = SAT.belegungFinden(formel);
        if (belegung.isEmpty()) {
            System.out.println("Keine Lösung gefunden!");
            System.out.println();
            return;
        }

        for (int y = 1; y <= daten.höhe; y++) {
            for (int x = 1; x <= daten.breite; x++) {
                if (belegung.get().get("A"+x+","+y)) {
                    System.out.print("@");
                    continue;
                }
                if (l.containsKey(new Position(x, y))) {
                    System.out.print(Math.min(9, l.get(new Position(x, y))));
                    continue;
                }
                System.out.print("_");
            }
            System.out.println();
        }
        System.out.println();
    }

    private enum Richtung {
        LINKS(-1, 0),
        RECHTS(1, 0),
        OBEN(0, -1),
        UNTEN(0, 1);

        private final int x, y;

        Richtung(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private record Position(int x, int y) {
        private boolean istKorrekt(int b, int h) {
            return x >= 1 && y >= 1 && x <= b && y <= h;
        }
    }

    private static Position S(Position pos, Richtung r) {
        return new Position(pos.x+r.x, pos.y+r.y);
    }

    private static Set<Position> N(Position pos, int b, int h) {
        return Stream.of(
                new Position(pos.x-1, pos.y),
                new Position(pos.x, pos.y-1),
                new Position(pos.x, pos.y+1),
                new Position(pos.x+1, pos.y)
        ).filter(p -> p.istKorrekt(b, h)).collect(Collectors.toSet());
    }

    private static Formel F(int b, int h, Set<Position> B, Map<Position, Richtung> r, Map<Position, Integer> l) {
        return new Konjunktion(G(b, h), H(b, h, B, r, l));
    }

    private static Formel G(int b, int h) {
        return IntStream.rangeClosed(1, b).mapToObj(x ->
                IntStream.rangeClosed(1, h).<Formel>mapToObj(y ->
                        new Implikation(
                                new Atom("A"+x+","+y),
                                N(new Position(x, y), b, h).stream().<Formel>map(nachbar ->
                                    new Negation(new Atom("A"+nachbar.x+","+nachbar.y))
                                ).reduce(Konjunktion::new).orElse(Top.INSTANCE)
                        )
                ).reduce(Konjunktion::new).orElse(Top.INSTANCE)
        ).reduce(Konjunktion::new).orElse(Top.INSTANCE);
    }

    private static Formel H(int b, int h, Set<Position> B, Map<Position, Richtung> r, Map<Position, Integer> l) {
        return B.stream().<Formel>map(pos ->
                new Implikation(
                        new Negation(new Atom("A"+pos.x+","+pos.y)),
                        W(S(pos, r.get(pos)), r.get(pos), l.get(pos), b, h)
                )
        ).reduce(Konjunktion::new).orElse(Top.INSTANCE);
    }

    @SuppressWarnings("DuplicateExpressions")
    private static Formel W(Position pos, Richtung r, int l, int b, int h) {
        if (l == 0) {
            if (pos.istKorrekt(b, h)) {
                return new Konjunktion(new Negation(new Atom("A"+pos.x+","+pos.y)), W(S(pos, r), r, 0, b, h));
            } else {
                return Top.INSTANCE;
            }
        } else {
            if (pos.istKorrekt(b, h)) {
                return new Disjunktion(
                        new Konjunktion(new Atom("A"+pos.x+","+pos.y), W(S(pos, r), r, l-1, b, h)),
                        new Konjunktion(new Negation(new Atom("A"+pos.x+","+pos.y)), W(S(pos, r), r, l, b, h))
                );
            } else {
                return Bottom.INSTANCE;
            }
        }
    }
}
