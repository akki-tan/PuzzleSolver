// Pemilik: Adi
import java.awt.image.BufferedImage;

public class EdgeScorer {
    private PuzzleData data;
    private int ukuran;
    private int[][] piksel;

    public EdgeScorer(PuzzleData data) {
        this.data = data;
        this.ukuran = data.getUkuranTile();

        BufferedImage[] tiles = data.getTiles();
        piksel = new int[tiles.length][];
        for (int i = 0; i < tiles.length; i++) {
            piksel[i] = tiles[i].getRGB(0, 0, ukuran, ukuran, null, 0, ukuran);
        }
    }

    public int edgeScore(int tileI, int tileJ, boolean keKanan) {
        int total = 0;
        for (int k = 0; k < ukuran; k++) {
            int pikselA, pikselB;
            if (keKanan) {
                pikselA = piksel[tileI][k * ukuran + (ukuran - 1)];
                pikselB = piksel[tileJ][k * ukuran];
            } else {
                pikselA = piksel[tileI][(ukuran - 1) * ukuran + k];
                pikselB = piksel[tileJ][k];
            }
            total += selisihPiksel(pikselA, pikselB);
        }
        return total;
    }

    public int selisihPiksel(int pikselA, int pikselB) {
        int merah = Math.abs(((pikselA >> 16) & 0xFF) - ((pikselB >> 16) & 0xFF));
        int hijau = Math.abs(((pikselA >> 8) & 0xFF) - ((pikselB >> 8) & 0xFF));
        int biru = Math.abs((pikselA & 0xFF) - (pikselB & 0xFF));
        return merah + hijau + biru;
    }
}
