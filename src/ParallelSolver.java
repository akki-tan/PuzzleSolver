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
        // TODO: hasilThread[jumlahThread], tiap thread nulis ke slotnya sendiri (seed + idThread)
        // TODO: start() semua, join() semua
        // TODO: pilih papan dengan hitungSkor terkecil, berlaku untuk mode berbagi maupun tidak
        return null;
    }

    class AnnealThread extends Thread {
        int idThread;
        int[] papanAwal;
        int[] hasil;

        AnnealThread(int idThread, int[] papanAwal) {
            this.idThread = idThread;
            this.papanAwal = papanAwal;
        }

        @Override
        public void run() {
            // TODO: monitor.mulai, solver.anneal(...), simpan ke hasil, monitor.selesai (cek monitor null)
        }
    }
}
