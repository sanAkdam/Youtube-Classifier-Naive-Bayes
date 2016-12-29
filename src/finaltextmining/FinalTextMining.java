/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finaltextmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.id.IndonesianStemmer;

public class FinalTextMining {

    /**
     * @param args the command line arguments
     */
    static ArrayList data = new ArrayList();
    static String[] kalimat = null;
    static String[] result = null;
    private static String[] namaFitur;
    private static ArrayList[] isiFitur;

    static void prosesData() {

        try (BufferedReader br = new BufferedReader(new FileReader("trendingtopik.txt"))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                data.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        kalimat = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            kalimat[i] = data.get(i).toString().replaceAll("[!-?]", "");
            kalimat[i] = kalimat[i].toLowerCase();

            result = kalimat[i].split("\\s");
//           System.out.println(result.length);
//            return result[i];
            filter();
        }

    }
    static ArrayList stopword = new ArrayList();

    static void getStopword() {
        try (BufferedReader br = new BufferedReader(new FileReader("stopword.txt"))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                stopword.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void filter() {
//        getData();
        getStopword();
        int flag = 0;
        boolean tmp = false;

        int[] index = new int[result.length];
        for (int x = 0; x < result.length; x++) {
            for (int y = 0; y < stopword.size(); y++) {
                if (result[x].equalsIgnoreCase(String.valueOf(stopword.get(y)))) {
                    index[x] = x;
                    if (tmp == false) {
                        flag = index[x];
                        tmp = true;
                    }
//                    System.out.println(index[x]);
                }

            }
        }
        System.out.println("Flag :" + flag);

        for (int x = 0; x < result.length; x++) {
//            System.out.println("Pembanding : " + result[x]);
//            System.out.println("Stopword : " + result[index[x]]);
            if (x == 0) {

                if (flag == 0) {
                    if (!result[x].equalsIgnoreCase(result[index[flag]])) {
                        IndonesianStemmer stemmer = new IndonesianStemmer();
                        char[] chars = result[x].toCharArray();
                        int len = stemmer.stem(chars, chars.length, true);
                        String stem = new String(chars, 0, len);
                        System.out.println(stem);
//                    System.out.println("Stemming : " + result[x]);
                    }
                } else {

                    IndonesianStemmer stemmer = new IndonesianStemmer();
                    char[] chars = result[x].toCharArray();
                    int len = stemmer.stem(chars, chars.length, true);
                    String stem = new String(chars, 0, len);
                    System.out.println(stem);
//                    System.out.println("Stemming : " + result[x]);

                }

            }
            if (!result[x].equalsIgnoreCase(result[index[x]])) {
                IndonesianStemmer stemmer = new IndonesianStemmer();
                char[] chars = result[x].toCharArray();
                int len = stemmer.stem(chars, chars.length, true);
                String stem = new String(chars, 0, len);
                System.out.println(stem);
            }
        }
    }

//    public static void loadFitur() {
//        File dir = new File("FITUR");
//        namaFitur = new String[dir.listFiles().length];
//        isiFitur = new ArrayList[dir.listFiles().length];
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            File f = files[i];
//            System.out.println(f.getName().replace(".txt", ""));
//            namaFitur[i] = f.getName().replace(".txt", "");
//            isiFitur[i] = new ArrayList();
//            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
//                String sCurrentLine;
//                while ((sCurrentLine = br.readLine()) != null) {
////                    stopword.add(sCurrentLine);
//                    System.out.println(sCurrentLine);
//                    isiFitur[i].add(sCurrentLine);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println();
//        }
//    }

    public static void main(String[] args) {
//        loadDataTraining();
        BayesTextMining btm = new BayesTextMining();
        btm.start();
//        loadFitur();
    }

}
