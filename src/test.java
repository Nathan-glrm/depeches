import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedWriter br = new BufferedWriter(new FileWriter("./perftest"));
        for (int i = 0; i < 1000 - 1; i++) {
            br.write("qkllqsflkqsf" + ":" + "2");
            br.newLine();
        }
        // Empêche le rajout d'un saut de ligne dans le fichier lexique (ce qui perturberait le système de lecture)
        br.close();
        System.out.println(System.currentTimeMillis() - start);
    }
}
