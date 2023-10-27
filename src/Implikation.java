import java.util.List;
import java.util.Map;

public record Implikation(Formel prämisse, Formel konklustion) implements Formel {
    @Override
    public KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    ) {
        KNFKonvertierungsErgebnis prämisseKNF = prämisse.zuKNFKonvertieren(variablenZähler, klauseln, atomeZuVariablen, variablenZuAtomen);
        variablenZähler = prämisseKNF.variablenZähler();

        KNFKonvertierungsErgebnis konklusionKNF = konklustion.zuKNFKonvertieren(variablenZähler, klauseln, atomeZuVariablen, variablenZuAtomen);
        variablenZähler = konklusionKNF.variablenZähler();

        int variable = variablenZähler++;

        klauseln.add(new int[] { -prämisseKNF.wurzelVariable(), konklusionKNF.wurzelVariable(), -variable });
        klauseln.add(new int[] { prämisseKNF.wurzelVariable(), variable });
        klauseln.add(new int[] { -konklusionKNF.wurzelVariable(), variable });

        return new KNFKonvertierungsErgebnis(variablenZähler, variable);
    }

    @Override
    public boolean auswerten(Map<String, Boolean> atome) {
        return !prämisse.auswerten(atome) || konklustion.auswerten(atome);
    }

    @Override
    public String toString() {
        return "(%s => %s)".formatted(prämisse, konklustion);
    }
}
