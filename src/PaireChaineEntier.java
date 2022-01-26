public class PaireChaineEntier {
    private String chaine;
    private int entier;

    public PaireChaineEntier(String chaine, int entier){
        this.chaine = chaine;
        this.entier = entier;
    }

    public String getChaine() {
        return chaine;
    }

    public int getEntier() {
        return entier;
    }

    public void setEntier(int entier) {
        this.entier = entier;
    }

    public void increment(){
        this.entier++;
    }
    public void decrement(){
        this.entier = this.entier - 3;
    }

    @Override
    public String toString() {
        return chaine + ':' + entier;
    }

    public int compareTo(PaireChaineEntier PaireChaine) {
        if (PaireChaine.getEntier() > entier) {
            return 1;
        } else {
            return -1;
        }
    }
}
