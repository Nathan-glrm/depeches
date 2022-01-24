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


    public static void classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
    }


    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        return resultat;

    }

    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
    }

    public static int poidsPourScore(int score) {
        return 0;
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {

    }

    public static void main(String[] args) {

        //Chargement des dépêches en mémoire
        System.out.println("chargement des dépêches");
        ArrayList<Depeche> depeches = lectureDepeches("./test.txt");

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

        Categorie.classementDepeches(depeches, cat, "./res.txt");
    }


}

