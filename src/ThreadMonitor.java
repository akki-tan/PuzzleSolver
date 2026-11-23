// Pemilik: Adi
import java.util.ArrayList;
import java.util.List;

public class ThreadMonitor {
    private long[] waktuMulai, waktuSelesai;
    private List<List<String>> logThread;

    public ThreadMonitor(int jumlahThread) {
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
        // TODO: simpan baris "idThread,waktu,langkah,skor" ke logThread.get(idThread)
    }

    public void simpanCsv(String namaFile) {
        // TODO: tulis semua log ke output/namaFile untuk grafik timeline dan grafik skor
    }

    public void cetakRingkasan() {
        // TODO: cetak durasi tiap thread (waktuSelesai - waktuMulai)
    }
}
