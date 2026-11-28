// Pemilik: Kak Qim
public class ParallelSolver {
    private Solver solver;
    private int jumlahTile;
    private long seed;
    private ThreadMonitor monitor;
    private GreedyBuilder greedy;

    public ParallelSolver(Solver solver, int jumlahTile, long seed) {
        this.solver = solver;
        this.jumlahTile = jumlahTile;
        this.seed = seed;
    }

    public void pasangPemantau(ThreadMonitor monitor) {
        this.monitor = monitor;
    }

    public void setGreedy(GreedyBuilder greedy) {
        this.greedy = greedy;
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

        // mode berlapis: tiap thread mengerjakan lapisan demi lapisan, dan di akhir tiap lapisan semua thread bertemu
    public int[] runBerlapis(int jumlahThread, boolean pakaiBerbagi, int langkahPerPosisi) {
        if (jumlahThread <= 0 || greedy == null) {
            return null;
        }

        int[][] papanThread = new int[jumlahThread][];
        int[] lapisSekarang = {0};
        java.util.concurrent.CyclicBarrier gerbang = new java.util.concurrent.CyclicBarrier(jumlahThread, () -> {
            lapisSekarang[0]++;
            if (!pakaiBerbagi) return;
            int terbaik = 0;
            long biayaTerbaik = Long.MAX_VALUE;
            for (int t = 0; t < jumlahThread; t++) {
                long biaya = solver.biayaDikenal(papanThread[t], lapisSekarang[0]);
                if (biaya < biayaTerbaik) {
                    biayaTerbaik = biaya;
                    terbaik = t;
                }
            }
            for (int t = 0; t < jumlahThread; t++) {
                papanThread[t] = papanThread[terbaik].clone();
            }
        });

        Thread[] threads = new Thread[jumlahThread];
        for (int t = 0; t < jumlahThread; t++) {
            int idThread = t;
            threads[t] = new Thread(() -> kerjakanBerlapis(idThread, papanThread, gerbang, langkahPerPosisi));
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
            if (papanThread[t] == null) continue;
            int skor = solver.hitungSkor(papanThread[t]);
            if (papanTerbaik == null || skor < skorTerbaik) {
                skorTerbaik = skor;
                papanTerbaik = papanThread[t];
            }
        }
        return papanTerbaik;
    }

    private void kerjakanBerlapis(int idThread, int[][] papanThread,
                                  java.util.concurrent.CyclicBarrier gerbang, int langkahPerPosisi) {
        if (monitor != null) monitor.mulai(idThread);
        java.util.Random rand = new java.util.Random(seed + idThread);
        int[] papan = acakPapan(seed + idThread);
        papanThread[idThread] = papan;

        try {
            for (int lapisAktif = 1; lapisAktif <= solver.lapisTerdalam(); lapisAktif++) {
                greedy.susunLapisan(papan, lapisAktif, idThread);
                solver.annealLapisan(papan, lapisAktif, rand, langkahPerPosisi, 300, 50);
                if (monitor != null) {
                    monitor.catat(idThread, lapisAktif, (int) solver.biayaDikenal(papan, lapisAktif));
                }
                papanThread[idThread] = papan;
                gerbang.await();
                papan = papanThread[idThread].clone();
            }
        } catch (InterruptedException | java.util.concurrent.BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }

        papanThread[idThread] = papan;
        if (monitor != null) monitor.selesai(idThread);
    }

    private int[] acakPapan(long seedThread) {
        int[] papan = new int[jumlahTile];
        for (int i = 0; i < jumlahTile; i++) {
            papan[i] = i;
        }
        java.util.Random rand = new java.util.Random(seedThread);
        int[] bebas = solver.getPosisiBebas();
        for (int i = bebas.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = papan[bebas[i]];
            papan[bebas[i]] = papan[bebas[j]];
            papan[bebas[j]] = tmp;
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
            if (greedy != null) {
                papanAwal = greedy.susun(papanAwal, idThread);
                if (monitor != null) monitor.catat(idThread, 0, solver.hitungSkor(papanAwal));
            }
            hasil = solver.anneal(papanAwal, seed, pakaiBerbagi, jedaSync, idThread);
            hasilThread[idThread] = hasil;
            if (monitor != null) monitor.selesai(idThread);
        }
    }
}
