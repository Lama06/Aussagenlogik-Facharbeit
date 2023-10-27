import java.util.List;
import java.util.Map;

public record Atom(String name) implements Formel {
    @Override
    public KNFKonvertierungsErgebnis zuKNFKonvertieren(
            int variablenZähler,
            List<int[]> klauseln,
            Map<String, Integer> atomeZuVariablen,
            Map<Integer, String> variablenZuAtomen
    ) {
        int variable;
        if (atomeZuVariablen.containsKey(name)) {
            variable = atomeZuVariablen.get(name);
        } else {
            variable = variablenZähler++;
            atomeZuVariablen.put(name, variable);
            variablenZuAtomen.put(variable, name);
        }
        return new KNFKonvertierungsErgebnis(variablenZähler, variable);
    }

    @Override
    public boolean auswerten(Map<String, Boolean> atome) {
        return atome.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
