// Pemilik: Kak Qim
public class QualityChecker {

    public double persenTileBenar(int[] papan) {
        if (papan == null || papan.length == 0) {
            return 0.0;
        }
        int jumlahBenar = 0;
        for (int p = 0; p < papan.length; p++) {
            if (papan[p] == p) {
                jumlahBenar++;
            }
        }
        return (jumlahBenar * 100.0) / papan.length;
    }
}
