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
import java.util.Arrays;
import org.apache.lucene.analysis.id.IndonesianStemmer;

public class BayesTextMining {

    private double[] prior;
    private String[] kelasTraining;
    private String[][] teksTraining;
    private String[] stopWord;
    private String testing = "Real Madrid Menang Adu Penalti final Liga";

    public static void main(String[] args) {
        new BayesTextMining().start();
    }

    public void start() {
        loadStopWord(false);
        loadDataTraining(true);
        String[] hasilTokenize = tokenize(teksTraining, true);
        String[] hasilFilter = filter(hasilTokenize, stopWord, true);
        String[] hasilStem = stem(hasilFilter, true);
        String[] hasilSort = sort(hasilStem, true);
        String[] hasilHapusDuplikat = hapusDuplikat(hasilSort, true);
        String[] tokenizeTesting = tokenize(new String[][]{{testing}}, true);
        String[] filterTesting = filter(tokenizeTesting, stopWord, true);
        String[] stemTesting = stem(filterTesting, true);
        String[] sortTesting = sort(stemTesting, true);
        String[] hapusDuplikatTesting = hapusDuplikat(sortTesting, true);
        hitungPrior(true);
        int[][] jumlahTermTiapKelas = hitungJumlahTermTiapKelas(hapusDuplikatTesting, true);
        double[][] likelihood = hitungLikelihood(jumlahTermTiapKelas, hasilHapusDuplikat, hapusDuplikatTesting, true);
        double[] posterior = hitungPosterior(likelihood, prior, true);
        System.out.println("Kelas Hasil Klasifikasi : " + kelasTraining[getIndexMax(posterior)]);
    }

    public void loadStopWord(boolean print) {
        int counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File("stopword.txt")))) {
            while ((br.readLine()) != null) {
                counter++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopWord = new String[counter];
        counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File("stopword.txt")))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                stopWord[counter++] = sCurrentLine;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (print) {
            System.out.println("StopWord : ");
            for (int i = 0; i < stopWord.length; i++) {
                System.out.println((i + 1) + ". " + stopWord[i]);
            }
            System.out.println();
        }
    }

    public void loadDataTraining(boolean print) {
        File dir = new File("training");
        kelasTraining = new String[dir.listFiles().length];
        teksTraining = new String[dir.listFiles().length][];
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            kelasTraining[i] = f.getName().replace(".txt", "");
            if (print) {
                System.out.println("Kelas " + kelasTraining[i]);
            }
            int counter = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                while ((br.readLine()) != null) {
                    counter++;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            teksTraining[i] = new String[counter];
            counter = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    teksTraining[i][counter++] = sCurrentLine;
                    if (print) {
                        System.out.println(counter + ". " + teksTraining[i][counter - 1]);
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (print) {
                System.out.println();
            }
        }
    }

    public String[] tokenize(String[][] teks, boolean print) {
        int counter = 0;
        for (int i = 0; i < teks.length; i++) {
            for (int j = 0; j < teks[i].length; j++) {
                String[] term = teks[i][j].toLowerCase().replaceAll("[!-?]", "").split(" ");
                for (int k = 0; k < term.length; k++) {
                    if (!term[k].equals("")) {
                        counter++;
                    }
                }
            }
        }
        String[] hasilTokenize = new String[counter];
        counter = 0;
        if (print) {
            System.out.println("Hasil Tokenize : ");
        }
        for (int i = 0; i < teks.length; i++) {
            for (int j = 0; j < teks[i].length; j++) {
                String[] term = teks[i][j].toLowerCase().replaceAll("[!-?]", "").split(" ");
                for (int k = 0; k < term.length; k++) {
                    if (!term[k].equals("")) {
                        hasilTokenize[counter++] = term[k];
                        if (print) {
                            System.out.println(counter + ". " + hasilTokenize[counter - 1]);
                        }
                    }
                }
            }
        }
        if (print) {
            System.out.println();
        }
        return hasilTokenize;
    }

    public String[] stem(String[] term, boolean print) {
        IndonesianStemmer is = new IndonesianStemmer();
        String[] hasilStem = new String[term.length];
        if (print) {
            System.out.println("Hasil Stem : ");
        }
        for (int i = 0; i < term.length; i++) {
            char[] chars = term[i].toCharArray();
            int len = is.stem(chars, chars.length, true);
            hasilStem[i] = new String(chars, 0, len);
            if (print) {
                System.out.println((i + 1) + ". " + hasilStem[i]);
            }
        }
        if (print) {
            System.out.println();
        }
        return hasilStem;
    }

    public String[] filter(String[] term, String[] stopWord, boolean print) {
        int counter = 0;
        for (int i = 0; i < term.length; i++) {
            boolean ada = false;
            for (int j = 0; j < stopWord.length; j++) {
                if (term[i].equalsIgnoreCase(stopWord[j])) {
                    ada = true;
                    break;
                }
            }
            if (!ada) {
                counter++;
            }
        }

        String[] hasilFilter = new String[counter];
        counter = 0;
        if (print) {
            System.out.println("Hasil Filter : ");
        }
        for (int i = 0; i < term.length; i++) {
            boolean ada = false;
            for (int j = 0; j < stopWord.length; j++) {
                if (term[i].equalsIgnoreCase(stopWord[j])) {
                    ada = true;
                    break;
                }
            }
            if (!ada) {
                hasilFilter[counter++] = term[i];
                if (print) {
                    System.out.println(counter + ". " + hasilFilter[counter - 1]);
                }
            }
        }
        if (print) {
            System.out.println();
        }
        return hasilFilter;
    }

    public String[] sort(String[] term, boolean print) {
        String[] hasilSort = new String[term.length];
        System.arraycopy(term, 0, hasilSort, 0, term.length);
        for (int i = 0; i < hasilSort.length; i++) {
            for (int j = 0; j < i; j++) {
                if (hasilSort[i].compareTo(hasilSort[j]) < 0) {
                    String temp = hasilSort[i];
                    hasilSort[i] = hasilSort[j];
                    hasilSort[j] = temp;
                }
            }
        }
        if (print) {
            System.out.println("Hasil Sort : ");
            for (int i = 0; i < hasilSort.length; i++) {
                System.out.println((i + 1) + ". " + hasilSort[i]);
            }
            System.out.println();
        }
        return hasilSort;
    }

    public String[] hapusDuplikat(String[] term, boolean print) {
        String tmp = null;
        int counter = 0;
        for (int i = 0; i < term.length; i++) {
            if (tmp == null || !term[i].equalsIgnoreCase(tmp)) {
                counter++;
            }
            tmp = term[i];
        }
        String[] hasilHapusDuplikat = new String[counter];
        counter = 0;
        if (print) {
            System.out.println("Hasil Hapus Duplikat : ");
        }
        tmp = null;
        for (int i = 0; i < term.length; i++) {
            if (tmp == null || !term[i].equalsIgnoreCase(tmp)) {
                hasilHapusDuplikat[counter++] = term[i];
                if (print) {
                    System.out.println(counter + ". " + hasilHapusDuplikat[counter - 1]);
                }
            }
            tmp = term[i];
        }
        if (print) {
            System.out.println();
        }
        return hasilHapusDuplikat;
    }

    public int[][] hitungJumlahTermTiapKelas(String[] term, boolean print) {
        int[][] jumlahTermTiapKelas = new int[term.length][kelasTraining.length];
        for (int i = 0; i < teksTraining.length; i++) {
            String[] hasilTokenize = tokenize(new String[][]{teksTraining[i]}, false);
            String[] hasilFilter = filter(hasilTokenize, stopWord, false);
            String[] hasilStem = stem(hasilFilter, false);
            for (int j = 0; j < term.length; j++) {
                int counter = 0;
                for (int k = 0; k < hasilStem.length; k++) {
                    if (hasilStem[k].equalsIgnoreCase(term[j])) {
                        counter++;
                    }
                }
                jumlahTermTiapKelas[j][i] = counter;
            }
        }
        if (print) {
            System.out.println("Count(w,c) :");
            System.out.printf("%-24s", "");
            for (int i = 0; i < kelasTraining.length; i++) {
                System.out.printf("%-20s", kelasTraining[i]);
            }
            System.out.println();
            for (int i = 0; i < jumlahTermTiapKelas.length; i++) {
                System.out.printf("%02d. %-20s", (i + 1), term[i]);
                for (int j = 0; j < jumlahTermTiapKelas[i].length; j++) {
                    System.out.printf("%-20d", jumlahTermTiapKelas[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }
        return jumlahTermTiapKelas;
    }

    public double[][] hitungLikelihood(int[][] jumlahTermTiapKelas, String[] termTraining, String[] termTesting, boolean print) {
        double[][] hasilLikelihood = new double[jumlahTermTiapKelas.length][kelasTraining.length];
        for (int i = 0; i < hasilLikelihood.length; i++) {
            for (int j = 0; j < hasilLikelihood[i].length; j++) {
                hasilLikelihood[i][j] = (double) (jumlahTermTiapKelas[i][j]) / (teksTraining[j].length + termTraining.length);
//                System.out.println("Likehood :" + hasilLikelihood[i][j]);
            }
        }

        if (print) {
            System.out.println("P(w|c) :");
            System.out.printf("%-24s", "");
            for (int i = 0; i < kelasTraining.length; i++) {
                System.out.printf("%-20s", kelasTraining[i]);
            }
            System.out.println();
            for (int i = 0; i < hasilLikelihood.length; i++) {
                System.out.printf("%02d. %-20s", (i + 1), termTesting[i]);
                for (int j = 0; j < hasilLikelihood[i].length; j++) {
                    System.out.printf("%-20f", hasilLikelihood[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }
        return hasilLikelihood;
    }

    public double[] hitungPosterior(double[][] likelihood, double[] prior, boolean print) {
        double[] hasilPosterior = new double[kelasTraining.length];
        for (int i = 0; i < hasilPosterior.length; i++) {
            hasilPosterior[i] = prior[i];
        }
        double[] hasil = new double[hasilPosterior.length];
        for (int i = 0; i < likelihood.length; i++) {
            for (int j = 0; j < likelihood[i].length; j++) {
                hasil[j] += (hasilPosterior[j] * likelihood[i][j]);
            }
        }
        if (print) {
            System.out.println("Hasil Posterior : ");
            for (int i = 0; i < hasilPosterior.length; i++) {
                System.out.printf("%d. %s : %f", (i + 1), kelasTraining[i], hasil[i]);
            }
            System.out.println();
        }
        return hasil;
    }

    public int getIndexMax(double[] posterior) {        
        int index = 0;
        double max = posterior[0];
        for (int i = 1; i < posterior.length; i++) {
            if (max < posterior[i]) {
                index = i;
                max = posterior[i];
            }
        }
        return index;
    }

    public void hitungPrior(boolean print) {
        prior = new double[kelasTraining.length];
        double totalTeks = 0;
        for (String[] teks : teksTraining) {
            totalTeks += teks.length;
        }
        for (int i = 0; i < prior.length; i++) {
            prior[i] = teksTraining[i].length / totalTeks;
        }

        if (print) {
            for (int i = 0; i < prior.length; i++) {
                System.out.println("Prior " + kelasTraining[i] + " : " + prior[i]);
            }
        }
    }

}
