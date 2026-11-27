// Pemilik: Kak Qim
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        String pathGambar = (args.length > 0) ? args[0] : "input/gambar.png";
        int baris = (args.length > 1) ? Integer.parseInt(args[1]) : 8;
        int kolom = (args.length > 2) ? Integer.parseInt(args[2]) : 8;
        int jumlahThread = (args.length > 3) ? Integer.parseInt(args[3]) : 4;
        int jedaSync = (args.length > 4) ? Integer.parseInt(args[4]) : 500;
        long seed = 42L;

        System.out.println("=== Demo Race Condition ===");
        RaceDemo raceDemo = new RaceDemo(jumlahThread, 100000);
        raceDemo.jalankanTanpaLock();
        raceDemo.jalankanDenganLock();

        File fileGambar = new File(pathGambar);
        if (!fileGambar.exists()) {
            System.out.println("File gambar tidak ditemukan: " + pathGambar);
            System.out.println("Penggunaan: java -cp src Main <pathGambar> [baris] [kolom] [jumlahThread] [jedaSync]");
            return;
        }

        BufferedImage gambar;
        try {
            gambar = ImageIO.read(fileGambar);
        } catch (IOException e) {
            System.err.println("Gagal membaca gambar: " + e.getMessage());
            return;
        }
        if (gambar == null) {
            System.err.println("Format gambar tidak didukung: " + pathGambar);
            return;
        }

        System.out.println("\n=== Tahap 1: Memecah Tile & Membangun Tabel Kecocokan ===");
        TileSplitter splitter = new TileSplitter(gambar, baris, kolom);
        PuzzleData data = splitter.split();
        int[] papanAcak = splitter.shuffle(seed);
        if (data == null || papanAcak == null) {
            System.out.println("TileSplitter belum diimplementasikan (split/shuffle mengembalikan null).");
            return;
        }

        EdgeScorer scorer = new EdgeScorer(data);
        ParallelMatrixBuilder matrixBuilder = new ParallelMatrixBuilder(data, scorer);
        CompatibilityTable tabel = matrixBuilder.buildParallel(jumlahThread);
        if (tabel == null) {
            System.out.println("ParallelMatrixBuilder belum mengembalikan CompatibilityTable.");
            return;
        }

        System.out.println("\n=== Tahap 2: Pencarian Susunan (Simulated Annealing Paralel) ===");
        QualityChecker checker = new QualityChecker();
        Visualizer visualizer = new Visualizer();
        visualizer.saveImage(papanAcak, data, "output/acak.png");

        Solver solver = new Solver(tabel, baris, kolom);
        ParallelSolver parallelSolver = new ParallelSolver(solver, data.getJumlahTile(), seed);

        System.out.printf("Awal       -> skor: %d, benar: %.2f%%%n",
                solver.hitungSkor(papanAcak), checker.persenTileBenar(papanAcak));

        int[] hasilTanpaBerbagi = parallelSolver.runMultiStart(jumlahThread, jedaSync, false);
        System.out.printf("Tanpa bagi -> skor: %d, benar: %.2f%%%n",
                solver.hitungSkor(hasilTanpaBerbagi), checker.persenTileBenar(hasilTanpaBerbagi));
        visualizer.saveImage(hasilTanpaBerbagi, data, "output/hasil_tanpa_berbagi.png");

        ThreadMonitor monitor = new ThreadMonitor(jumlahThread);
        solver.pasangPemantau(monitor, null);
        parallelSolver.pasangPemantau(monitor);

        int[] hasilBerbagi = parallelSolver.runMultiStart(jumlahThread, jedaSync, true);
        System.out.printf("Berbagi    -> skor: %d, benar: %.2f%%%n",
                solver.hitungSkor(hasilBerbagi), checker.persenTileBenar(hasilBerbagi));
        visualizer.saveImage(hasilBerbagi, data, "output/hasil.png");

        monitor.cetakRingkasan();
        monitor.simpanCsv("thread_log.csv");
        monitor.simpanTimeline("timeline.csv");

        System.out.println("\n=== Benchmark ===");
        new Benchmark(gambar).measureSpeedup();
    }
}
