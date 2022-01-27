import java.util.ArrayList;

public class Utilitaire {
    // Tri fusion est plus rapide que les autres tris, nombre de comparaisons divisé par près de 10.
    private static int fusionGD(ArrayList<PaireChaineEntier> Scores, int inf, int sup, int m){
        ArrayList<PaireChaineEntier> temp = new ArrayList<>();
        int i = inf;
        int j = m+1;
        int nbComp = 0;
        while (i <= m && j <= sup){
            nbComp++;
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
        return nbComp;
    }

    // Fonction appelant le tri fusion, situé juste au-dessus
    public static int triFusionInt(ArrayList<PaireChaineEntier> Scores, int inf, int sup) {
        int nbComp = 0;
        if (inf < sup) {
            int m = (inf + sup) / 2;
            nbComp += triFusionInt(Scores, inf, m);
            nbComp += triFusionInt(Scores, m + 1, sup);
            nbComp += fusionGD(Scores, inf, sup, m);
        }
        return nbComp;
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
    public static boolean estTrieString(ArrayList<PaireChaineEntier> v){
        int i = 1;
        while(i < v.size() && v.get(i).getChaine().compareTo(v.get(i - 1).getChaine()) >= 0){
            i++;
        }
        return i == v.size();
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
