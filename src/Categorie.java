import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Categorie {

    private final String nom; // le nom de la catégorie p.ex : sport, politique,...
    private ArrayList<PaireChaineEntier> lexique; //le lexique de la catégorie

    // constructeur
    public Categorie(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }

    public void setLexique(ArrayList<PaireChaineEntier> lexique) {
        this.lexique = lexique;
    }

    // initialisation du lexique de la catégorie à partir du contenu d'un FICHIER TEXTE
    // Cette methode est inutilisé car les lexiques sont initialisé dans generationLexique()
    public void initLexique(String nomFichier) {
        ArrayList<PaireChaineEntier> Paires = new ArrayList<>();
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
            //Verifie que Paires est trié par ordre alphabétique car c'est nécessaire pour la suite du programme
            if (!Utilitaire.estTrieString(Paires)) {
                Utilitaire.triFusionString(Paires, 0, Paires.size() - 1);
            }
            this.lexique = Paires;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // calcul du score d'une dépêche pour la catégorie
    public PaireEntierComparaison score(Depeche d) {
        int score = 0;
        int nbComp = 0;
        for (int j = 0; j < d.getMots().size(); j++) {
            PaireEntierComparaison scorecomp = UtilitairePaireChaineEntier.entierPourChaineTrie(lexique, d.getMots().get(j));
            score += scorecomp.getEntier();
            nbComp += scorecomp.getNbComp();
        }
        return new PaireEntierComparaison(score, nbComp);
    }

    @Override
    public String toString() {
        // Method to print the category and its values
        return "Categorie{" + "nom='" + nom + '\'' + ", lexique=" + lexique + '}';
    }
}
