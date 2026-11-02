package sem3matkul.pertemuan7.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Percobaan 3 - Layout manager.
 * Empat tab memperlihatkan FlowLayout, BorderLayout, GridLayout,
 * dan layout bersarang (kombinasi beberapa layout dalam satu jendela).
 */
public class DemoLayout extends JFrame {

    public DemoLayout() {
        super("Demo Layout Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("FlowLayout", panelFlow());
        tab.addTab("BorderLayout", panelBorder());
        tab.addTab("GridLayout", panelGrid());
        tab.addTab("Bersarang", panelBersarang());
        add(tab);

        setSize(520, 340);
        setLocationRelativeTo(null);
    }

    /** FlowLayout: komponen disusun berurutan kiri ke kanan, pindah baris bila penuh. */
    private JPanel panelFlow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (int i = 1; i <= 7; i++) {
            p.add(new JButton("Tombol " + i));
        }
        return p;
    }

    /** BorderLayout: lima area NORTH, SOUTH, WEST, EAST, CENTER. */
    private JPanel panelBorder() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JButton("NORTH"), BorderLayout.NORTH);
        p.add(new JButton("SOUTH"), BorderLayout.SOUTH);
        p.add(new JButton("WEST"), BorderLayout.WEST);
        p.add(new JButton("EAST"), BorderLayout.EAST);
        p.add(new JButton("CENTER (menyerap sisa ruang)"), BorderLayout.CENTER);
        return p;
    }

    /** GridLayout: kisi 3 x 3 dengan sel berukuran sama. */
    private JPanel panelGrid() {
        JPanel p = new JPanel(new GridLayout(3, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            p.add(new JButton(String.valueOf(i)));
        }
        return p;
    }

    /** Kerangka aplikasi umum: BorderLayout sebagai induk, panel anak memakai layout lain. */
    private JPanel panelBersarang() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel judul = new JLabel("Sistem Perpustakaan", SwingConstants.CENTER);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 18f));
        root.add(judul, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(4, 1, 0, 6));
        menu.add(new JButton("Anggota"));
        menu.add(new JButton("Buku"));
        menu.add(new JButton("Peminjaman"));
        menu.add(new JButton("Laporan"));
        root.add(menu, BorderLayout.WEST);

        JTextArea isi = new JTextArea("Area isi utama (CENTER)");
        root.add(new JScrollPane(isi), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bawah.add(new JButton("OK"));
        bawah.add(new JButton("Batal"));
        root.add(bawah, BorderLayout.SOUTH);
        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                // pakai tampilan bawaan
            }
            new DemoLayout().setVisible(true);
        });
    }
}
