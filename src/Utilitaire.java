import java.util.ArrayList;

public class Utilitaire {
    // Tri fusion est plus rapide que les autres tris, nombre de comparaisons divisé par près de 10.
    private static void fusionGD(ArrayList<PaireChaineEntier> Scores, int inf, int sup, int m){
        ArrayList<PaireChaineEntier> temp = new ArrayList<>();
        int i = inf;
        int j = m+1;
        while (i <= m && j <= sup){
            if (Scores.get(i).getEntier() >= Scores.get(j).getEntier()) {
                temp.add(Scores.get(i));
                i++;
            } else {
                temp.add(Scores.get(j));
                j++;
            }
        }
        while (i <= m){
            temp.add(Scores.get(i));
            i++;
        }
        while (j <= sup){
            temp.add(Scores.get(j));
            j++;
        }
        int k = 0;
        int l = sup - inf;
        while (k <= l){
            Scores.set(k + inf, temp.get(k));
            k++;
        }
    }

    // Fonction appelant le tri fusion, situé juste au-dessus
    public static void triFusionInt(ArrayList<PaireChaineEntier> Scores, int inf, int sup) {
        if (inf < sup) {
            int m = (inf + sup) / 2;
            triFusionInt(Scores, inf, m);
            triFusionInt(Scores, m + 1, sup);
            fusionGD(Scores, inf, sup, m);
        }
    }



    private static void fusionGDString(ArrayList<PaireChaineEntier> Lexiques, int inf, int sup, int m){
        ArrayList<PaireChaineEntier> temp = new ArrayList<>();
        int i = inf;
        int j = m+1;
        while (i <= m && j <= sup){
            if (Lexiques.get(i).getChaine().compareTo(Lexiques.get(j).getChaine()) <= 0) {
                temp.add(Lexiques.get(i));
                i++;
            } else {
                temp.add(Lexiques.get(j));
                j++;
            }
        }
        while (i <= m){
            temp.add(Lexiques.get(i));
            i++;
        }
        while (j <= sup){
            temp.add(Lexiques.get(j));
            j++;
        }
        int k = 0;
        int l = sup - inf;
        while (k <= l){
            Lexiques.set(k + inf, temp.get(k));
            k++;
        }
    }

    // Fonction appelant le tri fusion, situé juste au-dessus
    public static void triFusionString(ArrayList<PaireChaineEntier> Scores, int inf, int sup) {
        if (inf < sup) {
            int m = (inf + sup) / 2;
            triFusionString(Scores, inf, m);
            triFusionString(Scores, m + 1, sup);
            fusionGDString(Scores, inf, sup, m);
        }
    }
}