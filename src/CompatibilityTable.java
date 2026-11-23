// Pemilik: Adi
public class CompatibilityTable {
    private int[][] kanan, bawah;

    public CompatibilityTable(int jumlahTile) {
        kanan = new int[jumlahTile][jumlahTile];
        bawah = new int[jumlahTile][jumlahTile];
    }

    public int[][] getKanan() { return kanan; }
    public int[][] getBawah() { return bawah; }
}
