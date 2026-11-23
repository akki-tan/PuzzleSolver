// Pemilik: Adi
public class ParallelMatrixBuilder {
    private PuzzleData data;
    private EdgeScorer scorer;
    private CompatibilityTable tabel;
    private int jumlahThread;

    public ParallelMatrixBuilder(PuzzleData data, EdgeScorer scorer) {
        this.data = data;
        this.scorer = scorer;
    }

    public CompatibilityTable buildParallel(int jumlahThread) {
        this.jumlahThread = jumlahThread;
        tabel = new CompatibilityTable(data.getJumlahTile());
        Thread[] workers = new Thread[jumlahThread];
        for (int t = 0; t < jumlahThread; t++) workers[t] = new BarisThread(t);
        // TODO: start() semua, lalu join() semua (seperti jalankanThread di MatrixPar)
        return tabel;
    }

    class BarisThread extends Thread {
        int idThread;

        BarisThread(int idThread) {
            this.idThread = idThread;
        }

        @Override
        public void run() {
            int jumlahTile = data.getJumlahTile();
            int awal = idThread * jumlahTile / jumlahThread;
            int akhir = (idThread + 1) * jumlahTile / jumlahThread;
            // TODO: untuk tileI dari awal sampai akhir-1, isi tabel.getKanan() dan tabel.getBawah()
        }
    }
}
