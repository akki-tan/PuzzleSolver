// Pemilik: Adi
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ThreadMonitor {
    private long waktuNol;
    private long[] waktuMulai, waktuSelesai;
    private List<List<String>> logThread;

    public ThreadMonitor(int jumlahThread) {
        waktuNol = System.nanoTime();
        waktuMulai = new long[jumlahThread];
        waktuSelesai = new long[jumlahThread];
        logThread = new ArrayList<>();
        for (int t = 0; t < jumlahThread; t++) logThread.add(new ArrayList<>());
    }

    // tiap thread hanya menulis ke slot miliknya sendiri, jadi tanpa lock

    public void mulai(int idThread) {
        waktuMulai[idThread] = System.nanoTime();
    }

    public void selesai(int idThread) {
        waktuSelesai[idThread] = System.nanoTime();
    }

    public void catat(int idThread, int langkah, int skor) {
        double waktu = (System.nanoTime() - waktuNol) / 1000000.0;
        logThread.get(idThread).add(idThread + "," + waktu + "," + langkah + "," + skor);
    }

    public void simpanCsv(String namaFile) {
        new File("output").mkdirs();
        try (PrintWriter csv = new PrintWriter("output/" + namaFile)) {
            csv.println("idThread,waktuMs,langkah,skor");
            for (List<String> log : logThread) {
                for (String baris : log) csv.println(baris);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simpanTimeline(String namaFile) {
        new File("output").mkdirs();
        try (PrintWriter csv = new PrintWriter("output/" + namaFile)) {
            csv.println("idThread,mulaiMs,selesaiMs");
            for (int t = 0; t < waktuMulai.length; t++) {
                double mulai = (waktuMulai[t] - waktuNol) / 1000000.0;
                double selesai = (waktuSelesai[t] - waktuNol) / 1000000.0;
                csv.println(t + "," + mulai + "," + selesai);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cetakRingkasan() {
        for (int t = 0; t < waktuMulai.length; t++) {
            double durasi = (waktuSelesai[t] - waktuMulai[t]) / 1000000.0;
            System.out.println("Thread " + t + ": " + durasi + " ms, " + logThread.get(t).size() + " catatan");
        }
    }
}