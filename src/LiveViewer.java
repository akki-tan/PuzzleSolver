// Pemilik: Adi
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LiveViewer {
    private PuzzleData data;
    private JFrame jendela;
    private JLabel labelTerbaik;
    private JLabel[] labelThread;
    private PanelPapan[] panel;
    private int[][] papanSalinan;
    private int[] skorThread;

    public LiveViewer(PuzzleData data, int jumlahThread) {
        this.data = data;
        papanSalinan = new int[jumlahThread][];
        skorThread = new int[jumlahThread];
    }

    public synchronized void update(int idThread, int[] papan, int skor) {
        papanSalinan[idThread] = papan.clone();
        skorThread[idThread] = skor;
    }

    public synchronized int[] ambilPapan(int idThread) {
        return papanSalinan[idThread];
    }

    public synchronized int ambilSkor(int idThread) {
        return skorThread[idThread];
    }

    public void tampilkan() {
        SwingUtilities.invokeLater(() -> {
            int jumlahThread = papanSalinan.length;
            jendela = new JFrame("Puzzle Solver - pemantauan thread");
            jendela.setLayout(new BorderLayout());

            labelTerbaik = new JLabel("Skor terbaik: -");
            jendela.add(labelTerbaik, BorderLayout.NORTH);

            JPanel isi = new JPanel(new GridLayout(0, 2, 8, 8));
            labelThread = new JLabel[jumlahThread];
            panel = new PanelPapan[jumlahThread];
            for (int t = 0; t < jumlahThread; t++) {
                labelThread[t] = new JLabel("Thread " + t);
                panel[t] = new PanelPapan(t);
                JPanel sel = new JPanel(new BorderLayout());
                sel.add(labelThread[t], BorderLayout.NORTH);
                sel.add(panel[t], BorderLayout.CENTER);
                isi.add(sel);
            }
            jendela.add(isi, BorderLayout.CENTER);
            jendela.setSize(900, 700);
            jendela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jendela.setVisible(true);

            new Timer(200, e -> gambarUlang()).start();
        });
    }

    public void gambarUlang() {
        if (jendela == null) return;
        int terbaik = SharedBest.getSkorTerbaik();
        labelTerbaik.setText("Skor terbaik: " + (terbaik == Integer.MAX_VALUE ? "-" : terbaik));
        for (int t = 0; t < panel.length; t++) {
            labelThread[t].setText("Thread " + t + " | skor: " + ambilSkor(t));
            panel[t].repaint();
        }
    }

    class PanelPapan extends JPanel {
        int idThread;

        PanelPapan(int idThread) {
            this.idThread = idThread;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[] papan = ambilPapan(idThread);
            if (papan == null) return;

            int baris = data.getBaris();
            int kolom = data.getKolom();
            int ukuranSel = Math.min(getWidth() / kolom, getHeight() / baris);
            BufferedImage[] tiles = data.getTiles();
            for (int p = 0; p < papan.length; p++) {
                int x = (p % kolom) * ukuranSel;
                int y = (p / kolom) * ukuranSel;
                g.drawImage(tiles[papan[p]], x, y, ukuranSel, ukuranSel, null);
            }
        }
    }
}