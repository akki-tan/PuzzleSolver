// Pemilik: Kak Qim
public class ParallelSolver {
    private Solver solver;
    private int jumlahTile;
    private long seed;
    private ThreadMonitor monitor;

    public ParallelSolver(Solver solver, int jumlahTile, long seed) {
        this.solver = solver;
        this.jumlahTile = jumlahTile;
        this.seed = seed;
    }

    public void pasangPemantau(ThreadMonitor monitor) {
        this.monitor = monitor;
    }

    public int[] runMultiStart(int jumlahThread, int jedaSync, boolean pakaiBerbagi) {
        if (jumlahThread <= 0) {
            return null;
        }

        SharedBest.reset();

        int[][] hasilThread = new int[jumlahThread][];
        AnnealThread[] threads = new AnnealThread[jumlahThread];

        for (int t = 0; t < jumlahThread; t++) {
            int[] papanAwal = acakPapan(seed + t);
            threads[t] = new AnnealThread(t, papanAwal, jedaSync, pakaiBerbagi, hasilThread);
        }

        for (int t = 0; t < jumlahThread; t++) {
            threads[t].start();
        }

        for (int t = 0; t < jumlahThread; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int[] papanTerbaik = null;
        int skorTerbaik = Integer.MAX_VALUE;
        for (int t = 0; t < jumlahThread; t++) {
            if (hasilThread[t] == null) continue;
            int skor = solver.hitungSkor(hasilThread[t]);
            if (papanTerbaik == null || skor < skorTerbaik) {
                skorTerbaik = skor;
                papanTerbaik = hasilThread[t];
            }
        }
        return papanTerbaik;
    }

    private int[] acakPapan(long seedThread) {
        int[] papan = new int[jumlahTile];
        for (int i = 0; i < jumlahTile; i++) {
            papan[i] = i;
        }
        java.util.Random rand = new java.util.Random(seedThread);
        for (int i = jumlahTile - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = papan[i];
            papan[i] = papan[j];
            papan[j] = tmp;
        }
        return papan;
    }

    class AnnealThread extends Thread {
        int idThread;
        int[] papanAwal;
        int[] hasil;
        int jedaSync;
        boolean pakaiBerbagi;
        int[][] hasilThread;

        AnnealThread(int idThread, int[] papanAwal, int jedaSync, boolean pakaiBerbagi, int[][] hasilThread) {
            this.idThread = idThread;
            this.papanAwal = papanAwal;
            this.jedaSync = jedaSync;
            this.pakaiBerbagi = pakaiBerbagi;
            this.hasilThread = hasilThread;
        }

        @Override
        public void run() {
            if (monitor != null) monitor.mulai(idThread);
            hasil = solver.anneal(papanAwal, seed, pakaiBerbagi, jedaSync, idThread);
            hasilThread[idThread] = hasil;
            if (monitor != null) monitor.selesai(idThread);
        }
    }
}
