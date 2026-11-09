package sem3matkul.pertemuan10.swing;

import sem3matkul.pertemuan10.dao.AnggotaDAO;
import sem3matkul.pertemuan10.model.Anggota;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Form input data anggota ke database, mengikuti langkah pada modul Pertemuan 10. */
public class FormAnggota extends JFrame {

    private final JTextField txtId = new JTextField(24);
    private final JTextField txtNama = new JTextField(28);
    private final JTextArea txtAlamat = new JTextArea(3, 28);
    private final JTextField txtNotelp = new JTextField(28);

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnKeluar = new JButton("Keluar");

    private final Runnable setelahSimpan;

    public FormAnggota() {
        this(() -> { });
    }

    /** @param setelahSimpan dipanggil setelah data berhasil tersimpan */
    public FormAnggota(Runnable setelahSimpan) {
        this.setelahSimpan = setelahSimpan;
        initComponents();
        bersihText();
    }

    private void initComponents() {
        setTitle("Form Anggota");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel judul = new JLabel("Data Anggota");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 16, 0);
        form.add(judul, c);
        c.gridwidth = 1;
        c.insets = new Insets(6, 6, 6, 6);

        tambahBaris(form, c, 1, "ID", txtId);
        tambahBaris(form, c, 2, "Nama", txtNama);
        tambahBaris(form, c, 3, "Alamat", new JScrollPane(txtAlamat));
        tambahBaris(form, c, 4, "No Telp", txtNotelp);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        tombol.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 16));
        tombol.add(btnSimpan);
        tombol.add(btnReset);
        tombol.add(btnKeluar);

        btnSimpan.addActionListener(this::btnSimpanActionPerformed);
        btnReset.addActionListener(this::btnResetActionPerformed);
        btnKeluar.addActionListener(this::btnKeluarActionPerformed);

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
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtNotelp.setText("");
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private boolean idSudahAda(String id) throws SQLException {
        AnggotaDAO dao = new AnggotaDAO();

        if (dao.cariById(id)) {
            return true;
        }
        return false;
    }

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            AnggotaDAO dao = new AnggotaDAO();
            String id = txtId.getText().trim();
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String noHp = txtNotelp.getText().trim();
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
            int n = dao.tambah(new Anggota(id, nama, alamat, noHp));
            JOptionPane.showMessageDialog(this, "Anggota berhasil disimpan ke Database: " + n);
            bersihText();
            setelahSimpan.run();
        } catch (SQLException ex) {
            Logger.getLogger(FormAnggota.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(),
                    "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {
        bersihText();
    }

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }
}
