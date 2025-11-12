package models;

import java.util.Map;
/**
 * Sumar de statistici "light" pentru colectie:
 * total titluri, total exemplare, disponibile/imprumutate, plus distributii pe genuri.
 * <p>Contine si vectori pentru a sustine cerinta referitoare la masive.</p>
 */
public class Statistic {
    public final int totalTitles;                 // număr titluri distincte
    public final int totalCopies;                 // total exemplare
    public final int availableCopies;             // exemplare disponibile
    public final int borrowedCopies;              // exemplare împrumutate (total - disponibile)

    public final Map<String, Integer> titlesByGenre;  // gen -> #titluri
    public final Map<String, Integer> copiesByGenre;  // gen -> #exemplare (total)

    public final int[] totals;

    // Ordinea genurilor (pentru a interpreta vectorul de mai jos)
    public final String[] genresOrder;

    // Vector paralel cu genresOrder: copiesByGenreVec[i] = exemplare totale pentru genresOrder[i]
    public final int[] copiesByGenreVec;

    public Statistic(int totalTitles, int totalCopies, int availableCopies, int borrowedCopies,
                     Map<String, Integer> titlesByGenre, Map<String, Integer> copiesByGenre) {
        this.totalTitles = totalTitles;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.borrowedCopies = borrowedCopies;
        this.titlesByGenre = titlesByGenre;
        this.copiesByGenre = copiesByGenre;

        // ---- construim vectorii primitivi (fără să schimbăm restul codului din proiect) ----
        this.totals = new int[] { totalTitles, totalCopies, availableCopies, borrowedCopies };

        // stabilim o ordine stabilă a genurilor (cea din map, care e LinkedHashMap din BookService)
        this.genresOrder = this.titlesByGenre.keySet().toArray(new String[0]);

        // vector paralel cu ordinea de mai sus
        this.copiesByGenreVec = new int[this.genresOrder.length];
        for (int i = 0; i < this.genresOrder.length; i++) {
            String g = this.genresOrder[i];
            this.copiesByGenreVec[i] = this.copiesByGenre.getOrDefault(g, 0);
        }
    }
}
