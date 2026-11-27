// Pemilik: Adi
import java.awt.image.BufferedImage;
import java.util.Random;

public class TileSplitter {
    private BufferedImage gambar;
    private int baris, kolom, ukuranTile;

    public TileSplitter(BufferedImage gambar, int baris, int kolom) {
        this.gambar = gambar;
        this.baris = baris;
        this.kolom = kolom;
    }

    public PuzzleData split() {
        ukuranTile = Math.min(gambar.getWidth() / kolom, gambar.getHeight() / baris);
        BufferedImage[] tiles = new BufferedImage[baris * kolom];
        for (int i = 0; i < tiles.length; i++) {
            int x = (i % kolom) * ukuranTile;
            int y = (i / kolom) * ukuranTile;
            tiles[i] = gambar.getSubimage(x, y, ukuranTile, ukuranTile);
        }
        return new PuzzleData(tiles, ukuranTile, baris, kolom);
    }

    public int[] shuffle(long seed) {
        int jumlahTile = baris * kolom;
        int[] papan = new int[jumlahTile];
        for (int i = 0; i < jumlahTile; i++) papan[i] = i;

        Random acak = new Random(seed);
        for (int i = jumlahTile - 1; i > 0; i--) {
            int j = acak.nextInt(i + 1);
            int sementara = papan[i];
            papan[i] = papan[j];
            papan[j] = sementara;
        }
        return papan;
    }
}