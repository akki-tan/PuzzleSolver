// Pemilik: Adi
public class SerialMatrixBuilder {
    private PuzzleData data;
    private EdgeScorer scorer;

    public SerialMatrixBuilder(PuzzleData data, EdgeScorer scorer) {
        this.data = data;
        this.scorer = scorer;
    }

    public CompatibilityTable build() {
        int jumlahTile = data.getJumlahTile();
        CompatibilityTable tabel = new CompatibilityTable(jumlahTile);
        int[][] kanan = tabel.getKanan();
        int[][] bawah = tabel.getBawah();

        for (int tileI = 0; tileI < jumlahTile; tileI++) {
            for (int tileJ = 0; tileJ < jumlahTile; tileJ++) {
                if (tileI == tileJ) continue;
                kanan[tileI][tileJ] = scorer.edgeScore(tileI, tileJ, true);
                bawah[tileI][tileJ] = scorer.edgeScore(tileI, tileJ, false);
            }
        }
        return tabel;
    }
}