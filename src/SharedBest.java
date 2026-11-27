// Pemilik: Kak Qim
public class SharedBest {
    static int skorTerbaik = Integer.MAX_VALUE;
    static int[] papanTerbaik = null;

    public static synchronized int[] sync(int skor, int[] papan) {
        if (papan != null && (skor < skorTerbaik || papanTerbaik == null)) {
            skorTerbaik = skor;
            papanTerbaik = papan.clone();
        }
        return (papanTerbaik != null) ? papanTerbaik.clone() : null;
    }

    public static synchronized int[] getPapanTerbaik() { return papanTerbaik; }
    public static synchronized int getSkorTerbaik() { return skorTerbaik; }

    public static synchronized void reset() {
        skorTerbaik = Integer.MAX_VALUE;
        papanTerbaik = null;
    }
}
