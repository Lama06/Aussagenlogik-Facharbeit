import java.util.List;
import java.util.Map;

public interface Formel {
    record KNFKonvertierungsErgebnis(int variablenZähler, int wurzelVariable) {}

    KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    );

    boolean auswerten(Map<String, Boolean> atome);
}
