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
        // Recherche dichotomique pour trouver la première depeche de la catégorie selectionnée car depeches est trié par catégorie


        //INUTILE DE FAIRE UNE RECHERCHE DICHO, LES CATEGORIES NE SONT PAS TRIE PAR ORDRE ALPHABETIQUE, FAIRE UNE SIMPLE RECHERCHE SEQUENTIELLE


        int inf = 0;
        int sup = depeches.size()-1;
        int m;
        while(inf < sup){
            m = (inf + sup) / 2;
            System.out.println(categorie);
            System.out.println(depeches.get(m).getCategorie());
            System.out.println(depeches.get(m).getCategorie().compareTo(categorie));
            if (depeches.get(m).getCategorie().compareToIgnoreCase(categorie) >= 0){
                sup = m;
            }
            else{
                inf = m + 1;
            }
        }
        if (depeches.get(sup).getCategorie().compareTo(categorie) != 0){
            throw new CategorieNotExist("Catégorie n'existe pas dans depeches");
        }
        // Sup(ou inf) correspond à l'index du premier élément de depeches correspondant à la catégorie
        int i = sup;

        // IDEE OPTI: Trier le vecteur resultat pour faire bcp moins de comparaison dans le cas où on fait sans le tempresult
        while (i < depeches.size() && depeches.get(i).getCategorie().compareTo(categorie) == 0){
            for (int j = 0; j < depeches.get(i).getMots().size(); j++) {
                if (!tempresult.contains(depeches.get(i).getMots().get(j))){
                    tempresult.add(depeches.get(i).getMots().get(j));
                    resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                }
            }
        }
        System.out.println(resultat);

        return resultat;

    }

    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
    }

    public static int poidsPourScore(int score) {
        return 0;
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {

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
            System.out.println("votre saisie a été écrite avec succès dans fichier-sortie.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        //Chargement des dépêches en mémoire
        System.out.println("chargement des dépêches");
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");

        //Affichage des depeches
        /*for (int i = 0; i < depeches.size(); i++) {
            depeches.get(i).afficher();
        }
        System.out.println("Fin de l'affichage des dépêches.");
        */

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
        culture.initLexique("./lexiques/culture.txt");
        economie.initLexique("./lexiques/economie.txt");
        environnementsiences.initLexique("./lexiques/environnements-sciences.txt");
        politique.initLexique("./lexiques/politique.txt");
        sport.initLexique("./lexiques/sports.txt");

        ArrayList<PaireChaineEntier> liste = new ArrayList<>();

        int depecheid = 105;
        liste.add(new PaireChaineEntier("Culture", culture.score(depeches.get(depecheid))));
        liste.add(new PaireChaineEntier("Economie", economie.score(depeches.get(depecheid))));
        liste.add(new PaireChaineEntier("Sciences", environnementsiences.score(depeches.get(depecheid))));
        liste.add(new PaireChaineEntier("Politique", politique.score(depeches.get(depecheid))));
        liste.add(new PaireChaineEntier("Sport", sport.score(depeches.get(depecheid))));
        /*
        System.out.println("Culture: " + culture.score(depeches.get(depecheid)));
        System.out.println("Economie: " + economie.score(depeches.get(depecheid)));
        System.out.println("Sciences: " + environnementsiences.score(depeches.get(depecheid)));
        System.out.println("Politique: " + politique.score(depeches.get(depecheid)));
        System.out.println("Sport: " + sport.score(depeches.get(depecheid)));
        */

        classementDepeches(depeches, cat, "./res.txt");


        try{
            initDico(depeches, "Culture");
        } catch (CategorieNotExist e){
            System.out.println(e);
        }

    }




}

