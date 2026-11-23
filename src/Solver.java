// Pemilik: Kak Qim
public class Solver {
    private int[][] kanan, bawah;
    private int baris, kolom;
    private ThreadMonitor monitor;
    private LiveViewer viewer;

    public Solver(CompatibilityTable tabel, int baris, int kolom) {
        this.kanan = tabel.getKanan();
        this.bawah = tabel.getBawah();
        this.baris = baris;
        this.kolom = kolom;
    }

    public void pasangPemantau(ThreadMonitor monitor, LiveViewer viewer) {
        this.monitor = monitor;
        this.viewer = viewer;
    }

    public int[] anneal(int[] papanAwal, long seed, boolean pakaiBerbagi, int jedaSync, int idThread) {
        // TODO: variabel papan, skor, skorBaru, skorTerbaik, papanTerbaik, suhu, faktorTurun, jumlahLangkah
        // TODO: kalau pakaiBerbagi, tiap jedaSync langkah panggil SharedBest.sync(skor, papan)
        // TODO: tiap beberapa langkah, kalau monitor/viewer tidak null: monitor.catat(...), viewer.update(...)
        return papanAwal;
    }

    public int hitungSkor(int[] papan) {
        // TODO: jumlah kanan[..][..] pasangan kiri-kanan dan bawah[..][..] pasangan atas-bawah
        return 0;
    }

    public void tukar(int[] papan, int posisiA, int posisiB) {
        // TODO: tukar isi posisiA dan posisiB
    }
}
