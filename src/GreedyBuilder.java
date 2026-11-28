// Pemilik: Adi
import java.util.ArrayList;
import java.util.Collections;

public class GreedyBuilder {
    private int[][] kanan, bawah;
    private int baris, kolom;

    public GreedyBuilder(CompatibilityTable tabel, int baris, int kolom) {
        this.kanan = tabel.getKanan();
        this.bawah = tabel.getBawah();
        this.baris = baris;
        this.kolom = kolom;
    }

    // hanya untuk mode tepi tetap, papanAcak dari shuffleDalam; papanAcak sendiri tidak diubah
    public int[] susun(int[] papanAcak, int variasi) {
        int jumlahTile = baris * kolom;
        int[] papan = papanAcak.clone();
        boolean[] terkunci = new boolean[jumlahTile];
        for (int p = 0; p < jumlahTile; p++) {
            if (tepi(p)) terkunci[p] = true;
        }

        for (int posisi : urutan(variasi)) {
            int barisP = posisi / kolom, kolomP = posisi % kolom;
            long biayaTerkecil = Long.MAX_VALUE;
            int posisiTerbaik = posisi;

            for (int q = 0; q < jumlahTile; q++) {
                if (terkunci[q]) continue;
                int tile = papan[q];
                long biaya = 0;
                if (kolomP > 0 && terkunci[posisi - 1]) biaya += kanan[papan[posisi - 1]][tile];
                if (kolomP < kolom - 1 && terkunci[posisi + 1]) biaya += kanan[tile][papan[posisi + 1]];
                if (barisP > 0 && terkunci[posisi - kolom]) biaya += bawah[papan[posisi - kolom]][tile];
                if (barisP < baris - 1 && terkunci[posisi + kolom]) biaya += bawah[tile][papan[posisi + kolom]];
                if (biaya < biayaTerkecil) {
                    biayaTerkecil = biaya;
                    posisiTerbaik = q;
                }
            }

            int sementara = papan[posisi];
            papan[posisi] = papan[posisiTerbaik];
            papan[posisiTerbaik] = sementara;
            terkunci[posisi] = true;
        }
        return papan;
    }

    private boolean tepi(int p) {
        int barisP = p / kolom, kolomP = p % kolom;
        return barisP == 0 || kolomP == 0 || barisP == baris - 1 || kolomP == kolom - 1;
    }

    // variasi 0-3: mulai dari pojok kiri atas, kanan atas, kanan bawah, kiri bawah (searah jarum jam)
    // variasi 4-7: pojok yang sama, tapi berlawanan arah jarum jam
    private ArrayList<Integer> urutan(int variasi) {
        int pojok = variasi % 4;
        boolean berlawananJarum = (variasi / 4) % 2 == 1;
        ArrayList<Integer> hasil = new ArrayList<>();
        boolean[] sudah = new boolean[baris * kolom];

        for (int k = 1; k <= (Math.min(baris, kolom) - 1) / 2; k++) {
            int atas = k, bawahB = baris - 1 - k, kiriK = k, kananK = kolom - 1 - k;
            ArrayList<Integer> cincin = new ArrayList<>();
            for (int c = kiriK; c <= kananK; c++) cincin.add(atas * kolom + c);
            for (int r = atas + 1; r <= bawahB; r++) cincin.add(r * kolom + kananK);
            if (bawahB > atas) for (int c = kananK - 1; c >= kiriK; c--) cincin.add(bawahB * kolom + c);
            if (kananK > kiriK) for (int r = bawahB - 1; r > atas; r--) cincin.add(r * kolom + kiriK);

            if (berlawananJarum) Collections.reverse(cincin);
            int[] daftarPojok = {
                atas * kolom + kiriK, atas * kolom + kananK,
                bawahB * kolom + kananK, bawahB * kolom + kiriK
            };
            int awal = cincin.indexOf(daftarPojok[pojok]);
            for (int i = 0; i < cincin.size(); i++) {
                int p = cincin.get((awal + i) % cincin.size());
                hasil.add(p);
                sudah[p] = true;
            }
        }

        for (int p = 0; p < baris * kolom; p++) {
            if (!tepi(p) && !sudah[p]) hasil.add(p);
        }
        return hasil;
    }

    public int lapis(int posisi) {
        int barisP = posisi / kolom, kolomP = posisi % kolom;
        return Math.min(Math.min(barisP, kolomP), Math.min(baris - 1 - barisP, kolom - 1 - kolomP));
    }

    // satu lapisan saja (lapis 0 = bingkai, 1 = yang menempel bingkai, dst); papan diubah langsung
    public void susunLapisan(int[] papan, int lapisAktif, int variasi) {
        int jumlahTile = baris * kolom;
        boolean[] terkunci = new boolean[jumlahTile];
        for (int p = 0; p < jumlahTile; p++) {
            if (lapis(p) < lapisAktif) terkunci[p] = true;
        }

        for (int posisi : cincin(lapisAktif, variasi)) {
            int barisP = posisi / kolom, kolomP = posisi % kolom;
            long biayaTerkecil = Long.MAX_VALUE;
            int posisiTerbaik = posisi;

            for (int q = 0; q < jumlahTile; q++) {
                if (terkunci[q]) continue;
                int tile = papan[q];
                long biaya = 0;
                if (kolomP > 0 && terkunci[posisi - 1]) biaya += kanan[papan[posisi - 1]][tile];
                if (kolomP < kolom - 1 && terkunci[posisi + 1]) biaya += kanan[tile][papan[posisi + 1]];
                if (barisP > 0 && terkunci[posisi - kolom]) biaya += bawah[papan[posisi - kolom]][tile];
                if (barisP < baris - 1 && terkunci[posisi + kolom]) biaya += bawah[tile][papan[posisi + kolom]];
                if (biaya < biayaTerkecil) {
                    biayaTerkecil = biaya;
                    posisiTerbaik = q;
                }
            }

            int sementara = papan[posisi];
            papan[posisi] = papan[posisiTerbaik];
            papan[posisiTerbaik] = sementara;
            terkunci[posisi] = true;
        }
    }

    private ArrayList<Integer> cincin(int k, int variasi) {
        int pojok = variasi % 4;
        boolean berlawananJarum = (variasi / 4) % 2 == 1;
        int atas = k, bawahB = baris - 1 - k, kiriK = k, kananK = kolom - 1 - k;

        ArrayList<Integer> daftar = new ArrayList<>();
        for (int c = kiriK; c <= kananK; c++) daftar.add(atas * kolom + c);
        for (int r = atas + 1; r <= bawahB; r++) daftar.add(r * kolom + kananK);
        if (bawahB > atas) for (int c = kananK - 1; c >= kiriK; c--) daftar.add(bawahB * kolom + c);
        if (kananK > kiriK) for (int r = bawahB - 1; r > atas; r--) daftar.add(r * kolom + kiriK);

        if (berlawananJarum) Collections.reverse(daftar);
        int[] daftarPojok = {
            atas * kolom + kiriK, atas * kolom + kananK,
            bawahB * kolom + kananK, bawahB * kolom + kiriK
        };
        int awal = daftar.indexOf(daftarPojok[pojok]);
        ArrayList<Integer> hasil = new ArrayList<>();
        for (int i = 0; i < daftar.size(); i++) hasil.add(daftar.get((awal + i) % daftar.size()));
        return hasil;
    }
}