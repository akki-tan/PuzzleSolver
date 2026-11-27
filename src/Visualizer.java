// Pemilik: Kak Qim
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Visualizer {

    public void saveImage(int[] papan, PuzzleData data, String namaFile) {
        if (papan == null || data == null) {
            return;
        }
        int kolom = data.getKolom();
        int baris = data.getBaris();
        int ukuranTile = data.getUkuranTile();
        BufferedImage[] tiles = data.getTiles();

        BufferedImage gambarHasil = new BufferedImage(
                kolom * ukuranTile,
                baris * ukuranTile,
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = gambarHasil.createGraphics();
        for (int p = 0; p < papan.length; p++) {
            int x = (p % kolom) * ukuranTile;
            int y = (p / kolom) * ukuranTile;
            g.drawImage(tiles[papan[p]], x, y, null);
        }
        g.dispose();

        File fileOutput = new File(namaFile);
        if (fileOutput.getParentFile() != null) {
            fileOutput.getParentFile().mkdirs();
        }
        String format = namaFile.toLowerCase().endsWith(".jpg")
                || namaFile.toLowerCase().endsWith(".jpeg") ? "jpg" : "png";
        try {
            ImageIO.write(gambarHasil, format, fileOutput);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan gambar ke " + namaFile + ": " + e.getMessage());
        }
    }
}
