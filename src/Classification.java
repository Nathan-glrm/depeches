import java.io.*;
import java.util.ArrayList;

public class Classification {

    //On utilise l'objet Buffer de java qui à de meilleurs performance que FileInputStream (entre 2 à 3 fois plus vite)
    private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
        //creation d'un tableau de dépêches
        ArrayList<Depeche> depeches = new ArrayList<>();
        try {
            // lecture du fichier d'entrée
            Reader file = new FileReader(nomFichier);
            BufferedReader br = new BufferedReader(file, 300000);

            String ligne;
            while ((ligne = br.readLine()) != null) {
                String id = ligne.substring(3);
                ligne = br.readLine();
                String date = ligne.substring(3);
                ligne = br.readLine();
                String categorie = ligne.substring(3);
                ligne = br.readLine();
                StringBuilder lignes = new StringBuilder(ligne.substring(3));
                while ((ligne = br.readLine()) != null && !ligne.equals("")) {
                    lignes.append('\n').append(ligne);
                }
                Depeche uneDepeche = new Depeche(id, date, categorie, lignes.toString());
                depeches.add(uneDepeche);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return depeches;
    }

    // Pour une catégorie donnée, renvoie tous les mots associés (tous les mots des dépêches d'une catégorie)
    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) throws CategorieNotExist {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        categorie = categorie.toUpperCase();

        //Les depeches étant regroupées par catégorie, on recherche l'index de la première depeche de la catégorie recherchée.
        int i = 0;
        while (i < depeches.size() && depeches.get(i).getCategorie().compareTo(categorie) != 0) {
            i++;
        }
        if (i == depeches.size()) {
            throw new CategorieNotExist("Catégorie n'existe pas dans depeches");
        }

        // i correspond à l'index du premier élément de depeches correspondant à la catégorie

        while (i < depeches.size() && depeches.get(i).getCategorie().compareTo(categorie) == 0) {
            for (int j = 0; j < depeches.get(i).getMots().size(); j++) {
                int index = UtilitairePaireChaineEntier.indexPourChaineTrie(resultat, depeches.get(i).getMots().get(j));
                if (index > -1) {
                    resultat.add(index, new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                }
            }
            i++;
        }
        return resultat;

    }

    // Calcul des scores (Apparition des mots dans leur catégorie (incrémentation du score) et dans les autres (decrementation du score)
    public static int calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        int i = 0;
        int nbComp = 0;
        categorie = categorie.toUpperCase();
        while (i < depeches.size()) {
            int j = 0;
            while (j < depeches.get(i).getMots().size()) {
                PaireEntierComparaison recherche = UtilitairePaireChaineEntier.recherchePourChaineTrie(dictionnaire, depeches.get(i).getMots().get(j));
                int index = recherche.getEntier();
                nbComp += recherche.getNbComp();
                if (index < dictionnaire.size() && index >= 0) {
                    if (depeches.get(i).getCategorie().compareTo(categorie) == 0) {
                        dictionnaire.get(index).increment();
                    } else if (depeches.get(i).getCategorie().compareTo(categorie) != 0) {
                        dictionnaire.get(index).decrement();
                    }
                    nbComp++;
                }

                j++;
            }
            i++;
        }
        return nbComp;
    }

    // Fonction de suppression des entiers donc le score est trop négatif (ici -3 offre les meilleurs résultat)
    public static ArrayList<PaireChaineEntier> removeNegative(ArrayList<PaireChaineEntier> Scores) {
        int inf = 0;
        int sup = Scores.size();
        int m;
        while (inf < sup) {
            m = (inf + sup) / 2;
            if (Scores.get(m).getEntier() <= -3) {
                sup = m;
            } else {
                inf = m + 1;
            }
        }
        return new ArrayList<>(Scores.subList(0, inf));
    }

    // poidsPourScore de base, avec choix de la ponderation à valeur fixe
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

    // poidsPourScore, utilisant la valeur maximale de PaireChaine entier pour déterminer la pondération
    public static void poidsPourScore(ArrayList<PaireChaineEntier> dico) {
        int max = dico.get(0).getEntier();
        int premierTiers = max / 7;
        int deuxiemeTiers = max / 2;
        int i = 0;
        //dico est trié par entier
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
    }

    //Generation des dictionnaire
    public static void generationLexique(ArrayList<Depeche> depeches, Categorie categorie, String nomFichier) {
        System.out.println("*** Creation du lexique pour la catégorie " + categorie.getNom().toUpperCase());
        try {
            //Creation du dico, trié par ordre alphabétique
            final long dicoStartTime = System.currentTimeMillis();
            ArrayList<PaireChaineEntier> dico = initDico(depeches, categorie.getNom()); //3ms
            System.out.println("\t\t\u001B[37mInitialisation du dico (" + dico.size() + " mots) : " + (System.currentTimeMillis() - dicoStartTime + "ms"));


            //Attribution d'un score en fonction de la redondance du mot
            final long scoreStartTime = System.currentTimeMillis();
            int comp = calculScores(depeches, categorie.getNom(), dico); //7ms
            System.out.println("\t\tCalcul du score des mots (" + comp + " comparaisons) : " + (System.currentTimeMillis() - scoreStartTime + "ms"));


            //Tri par score
            final long trinumStartTime = System.currentTimeMillis();
            comp = Utilitaire.triFusionInt(dico, 0, dico.size() - 1); //0ms
            System.out.println("\t\tTri par score (" + comp + " comparaisons) : " + (System.currentTimeMillis() - trinumStartTime + "ms"));


            //Suppression des mots donc le score est inférieur à x, qui ne nous intéresse pas
            final long poidsStartTime = System.currentTimeMillis();

            dico = removeNegative(dico); //0ms
            //Attribution en fonction du score, un poids entre 1 et 3
            poidsPourScore(dico); //0ms
            System.out.println("\t\tSuppression des mots non pertinents & pondération: " + (System.currentTimeMillis() - poidsStartTime + "ms"));


            //On retri par ordre alphabétique pour des besoins futur
            final long triStringStartTime = System.currentTimeMillis();
            comp = Utilitaire.triFusionString(dico, 0, dico.size() - 1);
            System.out.println("\t\tTri par mots (" + comp + " comparaisons) : " + (System.currentTimeMillis() - triStringStartTime + "ms"));


            categorie.setLexique(dico); //On envoie directement le lexique à la Catégorie pour éviter une lecture inutile des fichiers générés


            //Generation des fichiers lexiques (2-4ms)
            final long ecritureStartTime = System.currentTimeMillis();

            Writer file = new FileWriter("./autoLexiques/" + nomFichier);
            BufferedWriter br = new BufferedWriter(file, 16384);
            for (int i = 0; i < dico.size() - 1; i++) {
                br.write(dico.get(i).getChaine() + ':' + dico.get(i).getEntier());
                br.newLine();
            }
            // Empêche le rajout d'un saut de ligne dans le fichier lexique (ce qui perturberait le système de lecture)
            br.write(dico.get(dico.size() - 1).getChaine() + ':' + dico.get(dico.size() - 1).getEntier());
            br.close();
            System.out.println("\t\tEcriture du lexique de " + dico.size() + " mots en : " + (System.currentTimeMillis() - ecritureStartTime + "ms\u001B[0m"));
            System.out.println("\tTemps total de création du lexique de " + categorie.getNom() + ": " + (System.currentTimeMillis() - dicoStartTime + "ms\u001B[0m\n"));


        } catch (CategorieNotExist e) {
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static int classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        int i = 0;
        int nbComp = 0;
        try {
            Writer file = new FileWriter(nomFichier);
            BufferedWriter br = new BufferedWriter(file, 16384);
            //Generation de deux ArrayList, un qui compte le nombre de depeches qui ont été assignés à la catégorie et un autre qui compte les assignations correctes
            ArrayList<PaireChaineEntier> depechePerCat = new ArrayList<>();
            ArrayList<PaireChaineEntier> correctGuess = new ArrayList<>();
            //Initialisation des ArraysList avec les catégories
            for (Categorie category : categories) {
                depechePerCat.add(new PaireChaineEntier(category.getNom(), 0));
                correctGuess.add(new PaireChaineEntier(category.getNom(), 0));
            }

            //Classement des depeches
            while (i < depeches.size()) {
                //Calculer le score de la dépeche pour chaque catégories
                ArrayList<PaireChaineEntier> listeDeScore = new ArrayList<>();
                for (Categorie category : categories) {
                    PaireEntierComparaison score = category.score(depeches.get(i));
                    listeDeScore.add(new PaireChaineEntier(category.getNom(), score.getEntier()));
                    nbComp += score.getNbComp();
                }

                //Recuperer la meilleur catégorie
                String cat = UtilitairePaireChaineEntier.chaineMax(listeDeScore);
                int index = UtilitairePaireChaineEntier.indicePourChaine(depechePerCat, cat);

                depechePerCat.get(index).setEntier(depechePerCat.get(index).getEntier() + 1);       //Ajoute dans le compteur d'assignation
                if (depeches.get(i).getCategorie().compareToIgnoreCase(cat) == 0) {
                    correctGuess.get(index).setEntier(correctGuess.get(index).getEntier() + 1);     //Ajoute dans le compteur d'assignation correcte
                }
                br.write(depeches.get(i).getId() + ':' + cat.toUpperCase());
                br.newLine();
                i++;
            }

            //Le calcul des pourcentage de reussite est exploitable uniquement lorsque chaque catégorie possèdent respectivement 100 depeches
            br.write("\n\n ------ RESULTATS ------ \n\n");
            System.out.println("\n ------ RÉSULTATS ------ \n");
            float moyenne = 0.0f;
            for (PaireChaineEntier guess : correctGuess) {
                br.write(guess.getChaine().toUpperCase() + ":" + guess.getEntier() + "%");
                br.newLine();
                System.out.printf("%-25s%5s%s", guess.getChaine().toUpperCase().concat(":"), guess.getEntier(), "%\n");
                moyenne += guess.getEntier();
            }
            //Caclculer le pourcentage de réussite
            br.write("MOYENNE: " + moyenne / categories.size() + "%");

            System.out.println("\nMOYENNE: " + moyenne / categories.size() + "%\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nbComp;
    }

    public static void main(String[] args) {
        //Chargement des dépêches en mémoire
        long startTime = System.currentTimeMillis();
        System.out.println("Chargement des dépêches");
        ArrayList<Depeche> depechesForLexique = lectureDepeches("./depeches.txt");
        ArrayList<Depeche> depeches = lectureDepeches("./test.txt");
        System.out.println("Lecture des depeches en " + (System.currentTimeMillis() - startTime) + "ms\n");

        long lexiquesTemps = System.currentTimeMillis();

        Categorie culture = new Categorie("culture");
        Categorie economie = new Categorie("economie");
        Categorie environnementsiences = new Categorie("environnement-sciences");
        Categorie politique = new Categorie("politique");
        Categorie sport = new Categorie("sports");

        System.out.println("Generation des lexiques à partir de depeches.txt");
        generationLexique(depechesForLexique, environnementsiences, "./environnement-sciences.txt");
        generationLexique(depechesForLexique, culture, "./culture.txt");
        generationLexique(depechesForLexique, economie, "./economie.txt");
        generationLexique(depechesForLexique, politique, "./politique.txt");
        generationLexique(depechesForLexique, sport, "./sports.txt");
        System.out.println("Generation des 5 lexiques en " + (System.currentTimeMillis() - lexiquesTemps) + "ms\n");


        // New ArrayList for categories
        ArrayList<Categorie> cat = new ArrayList<>();
        cat.add(culture);
        cat.add(economie);
        cat.add(environnementsiences);
        cat.add(politique);
        cat.add(sport);


        //Initialisation des lexiques écrit manuellement

        /*String lexiques = "./lexiques/";
        culture.initLexique(lexiques + "culture.txt");
        economie.initLexique(lexiques + "economie.txt");
        environnementsiences.initLexique(lexiques + "environnement-sciences.txt");
        politique.initLexique(lexiques + "politique.txt");
        sport.initLexique(lexiques + "sports.txt");
        */


        //Classement des depeches de test.txt
        long classementStartTime = System.currentTimeMillis();
        int comp = classementDepeches(depeches, cat, "./resultat.txt");
        System.out.println("Classement des depeches (" + comp +" comparaisons) en " + (System.currentTimeMillis() - classementStartTime) + "ms");

        long endTime = System.currentTimeMillis();
        System.out.println("Temps total d'execution du programme en : " + (endTime - startTime) + "ms");
    }
}

