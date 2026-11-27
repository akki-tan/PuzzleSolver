// Pemilik: Adi
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Benchmark {
    private BufferedImage gambar;
    private int[] daftarGrid = {8, 16, 32};
    private int[] daftarThread = {2, 4, 8};
    private int jumlahUlang = 3;
    private int jedaSync = 1000;

    private long waktuSerial, waktuParalel;
    private double speedup;

    private Solver solver;
    private int jumlahTile;
    private QualityChecker pemeriksa = new QualityChecker();

    public Benchmark(BufferedImage gambar) {
        this.gambar = gambar;
    }

    public void measureSpeedup() {
        new File("output").mkdirs();
        try (PrintWriter csvTahap1 = new PrintWriter("output/benchmark_tahap1.csv");
             PrintWriter csvTahap2 = new PrintWriter("output/benchmark_tahap2.csv")) {
            csvTahap1.println("grid,jumlahTile,jumlahThread,waktuSerialMs,waktuParalelMs,speedup");
            csvTahap2.println("grid,mode,jumlahThread,waktuMs,skor,persenTileBenar");
            for (int grid : daftarGrid) {
                ukurTahap1(grid, csvTahap1);
                ukurTahap2(grid, csvTahap2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ukurTahap1(int grid, PrintWriter csv) {
        PuzzleData data = new TileSplitter(gambar, grid, grid).split();
        EdgeScorer scorer = new EdgeScorer(data);

        new SerialMatrixBuilder(data, scorer).build();
        long total = 0;
        for (int u = 0; u < jumlahUlang; u++) {
            long mulai = System.nanoTime();
            new SerialMatrixBuilder(data, scorer).build();
            total += System.nanoTime() - mulai;
        }
        waktuSerial = total / jumlahUlang;

        for (int jumlahThread : daftarThread) {
            new ParallelMatrixBuilder(data, scorer).buildParallel(jumlahThread);
            total = 0;
            for (int u = 0; u < jumlahUlang; u++) {
                long mulai = System.nanoTime();
                new ParallelMatrixBuilder(data, scorer).buildParallel(jumlahThread);
                total += System.nanoTime() - mulai;
            }
            waktuParalel = total / jumlahUlang;
            speedup = (double) waktuSerial / waktuParalel;

            System.out.println("Tahap 1 | grid " + grid + " | " + jumlahThread + " thread | serial "
                    + ms(waktuSerial) + " ms | paralel " + ms(waktuParalel) + " ms | speedup " + speedup);
            csv.println(grid + "," + data.getJumlahTile() + "," + jumlahThread + ","
                    + ms(waktuSerial) + "," + ms(waktuParalel) + "," + speedup);
        }
    }

    private void ukurTahap2(int grid, PrintWriter csv) {
        PuzzleData data = new TileSplitter(gambar, grid, grid).split();
        EdgeScorer scorer = new EdgeScorer(data);
        int jumlahCore = Runtime.getRuntime().availableProcessors();
        CompatibilityTable tabel = new ParallelMatrixBuilder(data, scorer).buildParallel(jumlahCore);

        solver = new Solver(tabel, grid, grid);
        jumlahTile = data.getJumlahTile();

        jalankanMode(grid, "serial", 1, false, csv);
        for (int jumlahThread : daftarThread) {
            jalankanMode(grid, "tanpa berbagi", jumlahThread, false, csv);
            jalankanMode(grid, "dengan berbagi", jumlahThread, true, csv);
        }
    }

    private void jalankanMode(int grid, String namaMode, int jumlahThread, boolean pakaiBerbagi, PrintWriter csv) {
        long totalWaktu = 0;
        long totalSkor = 0;
        double totalPersen = 0;

        for (int u = 0; u < jumlahUlang; u++) {
            SharedBest.reset();
            ParallelSolver pencari = new ParallelSolver(solver, jumlahTile, 100 + u);
            long mulai = System.nanoTime();
            int[] hasil = pencari.runMultiStart(jumlahThread, jedaSync, pakaiBerbagi);
            totalWaktu += System.nanoTime() - mulai;
            totalSkor += solver.hitungSkor(hasil);
            totalPersen += pemeriksa.persenTileBenar(hasil);
        }

        double waktuRata = ms(totalWaktu / jumlahUlang);
        double skorRata = (double) totalSkor / jumlahUlang;
        double persenRata = totalPersen / jumlahUlang;

        System.out.println("Tahap 2 | grid " + grid + " | " + namaMode + " | " + jumlahThread + " thread | "
                + waktuRata + " ms | skor " + skorRata + " | benar " + persenRata + "%");
        csv.println(grid + "," + namaMode + "," + jumlahThread + "," + waktuRata + "," + skorRata + "," + persenRata);
    }

    private double ms(long nano) {
        return nano / 1000000.0;
    }
}