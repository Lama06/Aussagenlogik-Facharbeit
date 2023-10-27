import java.util.List;
import java.util.Map;

public record Disjunktion(Formel links, Formel rechts) implements Formel {
    @Override
    public KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    ) {
        KNFKonvertierungsErgebnis linksKNF = links.zuKNFKonvertieren(variablenZähler, klauseln, atomeZuVariablen, variablenZuAtomen);
        variablenZähler = linksKNF.variablenZähler();

        KNFKonvertierungsErgebnis rechtsKNF = rechts.zuKNFKonvertieren(variablenZähler, klauseln, atomeZuVariablen, variablenZuAtomen);
        variablenZähler = rechtsKNF.variablenZähler();

        int variable = variablenZähler++;

        klauseln.add(new int[] { linksKNF.wurzelVariable(), rechtsKNF.wurzelVariable(), -variable });
        klauseln.add(new int[] { -linksKNF.wurzelVariable(), variable });
        klauseln.add(new int[] { -rechtsKNF.wurzelVariable(), variable });

        return new KNFKonvertierungsErgebnis(variablenZähler, variable);
    }

    @Override
    public boolean auswerten(Map<String, Boolean> atome) {
        return links.auswerten(atome) || rechts.auswerten(atome);
    }

    @Override
    public String toString() {
        return "(%s | %s)".formatted(links, rechts);
    }
}
