// Pemilik: Kak Qim
public class Solver {
    private int[][] kanan, bawah;
    private int baris, kolom;
    private ThreadMonitor monitor;
    private LiveViewer viewer;
    private boolean tepiTetap = false;
    private int langkahPerTile = 50000;
    private double suhuAwal = 5000.0;

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

    public void setTepiTetap(boolean tepiTetap) {
        this.tepiTetap = tepiTetap;
    }

    public void setLangkahPerTile(int langkahPerTile) {
        this.langkahPerTile = langkahPerTile;
    }

    public void setSuhuAwal(double suhuAwal) {
        this.suhuAwal = suhuAwal;
    }

    public int[] getPosisiBebas() {
        java.util.ArrayList<Integer> daftar = new java.util.ArrayList<>();
        for (int p = 0; p < baris * kolom; p++) {
            int barisP = p / kolom, kolomP = p % kolom;
            boolean tepi = barisP == 0 || kolomP == 0 || barisP == baris - 1 || kolomP == kolom - 1;
            if (!(tepiTetap && tepi)) daftar.add(p);
        }
        int[] hasil = new int[daftar.size()];
        for (int i = 0; i < hasil.length; i++) hasil[i] = daftar.get(i);
        return hasil;
    }
    
    public int[] anneal(int[] papanAwal, long seed, boolean pakaiBerbagi, int jedaSync, int idThread) {
        java.util.Random rand = new java.util.Random(seed + idThread);
        int[] papan = papanAwal.clone();
        int n = papan.length;
        int skor = hitungSkor(papan);
        int skorTerbaik = skor;
        int[] papanTerbaik = papan.clone();

        int[] posisiBebas = getPosisiBebas();
        int m = posisiBebas.length;
        if (m < 2) {
            return papanTerbaik;
        }

        int jumlahLangkah = Math.max(20000, n * langkahPerTile);
        double suhu = suhuAwal;
        double suhuAkhir = 50.0;
        double faktorTurun = Math.pow(suhuAkhir / suhu, 1.0 / jumlahLangkah);
        int jedaPantau = (jedaSync > 0) ? jedaSync : Math.max(100, jumlahLangkah / 50);

        for (int langkah = 0; langkah < jumlahLangkah; langkah++) {
            int indeksA = rand.nextInt(m);
            int indeksB = rand.nextInt(m - 1);
            if (indeksB >= indeksA) {
                indeksB++;
            }
            int posisiA = posisiBebas[indeksA];
            int posisiB = posisiBebas[indeksB];

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

    public int lapis(int posisi) {
        int barisP = posisi / kolom, kolomP = posisi % kolom;
        return Math.min(Math.min(barisP, kolomP), Math.min(baris - 1 - barisP, kolom - 1 - kolomP));
    }

    public int lapisTerdalam() {
        return (Math.min(baris, kolom) - 1) / 2;
    }

    // biaya semua sisi yang kedua ujungnya sudah dikerjakan (bingkai sampai lapisAktif)
    public long biayaDikenal(int[] papan, int lapisAktif) {
        long total = 0;
        for (int p = 0; p < papan.length; p++) {
            if (lapis(p) > lapisAktif) continue;
            if (p % kolom < kolom - 1 && lapis(p + 1) <= lapisAktif) total += kanan[papan[p]][papan[p + 1]];
            if (p / kolom < baris - 1 && lapis(p + kolom) <= lapisAktif) total += bawah[papan[p]][papan[p + kolom]];
        }
        return total;
    }

    private int biayaSisi(int[] papan, int posisi, int tetangga) {
        if (tetangga == posisi + 1) return kanan[papan[posisi]][papan[tetangga]];
        if (tetangga == posisi - 1) return kanan[papan[tetangga]][papan[posisi]];
        if (tetangga == posisi + kolom) return bawah[papan[posisi]][papan[tetangga]];
        return bawah[papan[tetangga]][papan[posisi]];
    }

    private int biayaLokal(int[] papan, int posisi, int lewati, int lapisAktif) {
        if (lapis(posisi) > lapisAktif) return 0;
        int barisP = posisi / kolom, kolomP = posisi % kolom, total = 0;
        if (kolomP > 0 && posisi - 1 != lewati && lapis(posisi - 1) <= lapisAktif) total += biayaSisi(papan, posisi, posisi - 1);
        if (kolomP < kolom - 1 && posisi + 1 != lewati && lapis(posisi + 1) <= lapisAktif) total += biayaSisi(papan, posisi, posisi + 1);
        if (barisP > 0 && posisi - kolom != lewati && lapis(posisi - kolom) <= lapisAktif) total += biayaSisi(papan, posisi, posisi - kolom);
        if (barisP < baris - 1 && posisi + kolom != lewati && lapis(posisi + kolom) <= lapisAktif) total += biayaSisi(papan, posisi, posisi + kolom);
        return total;
    }

    // annealing untuk satu lapisan saja; papan diubah langsung
    public void annealLapisan(int[] papan, int lapisAktif, java.util.Random rand,
                              int langkahPerPosisi, double suhuAwal, double suhuAkhir) {
        java.util.ArrayList<Integer> posisiLapis = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> kolamPosisi = new java.util.ArrayList<>();
        for (int p = 0; p < papan.length; p++) {
            if (lapis(p) == lapisAktif) posisiLapis.add(p);
            if (lapis(p) >= lapisAktif) kolamPosisi.add(p);
        }
        if (kolamPosisi.size() < 2) return;

        long jumlahLangkah = (long) posisiLapis.size() * langkahPerPosisi;
        double suhu = suhuAwal;
        double faktorTurun = Math.pow(suhuAkhir / suhuAwal, 1.0 / jumlahLangkah);

        for (long langkah = 0; langkah < jumlahLangkah; langkah++) {
            int posisiA = posisiLapis.get(rand.nextInt(posisiLapis.size()));
            int posisiB = kolamPosisi.get(rand.nextInt(kolamPosisi.size()));
            if (posisiA != posisiB) {
                int sebelum = biayaLokal(papan, posisiA, -1, lapisAktif) + biayaLokal(papan, posisiB, posisiA, lapisAktif);
                tukar(papan, posisiA, posisiB);
                int sesudah = biayaLokal(papan, posisiA, -1, lapisAktif) + biayaLokal(papan, posisiB, posisiA, lapisAktif);
                int selisih = sesudah - sebelum;
                boolean terima = selisih <= 0 || rand.nextDouble() < Math.exp(-selisih / suhu);
                if (!terima) tukar(papan, posisiA, posisiB);
            }
            suhu *= faktorTurun;
        }
    }
}
