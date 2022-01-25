import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Categorie {

    private String nom; // le nom de la catégorie p.ex : sport, politique,...
    private ArrayList<PaireChaineEntier> lexique; //le lexique de la catégorie

    // constructeur
    public Categorie(String nom) {
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }


    public  ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }


    // initialisation du lexique de la catégorie à partir du contenu d'un fichier texte
    public void initLexique(String nomFichier) {
        ArrayList<PaireChaineEntier> Paires = new ArrayList<>();
        System.out.println("Chargement du fichier " + nomFichier);
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier); // open file for read
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] paireChaineEntier = line.split(":"); // get line, separate on ":", store in list
                String chaine = paireChaineEntier[0]; // get first part of line, word itself
                int entier = Integer.parseInt(paireChaineEntier[1]); // get second part of line, word ponderation
                PaireChaineEntier unePaire = new PaireChaineEntier(chaine, entier); // New object to store line values
                Paires.add(unePaire);
            }
            scanner.close();
            this.lexique = Paires;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fin du chargement de " + nomFichier);
    }



    // calcul du score d'une dépêche pour la catégorie
    public int score(Depeche d) {
        ArrayList<String> mots = d.getMots();
        int i = 0;
        int score = 0;
        while (i < lexique.size()) { // Loop for each lexical word
            if (mots.contains(lexique.get(i).getChaine().toLowerCase())) { // if lexical word is found in the depeche
                int y = 0;
                while (y < mots.size()){ // Loop to count word occurences
                    if (mots.get(y).compareTo(lexique.get(i).getChaine().toLowerCase()) == 0){ // Compare each word of the depeche to the lexical values
                        score += lexique.get(i).getEntier(); // Sum scores
                    }
                    y++;
                }
            }
            i++;
        }
        return score;
    }

    @Override
    public String toString() {
        // Method to print the category and its values
        return "Categorie{" + "nom='" + nom + '\'' + ", lexique=" + lexique + '}';
    }
}
