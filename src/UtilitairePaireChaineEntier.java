import java.util.ArrayList;

public class UtilitairePaireChaineEntier {


    public static int indicePourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int i = 0;
        while(i < listePaires.size() && listePaires.get(i).getChaine().compareTo(chaine) != 0){
            i++;
        }
        if (i < listePaires.size()){
            return i;
        }
        else {
            return -1;
        }
    }

    public static int indexPourChaineTrie(ArrayList<PaireChaineEntier> vPaires, String chaine){
        //Recherche de l'indice où inserer une valeur
        if (vPaires.size() == 0){
            return 0;
        }
        if (vPaires.get(vPaires.size()-1).getChaine().compareTo(chaine) < 0){
            return vPaires.size();
        }
        else{
            int inf = 0;
            int sup = vPaires.size()-1;
            int m;
            while(inf < sup){
                m = (inf + sup)/2;
                if (vPaires.get(m).getChaine().compareTo(chaine) >= 0){
                    sup = m;
                }
                else{
                    inf = m + 1;
                }
            }
            if (vPaires.get(inf).getChaine().compareTo(chaine) == 0){
                return -1;
            }
            else{
                return inf;
            }
        }

    }

    public static int recherchePourChaineTrie(ArrayList<PaireChaineEntier> vPaires, String chaine) {
        if (vPaires.get(vPaires.size()-1).getChaine().compareTo(chaine) < 0){
            return -1;
        }
        else{
            int inf = 0;
            int sup = vPaires.size()-1;
            int m;
            while(inf < sup){
                m = (inf + sup)/2;
                if (vPaires.get(m).getChaine().compareTo(chaine) >= 0){
                    sup = m;
                }
                else{
                    inf = m + 1;
                }
            }
            if (vPaires.get(inf).getChaine().compareTo(chaine) == 0){
                return inf;
            }
            else{
                return -1;
            }
        }
    }

    public static String chaineMax(ArrayList<PaireChaineEntier> listePaires) {
        PaireChaineEntier max = listePaires.get(0);
        int i = 1;
        while (i < listePaires.size()){
            if (listePaires.get(i).getEntier() > max.getEntier()){
                max = listePaires.get(i);
            }
            i++;
        }
        return max.getChaine();
    }


    public static float moyenne(ArrayList<PaireChaineEntier> listePaires) {
        int somme = 0;
        for (int i = 0; i < listePaires.size(); i++) {
            somme += listePaires.get(i).getEntier();
        }
        return somme/listePaires.size();
    }

}
