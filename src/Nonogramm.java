import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Nonogramm {
    private record Daten(int breite, int höhe, List<List<Integer>> spalten, List<List<Integer>> zeilen) { }

    // https://www.janko.at/Raetsel/Nonogramme/1691.a.htm
    private static final Daten SMILEY = new Daten(
            10, 10,
            List.of(
                    List.of(10),
                    List.of(3, 2),
                    List.of(2, 5, 1),
                    List.of(1, 1, 1, 2),
                    List.of(1, 5, 1),

                    List.of(1, 5, 1),
                    List.of(1, 1, 1, 2),
                    List.of(2, 5, 1),
                    List.of(3, 2),
                    List.of(10)
            ),
            List.of(
                    List.of(10),
                    List.of(3, 3),
                    List.of(2, 4, 2),
                    List.of(1, 1, 2, 1, 1),
                    List.of(1, 1, 2, 1, 1),

                    List.of(1, 6, 1),
                    List.of(1, 1, 2, 1, 1),
                    List.of(1, 2, 2, 1),
                    List.of(2, 4, 2),
                    List.of(3, 3)
            )
    );

    // https://www.janko.at/Raetsel/Nonogramme/0544.a.htm
    private static final Daten KATZE = new Daten(
            10, 10,
            List.of(
                    List.of(),
                    List.of(4),
                    List.of(9),
                    List.of(9),
                    List.of(4, 5),

                    List.of(4),
                    List.of(3),
                    List.of(1),
                    List.of(1, 1),
                    List.of(1)
            ),
            List.of(
                    List.of(1, 1),
                    List.of(4),
                    List.of(4),
                    List.of(4),
                    List.of(2),

                    List.of(3, 1),
                    List.of(4, 1),
                    List.of(5, 1),
                    List.of(6),
                    List.of(5)
            )
    );

    /*
    https://www.janko.at/Raetsel/Nonogramme/0300.a.htm
          @@@@         @@@@
      @  @@@@@@@@@@@  @@
    @ @ @@        @@   @
    @ @ @              @
    @ @@@              @
    @@ @               @
    @@ @              @@
    @@@@  @ @@ @    @@@
    @@@@@   @@      @
    @@@@@   @@     @@
    @@@@@@        @@
    @ @@@@@@     @@@
    @  @@@ @@@     @@
     @@@@          @@@
      @@@           @@
       @@@           @@
       @@@@@@@@      @@
        @@@@@        @@
        @@@@         @@
         @@         @@
     */
    private static final Daten KOALA = new Daten(
            20, 20,
            List.of(
                    List.of(11),
                    List.of(6, 1),
                    List.of(5, 5, 2),
                    List.of(1, 13),
                    List.of(1, 3, 11),

                    List.of(3, 3, 5),
                    List.of(1, 1, 1, 4),
                    List.of(1, 2, 3),
                    List.of(1, 3, 1, 2),
                    List.of(1, 3, 1, 1),

                    List.of(1, 1),
                    List.of(1, 1),
                    List.of(1),
                    List.of(1, 1),
                    List.of(2, 2),

                    List.of(3, 5),
                    List.of(1, 3, 3, 1),
                    List.of(1, 1, 7),
                    List.of(2, 2, 4),
                    List.of(6)
            ),
            List.of(
                    List.of(4, 4),
                    List.of(1, 11, 2),
                    List.of(1, 1, 2, 2, 1),
                    List.of(1, 1, 1, 1),
                    List.of(1, 3, 1),

                    List.of(2, 1, 1),
                    List.of(2, 1, 2),
                    List.of(4, 1, 2, 1, 3),
                    List.of(5, 2, 1),
                    List.of(5, 2, 2),

                    List.of(6, 2),
                    List.of(1, 6, 3),
                    List.of(1, 3, 3, 2),
                    List.of(4, 3),
                    List.of(3, 2),

                    List.of(3, 2),
                    List.of(8, 2),
                    List.of(5, 2),
                    List.of(4, 2),
                    List.of(2, 2)
            )
    );

    public static void main(String[] args) {
        lösen(SMILEY);
        lösen(KATZE);
        lösen(KOALA);
    }

    private static void lösen(Daten daten) {
        Formel formel = F(daten.breite(), daten.höhe(), daten.spalten(), daten.zeilen());
        Optional<Map<String, Boolean>> belegung = SAT.belegungFinden(formel);
        if (belegung.isEmpty()) {
            System.out.println("Keine Lösung gefunden!");
            System.out.println();
            return;
        }

        for (int y = 1; y <= daten.höhe(); y++) {
            for (int x = 1; x <= daten.breite(); x++) {
                System.out.print(belegung.get().get("A"+x+","+y) ? "@" : " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static Formel F(int b, int h, List<List<Integer>> s, List<List<Integer>> z) {
        return new Konjunktion(
                IntStream.rangeClosed(1, b).mapToObj(x -> S(x, h, s)).reduce(Konjunktion::new).orElse(Top.INSTANCE),
                IntStream.rangeClosed(1, h).mapToObj(y -> Z(y, b, z)).reduce(Konjunktion::new).orElse(Top.INSTANCE)
        );
    }

    private static Formel S(int x, int h, List<List<Integer>> s) {
        return W(1, s.get(x-1), i -> "A"+x+","+i, h);
    }

    private static Formel Z(int y, int b, List<List<Integer>> z) {
        return W(1, z.get(y-1), i -> "A"+i+","+y, b);
    }

    private static Formel W(
        int s,
        List<Integer> a,
        Function<Integer, String> X,
        int m
    ) {
        if (a.isEmpty()) {
            return IntStream.rangeClosed(s, m).<Formel>mapToObj(i -> new Negation(new Atom(X.apply(i)))).reduce(Konjunktion::new).orElse(Top.INSTANCE);
        }

        return IntStream.rangeClosed(s, m-a.get(0)+1).mapToObj(i ->
                Stream.of(
                        IntStream.rangeClosed(s, i-1).<Formel>mapToObj(j -> new Negation(new Atom(X.apply(j)))).reduce(Konjunktion::new).orElse(Top.INSTANCE),
                        IntStream.rangeClosed(i, i+a.get(0)-1).<Formel>mapToObj(j -> new Atom(X.apply(j))).reduce(Konjunktion::new).orElse(Top.INSTANCE),
                        IntStream.rangeClosed(i+a.get(0), Math.min(i+a.get(0), m)).<Formel>mapToObj(j -> new Negation(new Atom(X.apply(j)))).reduce(Konjunktion::new).orElse(Top.INSTANCE),
                        W(i+a.get(0)+1, a.subList(1, a.size()), X, m)
                ).reduce(Konjunktion::new).orElse(Top.INSTANCE)
        ).reduce(Disjunktion::new).orElse(Bottom.INSTANCE);
    }
}