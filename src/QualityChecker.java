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

    public double persenTileBenarDalam(int[] papan, int baris, int kolom) {
        int jumlahDalam = 0, jumlahBenar = 0;
        for (int p = 0; p < papan.length; p++) {
            int barisP = p / kolom, kolomP = p % kolom;
            if (barisP == 0 || kolomP == 0 || barisP == baris - 1 || kolomP == kolom - 1) continue;
            jumlahDalam++;
            if (papan[p] == p) jumlahBenar++;
        }
        return jumlahDalam == 0 ? 0 : (jumlahBenar * 100.0) / jumlahDalam;
    }
    
}
