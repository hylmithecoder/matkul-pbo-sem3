package sem3matkul.pertemuan7.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Percobaan 4 - Form Anggota lengkap.
 * Menggabungkan BorderLayout (kerangka), GridBagLayout (form),
 * JScrollPane (area teks), dan event ActionListener pada tiga tombol.
 * Data disimpan sementara di List<Anggota> (belum ke basis data);
 * pada minggu berikutnya bagian ini dihubungkan ke AnggotaDAO.
 */
public class FormAnggota extends JFrame {

    private final JTextField txtId = new JTextField(12);
    private final JTextField txtNama = new JTextField(20);
    private final JTextArea txtAlamat = new JTextArea(3, 20);
    private final JTextField txtNoHp = new JTextField(14);
    private final JTextArea areaLog = new JTextArea(6, 20);

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnKeluar = new JButton("Keluar");

    private final List<Anggota> daftar = new ArrayList<>();

    public FormAnggota() {
        super("Form Anggota Perpustakaan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        add(buatJudul(), BorderLayout.NORTH);

        JPanel tengah = new JPanel(new BorderLayout(0, 8));
        tengah.setBorder(BorderFactory.createEmptyBorder(8, 15, 0, 15));
        tengah.add(buatForm(), BorderLayout.NORTH);
        tengah.add(buatPanelLog(), BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        add(buatPanelTombol(), BorderLayout.SOUTH);

        // Event handling: setiap tombol punya listener sendiri
        btnSimpan.addActionListener(e -> simpan());
        btnReset.addActionListener(e -> reset());
        btnKeluar.addActionListener(e -> dispose());

        pack();                          // ukuran jendela menyesuaikan isi
        setSize(Math.max(getWidth(), 480), getHeight());
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------- penyusunan UI

    private JComponent buatJudul() {
        JLabel judul = new JLabel("DATA ANGGOTA PERPUSTAKAAN", SwingConstants.CENTER);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 17f));
        judul.setForeground(Color.WHITE);
        judul.setOpaque(true);
        judul.setBackground(new Color(0x0F3057));
        judul.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return judul;
    }

    /** Form berpasangan label-isian memakai GridBagLayout. */
    private JPanel buatForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        tambahBaris(form, c, 0, "ID Anggota", txtId);
        tambahBaris(form, c, 1, "Nama", txtNama);
        tambahBaris(form, c, 2, "Alamat", new JScrollPane(txtAlamat));
        tambahBaris(form, c, 3, "No. HP", txtNoHp);
        return form;
    }

    private void tambahBaris(JPanel form, GridBagConstraints c, int baris, String label, JComponent isian) {
        c.gridy = baris;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;                          // kolom isian melebar bila jendela diperbesar
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(isian, c);
    }

    private JComponent buatPanelLog() {
        areaLog.setEditable(false);
        JScrollPane sp = new JScrollPane(areaLog);
        sp.setBorder(BorderFactory.createTitledBorder("Anggota tersimpan (sementara di memori)"));
        return sp;
    }

    private JPanel buatPanelTombol() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
        p.add(btnSimpan);
        p.add(btnReset);
        p.add(btnKeluar);
        return p;
    }

    // ------------------------------------------------------------ logika event

    private void simpan() {
        String id = txtId.getText().trim();
        String nama = txtNama.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noHp = txtNoHp.getText().trim();

        if (id.isEmpty() || nama.isEmpty()) {
            peringatan("ID Anggota dan Nama wajib diisi.");
            return;
        }
        if (!noHp.isEmpty() && !noHp.matches("\\d{10,13}")) {
            peringatan("No. HP harus berupa 10 sampai 13 digit angka.");
            return;
        }
        if (idSudahAda(id)) {
            peringatan("ID Anggota " + id + " sudah terdaftar.");
            return;
        }

        Anggota a = new Anggota(id, nama, alamat, noHp);
        daftar.add(a);
        // Pada minggu berikutnya: new AnggotaDAO().tambah(a);
        areaLog.append(a.getIdAnggota() + " | " + a.getNama() + " | " + a.getNoHp() + "\n");
        reset();
        JOptionPane.showMessageDialog(this, "Anggota berhasil disimpan. Total: " + daftar.size());
    }

    private boolean idSudahAda(String id) {
        for (Anggota a : daftar) {
            if (a.getIdAnggota().equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    private void reset() {
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtNoHp.setText("");
        txtId.requestFocusInWindow();
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                // pakai tampilan bawaan
            }
            new FormAnggota().setVisible(true);
        });
    }
}
