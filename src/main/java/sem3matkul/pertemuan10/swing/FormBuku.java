package sem3matkul.pertemuan10.swing;

import sem3matkul.pertemuan10.dao.BukuDAO;
import sem3matkul.pertemuan10.model.Buku;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Tugas Mandiri Pertemuan 10: input data ke tabel buku. */
public class FormBuku extends JFrame {

    private final JTextField txtNama = new JTextField(28);
    private final JTextField txtPenulis = new JTextField(28);
    private final JTextField txtTipe = new JTextField(28);
    private final JTextField txtGenre = new JTextField(28);
    private final JTextField txtStok = new JTextField(8);

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnKeluar = new JButton("Keluar");

    private final Runnable setelahSimpan;

    public FormBuku() {
        this(() -> { });
    }

    /** @param setelahSimpan dipanggil setelah data berhasil tersimpan */
    public FormBuku(Runnable setelahSimpan) {
        this.setelahSimpan = setelahSimpan;
        initComponents();
        bersihText();
    }

    private void initComponents() {
        setTitle("Form Buku");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel judul = new JLabel("Data Buku");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 16, 0);
        form.add(judul, c);
        c.gridwidth = 1;
        c.insets = new Insets(6, 6, 6, 6);

        tambahBaris(form, c, 1, "Judul", txtNama);
        tambahBaris(form, c, 2, "Penulis", txtPenulis);
        tambahBaris(form, c, 3, "Tipe", txtTipe);
        tambahBaris(form, c, 4, "Genre", txtGenre);
        tambahBaris(form, c, 5, "Stok", txtStok);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        tombol.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 16));
        tombol.add(btnSimpan);
        tombol.add(btnReset);
        tombol.add(btnKeluar);

        btnSimpan.addActionListener(e -> simpan());
        btnReset.addActionListener(e -> bersihText());
        btnKeluar.addActionListener(e -> dispose());

        add(form, BorderLayout.CENTER);
        add(tombol, BorderLayout.SOUTH);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private void tambahBaris(JPanel form, GridBagConstraints c, int baris, String label, JComponent isian) {
        c.gridy = baris;
        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        form.add(isian, c);
    }

    public void bersihText() {
        txtNama.setText("");
        txtPenulis.setText("");
        txtTipe.setText("");
        txtGenre.setText("");
        txtStok.setText("");
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        String penulis = txtPenulis.getText().trim();
        String tipe = txtTipe.getText().trim();
        String genre = txtGenre.getText().trim();
        String stokTeks = txtStok.getText().trim();

        if (nama.isEmpty() || penulis.isEmpty()) {
            peringatan("Judul dan Penulis wajib diisi.");
            return;
        }
        if (!stokTeks.matches("\\d{1,6}")) {
            peringatan("Stok harus berupa angka 0 atau lebih.");
            return;
        }

        try {
            BukuDAO dao = new BukuDAO();
            if (dao.cariByJudulPenulis(nama, penulis)) {
                peringatan("Buku \"" + nama + "\" karya " + penulis + " sudah terdaftar.");
                return;
            }
            int n = dao.tambah(new Buku(nama, penulis, tipe, genre, Integer.parseInt(stokTeks)));
            JOptionPane.showMessageDialog(this, "Buku berhasil disimpan ke Database: " + n);
            bersihText();
            setelahSimpan.run();
        } catch (SQLException ex) {
            Logger.getLogger(FormBuku.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(),
                    "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}
