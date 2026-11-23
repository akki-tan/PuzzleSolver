// Pemilik: Adi
import java.awt.image.BufferedImage;

public class PuzzleData {
    private BufferedImage[] tiles;
    private int ukuranTile, baris, kolom, jumlahTile;

    public PuzzleData(BufferedImage[] tiles, int ukuranTile, int baris, int kolom) {
        this.tiles = tiles;
        this.ukuranTile = ukuranTile;
        this.baris = baris;
        this.kolom = kolom;
        this.jumlahTile = baris * kolom;
    }

    public BufferedImage[] getTiles() { return tiles; }
    public int getUkuranTile() { return ukuranTile; }
    public int getBaris() { return baris; }
    public int getKolom() { return kolom; }
    public int getJumlahTile() { return jumlahTile; }
}
