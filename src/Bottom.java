import java.util.List;
import java.util.Map;

public enum Bottom implements Formel {
    INSTANCE;

    @Override
    public KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    ) {
        int variable = variablenZähler++;
        klauseln.add(new int[] { -variable });
        return new KNFKonvertierungsErgebnis(variablenZähler, variable);
    }

    @Override
    public boolean auswerten(Map<String, Boolean> atome) {
        return false;
    }

    @Override
    public String toString() {
        return "false";
    }
}
