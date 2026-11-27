// Pemilik: Kak Qim
public class RaceDemo {
    private int hitungan;
    private int jumlahThread, jumlahTambah;

    public RaceDemo() {
        this(4, 100000);
    }

    public RaceDemo(int jumlahThread, int jumlahTambah) {
        this.jumlahThread = jumlahThread;
        this.jumlahTambah = jumlahTambah;
    }

    public void jalankanTanpaLock() {
        hitungan = 0;
        Thread[] threads = new Thread[jumlahThread];
        for (int t = 0; t < jumlahThread; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < jumlahTambah; i++) {
                    hitungan++;
                }
            });
            threads[t].start();
        }
        tungguSemua(threads);
        int seharusnya = jumlahThread * jumlahTambah;
        System.out.println("Tanpa lock  -> hasil: " + hitungan + " (seharusnya: " + seharusnya + ")");
    }

    public void jalankanDenganLock() {
        hitungan = 0;
        Thread[] threads = new Thread[jumlahThread];
        for (int t = 0; t < jumlahThread; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < jumlahTambah; i++) {
                    tambahDenganLock();
                }
            });
            threads[t].start();
        }
        tungguSemua(threads);
        int seharusnya = jumlahThread * jumlahTambah;
        System.out.println("Dengan lock -> hasil: " + hitungan + " (seharusnya: " + seharusnya + ")");
    }

    private synchronized void tambahDenganLock() {
        hitungan++;
    }

    private void tungguSemua(Thread[] threads) {
        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
