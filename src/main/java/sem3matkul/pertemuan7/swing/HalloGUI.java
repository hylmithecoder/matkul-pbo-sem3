package sem3matkul.pertemuan7.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Percobaan 1 - Jendela GUI pertama.
 * Menampilkan JFrame berisi satu JLabel dan satu JButton.
 */
public class HalloGUI {

    public static JFrame createFrame() {
        JFrame frame = new JFrame("Halo GUI - PBO II");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));

        JLabel label = new JLabel("Selamat datang di Pemrograman GUI Java");
        JButton tombol = new JButton("Klik Saya");

        // Event: saat tombol diklik, teks label berubah
        tombol.addActionListener(e -> label.setText("Tombol sudah diklik!"));

        frame.add(label);
        frame.add(tombol);

        frame.setSize(420, 160);
        frame.setLocationRelativeTo(null);   // tampil di tengah layar
        return frame;
    }

    public static void main(String[] args) {
        // Semua pembuatan GUI dijalankan di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = createFrame();
            frame.setVisible(true);
        });
    }
}
