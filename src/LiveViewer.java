// Pemilik: Adi
import javax.swing.JFrame;

public class LiveViewer {
    private PuzzleData data;
    private JFrame jendela;
    private int[][] papanSalinan;
    private int[] skorThread;

    public LiveViewer(PuzzleData data, int jumlahThread) {
        this.data = data;
        papanSalinan = new int[jumlahThread][];
        skorThread = new int[jumlahThread];
    }

    public synchronized void update(int idThread, int[] papan, int skor) {
        papanSalinan[idThread] = papan.clone();
        skorThread[idThread] = skor;
    }

    public synchronized int[] ambilPapan(int idThread) {
        return papanSalinan[idThread];
    }

    public void tampilkan() {
        // TODO: buat JFrame berisi satu panel per thread dan label skorTerbaik
    }

    public void gambarUlang() {
        // TODO: dipanggil Timer Swing tiap ~200 ms, gambar tile sesuai ambilPapan(idThread)
    }
}
