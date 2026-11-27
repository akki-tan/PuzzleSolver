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

        for (int t = 0; t < jumlahThread; t++) workers[t].start();
        try {
            for (int t = 0; t < jumlahThread; t++) workers[t].join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
            int[][] kanan = tabel.getKanan();
            int[][] bawah = tabel.getBawah();
            int awal = idThread * jumlahTile / jumlahThread;
            int akhir = (idThread + 1) * jumlahTile / jumlahThread;

            for (int tileI = awal; tileI < akhir; tileI++) {
                for (int tileJ = 0; tileJ < jumlahTile; tileJ++) {
                    if (tileI == tileJ) continue;
                    kanan[tileI][tileJ] = scorer.edgeScore(tileI, tileJ, true);
                    bawah[tileI][tileJ] = scorer.edgeScore(tileI, tileJ, false);
                }
            }
        }
    }
}