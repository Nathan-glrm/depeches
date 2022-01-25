import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Classification {


    private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
        //creation d'un tableau de dépêches
        ArrayList<Depeche> depeches = new ArrayList<>();
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String id = ligne.substring(3);
                ligne = scanner.nextLine();
                String date = ligne.substring(3);
                ligne = scanner.nextLine();
                String categorie = ligne.substring(3);
                ligne = scanner.nextLine();
                String lignes = ligne.substring(3);
                while (scanner.hasNextLine() && !ligne.equals("")) {
                    ligne = scanner.nextLine();
                    if (!ligne.equals("")) {
                        lignes = lignes + '\n' + ligne;
                    }
                }
                Depeche uneDepeche = new Depeche(id, date, categorie, lignes);
                depeches.add(uneDepeche);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return depeches;
    }

    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) throws CategorieNotExist {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        ArrayList<String> tempresult = new ArrayList<>();
        categorie = categorie.toUpperCase();
        //Les depeches étant regroupées par catégorie, on recherche l'index de la première depeche de la catégorie recherchée.

        int i = 0;
        while(i < depeches.size() && depeches.get(i).getCategorie().compareTo(categorie) != 0){
            i++;
        }
        if (i == depeches.size()){
            throw new CategorieNotExist("Catégorie n'existe pas dans depeches");
        }
        // i correspond à l'index du premier élément de depeches correspondant à la catégorie


        // IDEE OPTI: Trier le vecteur resultat pour faire bcp moins de comparaison dans le cas où on fait sans le tempresult
        while (i < depeches.size() && depeches.get(i).getCategorie().compareTo(categorie) == 0){
            for (int j = 0; j < depeches.get(i).getMots().size(); j++) {
                if (!tempresult.contains(depeches.get(i).getMots().get(j))){
                    tempresult.add(depeches.get(i).getMots().get(j));
                    resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                }
            }
            i++;
        }

        return resultat;

    }

    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        int i = 0;
        categorie = categorie.toUpperCase();
        while(i < dictionnaire.size()){
            int j = 0;
            while (j < depeches.size()) {
                if (depeches.get(j).getMots().contains(dictionnaire.get(i).getChaine())) {
                    if (depeches.get(j).getCategorie().compareTo(categorie) == 0) {
                        dictionnaire.get(i).increment();
                    } else {
                        dictionnaire.get(i).decrement();
                    }
                }
                j++;
            }
            i++;
        }
    }


    private static void fusionGD(ArrayList<PaireChaineEntier> Scores, int inf, int sup, int m){
        ArrayList<PaireChaineEntier> temp = new ArrayList<>();
        int i = inf;
        int j = m+1;
        while (i <= m && j <= sup){
            if (Scores.get(i).getEntier() >= Scores.get(j).getEntier()){
                temp.add(Scores.get(i));
                i++;
            }
            else{
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

    private static void triFusion(ArrayList<PaireChaineEntier> Scores, int inf, int sup) {
        if (inf < sup) {
            int m = (inf + sup) / 2;
            triFusion(Scores, inf, m);
            triFusion(Scores, m + 1, sup);
            fusionGD(Scores, inf, sup, m);
        }
    }

    public static ArrayList<PaireChaineEntier> removeNegative(ArrayList<PaireChaineEntier> Scores){
        int inf = 0;
        int sup = Scores.size();
        int m;
        while (inf < sup){
            m = (inf + sup) / 2;
            if (Scores.get(m).getEntier() <= -3){
                sup = m;
            }
            else{
                inf = m + 1;
            }
        }
        return new ArrayList<>(Scores.subList(0, inf));
    }


    //poidsPourScore de base
    /*public static int poidsPourScore(int score) {
        if (score > 6){
            return 3;
        }
        if (score > 3){
            return 2;
        }
        else{
            return 1;
        }
    }*/

    //poids
    public static void poidsPourScore(ArrayList<PaireChaineEntier> dico) {
        int i = dico.size();
        int premierTier = i/3;
        int deuxiemeTier = (2*i)/3;
        for (i = 0; i<premierTier; i++) {
            dico.get(i).setEntier(3);
        }
        for (i=premierTier; i<deuxiemeTier; i++) {
            dico.get(i).setEntier(2);
        }
        for (i=deuxiemeTier; i< dico.size(); i++) {
            dico.get(i).setEntier(1);
        }
    }

    /*public static void poidsPourScore(ArrayList<PaireChaineEntier> dico) {
        int max = dico.get(0).getEntier();
        int premierTiers = max/4;
        int deuxiemeTiers = max/2;
        int i = 0;
        while (dico.get(i).getEntier() > deuxiemeTiers) {
            dico.get(i).setEntier(3);
            i++;
        }
        while (dico.get(i).getEntier() > premierTiers) {
            dico.get(i).setEntier(2);
            i++;
        }
        while (i < dico.size()) {
            dico.get(i).setEntier(1);
            i++;
        }
    }*/

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {

        //Generation des dictionnaire

        try{

            ArrayList<PaireChaineEntier> dico = initDico(depeches, categorie);
            calculScores(depeches, categorie, dico);
            triFusion(dico, 0, dico.size() - 1);
            dico = removeNegative(dico);
            //Generation des fichiers lexiques
            FileWriter file = new FileWriter("./autoLexiques/" + nomFichier);


            //Dans le cas ou dico change directement dico
            poidsPourScore(dico);
            for (int i = 0; i < dico.size()-1; i++) {
                //dico.get(i).setEntier(poidsPourScore(dico.get(i).getEntier()));
                file.write(dico.get(i).getChaine() + ':' + dico.get(i).getEntier() + "\n");
            }
            //Préviens du rajout d'un saut de ligne dans le fichier lexiques
            file.write(dico.get(dico.size()-1).getChaine() + ':' + dico.get(dico.size()-1).getEntier());

            file.close();
            System.out.println(dico);
        } catch (CategorieNotExist e){
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static void classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        int i = 0;
        try {
            FileWriter file = new FileWriter(nomFichier);

            ArrayList<PaireChaineEntier> depechePerCat = new ArrayList<>();
            depechePerCat.add(new PaireChaineEntier("aucune", 0));
            for (int j = 0; j < categories.size(); j++) {
                depechePerCat.add(new PaireChaineEntier(categories.get(j).getNom(), 0));
            }

            ArrayList<PaireChaineEntier> correctGuess = new ArrayList<>();
            for (int k = 0; k < categories.size(); k++) {
                correctGuess.add(new PaireChaineEntier(categories.get(k).getNom(), 0));
            }
            while (i < depeches.size()) {

                //Calculer le score de la dépeche pour chaque catégories
                ArrayList<PaireChaineEntier> listeDeScore = new ArrayList<>();

                listeDeScore.add(new PaireChaineEntier("aucune", 0));
                for (int j = 0; j < categories.size(); j++) {
                    listeDeScore.add(new PaireChaineEntier(categories.get(j).getNom(), categories.get(j).score(depeches.get(i))));
                }
                System.out.println(listeDeScore);
                //Recuperer la meilleur catégorie
                String cat = UtilitairePaireChaineEntier.chaineMax(listeDeScore);
                System.out.println(depeches.get(i).getId()+":"+cat);



                int index = UtilitairePaireChaineEntier.indicePourChaine(depechePerCat, cat);
                depechePerCat.get(index).setEntier(depechePerCat.get(index).getEntier() + 1);


                if (depeches.get(i).getCategorie().compareToIgnoreCase(cat) == 0){
                    int indexCorrectGuess = UtilitairePaireChaineEntier.indicePourChaine(correctGuess, cat);
                    correctGuess.get(indexCorrectGuess ).setEntier(correctGuess.get(indexCorrectGuess).getEntier() + 1);
                }

                file.write(depeches.get(i).getId()+':'+cat.toUpperCase() + "\n");

                i++;
            }
            float moyenne = 0.0f;
            for (int j = 0; j < correctGuess.size(); j++) {
                file.write(correctGuess.get(j).getChaine().toUpperCase() + ":" + correctGuess.get(j).getEntier() + "%\n");
                moyenne += correctGuess.get(j).getEntier();
            }
            //Caclculer le pourcentage de réussite
            file.write("MOYENNE: " + moyenne/categories.size() + "%");


            System.out.println(depechePerCat);
            System.out.println(correctGuess);
            System.out.println("MOYENNE: " + moyenne/categories.size() + "%");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        //Chargement des dépêches en mémoire
        System.out.println("chargement des dépêches");
        ArrayList<Depeche> depechesForLexique = lectureDepeches("./depeches.txt");
        ArrayList<Depeche> depeches = lectureDepeches("./test.txt");

        generationLexique(depechesForLexique, "environnement-sciences", "./environnement-sciences.txt");
        generationLexique(depechesForLexique, "culture", "./culture.txt");
        generationLexique(depechesForLexique, "economie", "./economie.txt");
        generationLexique(depechesForLexique, "politique", "./politique.txt");
        generationLexique(depechesForLexique, "sports", "./sports.txt");

        // Create different categories
        ArrayList<PaireChaineEntier> paires = new ArrayList<>();
        Categorie culture = new Categorie("culture");
        Categorie economie = new Categorie("economie");
        Categorie environnementsiences = new Categorie("environnement-sciences");
        Categorie politique = new Categorie("politique");
        Categorie sport = new Categorie("sports");


        // New ArrayList for categories
        ArrayList<Categorie> cat = new ArrayList<>();
        cat.add(culture);
        cat.add(economie);
        cat.add(environnementsiences);
        cat.add(politique);
        cat.add(sport);

        // Initialize lexical vectors
        String lexiques = "./autoLexiques/";
        culture.initLexique(lexiques + "culture.txt");
        economie.initLexique(lexiques + "economie.txt");
        environnementsiences.initLexique(lexiques + "environnement-sciences.txt");
        politique.initLexique(lexiques + "politique.txt");
        sport.initLexique(lexiques + "sports.txt");




        classementDepeches(depeches, cat, "./res.txt");



    }




}

