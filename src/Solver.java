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
        java.util.Random rand = new java.util.Random(seed + idThread);
        int[] papan = papanAwal.clone();
        int n = papan.length;
        int skor = hitungSkor(papan);
        int skorTerbaik = skor;
        int[] papanTerbaik = papan.clone();

        if (n < 2) {
            return papanTerbaik;
        }

        int jumlahLangkah = Math.max(20000, n * 500);
        double suhu = 5000.0;
        double suhuAkhir = 0.5;
        double faktorTurun = Math.pow(suhuAkhir / suhu, 1.0 / jumlahLangkah);
        int jedaPantau = (jedaSync > 0) ? jedaSync : Math.max(100, jumlahLangkah / 50);

        for (int langkah = 0; langkah < jumlahLangkah; langkah++) {
            int posisiA = rand.nextInt(n);
            int posisiB = rand.nextInt(n - 1);
            if (posisiB >= posisiA) {
                posisiB++;
            }

            int selisih = hitungSelisihTukar(papan, posisiA, posisiB);
            boolean terima = (selisih <= 0) || (rand.nextDouble() < Math.exp(-selisih / suhu));

            if (terima) {
                tukar(papan, posisiA, posisiB);
                skor += selisih;
                if (skor < skorTerbaik) {
                    skorTerbaik = skor;
                    papanTerbaik = papan.clone();
                }
            }

            if (pakaiBerbagi && jedaSync > 0 && (langkah + 1) % jedaSync == 0) {
                int[] dariBersama = SharedBest.sync(skor, papan);
                papan = dariBersama.clone();
                skor = hitungSkor(papan);
                if (skor < skorTerbaik) {
                    skorTerbaik = skor;
                    papanTerbaik = papan.clone();
                }
            }

            if ((langkah + 1) % jedaPantau == 0) {
                if (monitor != null) {
                    monitor.catat(idThread, langkah + 1, skor);
                }
                if (viewer != null) {
                    viewer.update(idThread, papan, skor);
                }
            }

            suhu *= faktorTurun;
        }

        return papanTerbaik;
    }

    private int hitungSelisihTukar(int[] papan, int posisiA, int posisiB) {
        int sebelum = skorTetangga(papan, posisiA, posisiB);
        tukar(papan, posisiA, posisiB);
        int sesudah = skorTetangga(papan, posisiA, posisiB);
        tukar(papan, posisiA, posisiB);
        return sesudah - sebelum;
    }

    private int skorTetangga(int[] papan, int posisiA, int posisiB) {
        int total = 0;

        int barisA = posisiA / kolom;
        int kolomA = posisiA % kolom;
        int tileA = papan[posisiA];
        if (kolomA > 0) total += kanan[papan[posisiA - 1]][tileA];
        if (kolomA < kolom - 1) total += kanan[tileA][papan[posisiA + 1]];
        if (barisA > 0) total += bawah[papan[posisiA - kolom]][tileA];
        if (barisA < baris - 1) total += bawah[tileA][papan[posisiA + kolom]];

        int barisB = posisiB / kolom;
        int kolomB = posisiB % kolom;
        int tileB = papan[posisiB];
        // Hindari menghitung dua kali jika posisiA dan posisiB bertetangga langsung
        if (kolomB > 0 && posisiB - 1 != posisiA) total += kanan[papan[posisiB - 1]][tileB];
        if (kolomB < kolom - 1 && posisiB + 1 != posisiA) total += kanan[tileB][papan[posisiB + 1]];
        if (barisB > 0 && posisiB - kolom != posisiA) total += bawah[papan[posisiB - kolom]][tileB];
        if (barisB < baris - 1 && posisiB + kolom != posisiA) total += bawah[tileB][papan[posisiB + kolom]];

        return total;
    }

    public int hitungSkor(int[] papan) {
        int total = 0;
        for (int i = 0; i < papan.length; i++) {
            int row = i / kolom;
            int col = i % kolom;
            int id = papan[i];
            if (col < kolom - 1) {
                total += kanan[id][papan[i + 1]];
            }
            if (row < baris - 1) {
                total += bawah[id][papan[i + kolom]];
            }
        }
        return total;
    }

    public void tukar(int[] papan, int posisiA, int posisiB) {
        int tmp = papan[posisiA];
        papan[posisiA] = papan[posisiB];
        papan[posisiB] = tmp;
    }
}
