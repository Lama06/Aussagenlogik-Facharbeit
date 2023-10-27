import java.util.List;
import java.util.Map;

public record Negation(Formel negiert) implements Formel {
    @Override
    public KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    ) {
        KNFKonvertierungsErgebnis negiertKNF = negiert.zuKNFKonvertieren(variablenZähler, klauseln, atomeZuVariablen, variablenZuAtomen);
        variablenZähler = negiertKNF.variablenZähler();

        int variable = variablenZähler++;

        klauseln.add(new int[] { negiertKNF.wurzelVariable(), variable });
        klauseln.add(new int[] { -negiertKNF.wurzelVariable(), -variable });

        return new KNFKonvertierungsErgebnis(variablenZähler, variable);
    }

    @Override
    public boolean auswerten(Map<String, Boolean> atome) {
        return !negiert.auswerten(atome);
    }

    @Override
    public String toString() {
        return "(!%s)".formatted(negiert);
    }
}
