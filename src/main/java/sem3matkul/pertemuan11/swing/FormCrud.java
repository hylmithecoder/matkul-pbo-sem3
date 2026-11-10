package sem3matkul.pertemuan11.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kerangka form CRUD: isian di atas, tabel data di bawah.
 * Pilih baris pada tabel untuk mengisi form, lalu tekan Ubah atau Hapus.
 * Subclass mendaftarkan isiannya di konstruktor, lalu memanggil {@link #selesaikan()}.
 */
abstract class FormCrud<T> extends JFrame {

    /** Operasi database yang mengembalikan jumlah baris yang berubah. */
    @FunctionalInterface
    private interface AksiData {
        int jalankan() throws SQLException;
    }

    private final JPanel form = new JPanel(new GridBagLayout());
    private final GridBagConstraints c = new GridBagConstraints();
    private final DefaultTableModel modelTabel;
    private final JTable tabel;

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnKeluar = new JButton("Keluar");

    private final String namaData;
    private final Runnable setelahUbah;
    private List<T> data = List.of();
    private int barisForm = 1;

    /** @param setelahUbah dipanggil setelah data berhasil ditambah, diubah, atau dihapus */
    protected FormCrud(String namaData, String[] kolom, Runnable setelahUbah) {
        this.namaData = namaData;
        this.setelahUbah = setelahUbah;
        this.modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
        this.tabel = new JTable(modelTabel);

        setTitle("Form " + namaData);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel judul = new JLabel("Data " + namaData);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));

        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 16, 0);
        form.add(judul, c);
        c.gridwidth = 1;
        c.insets = new Insets(6, 6, 6, 6);
    }

    protected abstract List<T> muat() throws SQLException;

    protected abstract Object[] keBaris(T item);

    protected abstract void isiForm(T item);

    protected abstract void bersihForm();

    /** Membaca isian form. Melempar IllegalArgumentException bila isian tidak valid. */
    protected abstract T bacaForm();

    protected abstract int tambah(T baru) throws SQLException;

    protected abstract int ubah(T lama, T baru) throws SQLException;

    protected abstract int hapus(T item) throws SQLException;

    protected final void tambahBaris(String label, JComponent isian) {
        c.gridy = barisForm++;
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

    /** Menyusun jendela dan memuat data. Dipanggil di akhir konstruktor subclass. */
    protected final void selesaikan() {
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        tabel.setPreferredScrollableViewportSize(new Dimension(135 * modelTabel.getColumnCount(), 180));
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                barisDipilih();
            }
        });

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        tombol.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 16));
        tombol.add(btnSimpan);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnReset);
        tombol.add(btnKeluar);

        btnSimpan.addActionListener(e -> jalankan("disimpan", () -> tambah(bacaForm())));
        btnUbah.addActionListener(e -> jalankan("diubah", () -> ubah(terpilih(), bacaForm())));
        btnHapus.addActionListener(e -> konfirmasiHapus());
        btnReset.addActionListener(e -> reset());
        btnKeluar.addActionListener(e -> dispose());

        JScrollPane panelTabel = new JScrollPane(tabel);
        panelTabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        add(form, BorderLayout.NORTH);
        add(panelTabel, BorderLayout.CENTER);
        add(tombol, BorderLayout.SOUTH);

        reset();
        barisDipilih();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private T terpilih() {
        return data.get(tabel.getSelectedRow());
    }

    private void barisDipilih() {
        boolean adaPilihan = tabel.getSelectedRow() >= 0;
        btnSimpan.setEnabled(!adaPilihan);
        btnUbah.setEnabled(adaPilihan);
        btnHapus.setEnabled(adaPilihan);
        if (adaPilihan) {
            isiForm(terpilih());
        }
    }

    private void reset() {
        tabel.clearSelection();
        bersihForm();
        muatUlang();
    }

    private void muatUlang() {
        try {
            List<T> hasil = muat();
            modelTabel.setRowCount(0);
            data = hasil;
            for (T item : data) {
                modelTabel.addRow(keBaris(item));
            }
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void konfirmasiHapus() {
        int jawab = JOptionPane.showConfirmDialog(this, "Hapus data " + namaData + " yang dipilih?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            jalankan("dihapus", () -> hapus(terpilih()));
        }
    }

    private void jalankan(String hasilAksi, AksiData aksi) {
        try {
            int n = aksi.jalankan();
            reset();
            setelahUbah.run();
            if (n == 0) {
                peringatan("Data tidak ditemukan, mungkin sudah dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Data " + namaData + " berhasil " + hasilAksi + ".");
            }
        } catch (IllegalArgumentException ex) {
            peringatan(ex.getMessage());
        } catch (SQLIntegrityConstraintViolationException ex) {
            peringatan("Data bentrok: email, plat nomor, atau no tiket sudah dipakai.");
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private void tampilkanError(SQLException ex) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Gagal mengakses database: " + ex.getMessage(),
                "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
    }

    /** Mengambil teks isian dan memastikan tidak kosong. */
    protected static String wajib(String label, JTextField isian) {
        String nilai = isian.getText().trim();
        if (nilai.isEmpty()) {
            throw new IllegalArgumentException(label + " wajib diisi.");
        }
        return nilai;
    }

    protected static void cekEmail(String email) {
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }
    }
}
