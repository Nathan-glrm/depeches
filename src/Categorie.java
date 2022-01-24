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
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] paireChaineEntier = line.split(":");
                String chaine = paireChaineEntier[0];
                int entier = Integer.parseInt(paireChaineEntier[1]);
                PaireChaineEntier unePaire = new PaireChaineEntier(chaine, entier);
                Paires.add(unePaire);
            }
            scanner.close();
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
                    listeDeScore.add(new PaireChaineEntier(categories.get(j).nom, categories.get(j).score(depeches.get(i))));
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
            file.write("MOYENNE: " + moyenne/categories.size() + "%");
            //Caclculer le pourcentage de réussite
            System.out.println(depechePerCat);
            System.out.println(correctGuess);
            file.close();
            System.out.println("votre saisie a été écrite avec succès dans fichier-sortie.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //calcul du score d'une dépêche pour la catégorie
    public int score(Depeche d) {
        ArrayList<String> mots = d.getMots();
        int i = 0;
        int score = 0;
        while (i < lexique.size()) {
            if (mots.contains(lexique.get(i).getChaine().toLowerCase())) {
                int y = 0;
                while (y < mots.size()){
                    if (mots.get(y).compareTo(lexique.get(i).getChaine().toLowerCase()) == 0){
                        score += lexique.get(i).getEntier();
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
        return "Categorie{" + "nom='" + nom + '\'' + ", lexique=" + lexique + '}';
    }
}
