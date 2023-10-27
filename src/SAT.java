import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

public final class SAT {
    public static Optional<Map<String, Boolean>> belegungFinden(Formel formel) {
        List<int[]> klauseln = new ArrayList<>();
        Map<String, Integer> atomeZuVariablen = new HashMap<>();
        Map<Integer, String> variablenZuAtomen = new HashMap<>();
        Formel.KNFKonvertierungsErgebnis formelKNF = formel.zuKNFKonvertieren(1, klauseln, atomeZuVariablen, variablenZuAtomen);
        klauseln.add(new int[] { formelKNF.wurzelVariable() });

        ISolver solver = SolverFactory.newDefault();
        solver.newVar(formelKNF.variablenZähler()-1);
        
        solver.setExpectedNumberOfClauses(klauseln.size());
        for (int[] klausel : klauseln) {
            try {
                solver.addClause(VecInt.of(klausel));
            } catch (ContradictionException e) {
                return Optional.empty();
            }
        }

        int[] modell;
        try {
            modell = solver.findModel();
        } catch (TimeoutException e) {
            return Optional.empty();
        }

        Map<String, Boolean> modellVariablen = new HashMap<>();
        for (int literal : modell) {
            int variable = Math.abs(literal);
            if (!variablenZuAtomen.containsKey(variable)) {
                continue;
            }
            String variableName = variablenZuAtomen.get(variable);
            modellVariablen.put(variableName, literal > 0);
        }

        if (!formel.auswerten(modellVariablen)) {
            throw new IllegalStateException();
        }

        return Optional.of(modellVariablen);
    }
}
