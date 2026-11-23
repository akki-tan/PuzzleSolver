// Pemilik: Adi
import java.awt.image.BufferedImage;

public class TileSplitter {
    private BufferedImage gambar;
    private int baris, kolom, ukuranTile;

    public TileSplitter(BufferedImage gambar, int baris, int kolom) {
        this.gambar = gambar;
        this.baris = baris;
        this.kolom = kolom;
    }

    public PuzzleData split() {
        // TODO: ukuranTile = lebar gambar / kolom, potong pakai getSubimage, tile ke-i = ID i
        return null;
    }

    public int[] shuffle(long seed) {
        // TODO: papan[posisi] = ID tile, acak pakai Random(seed)
        return null;
    }
}
