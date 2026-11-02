package sem3matkul.pertemuan7.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Percobaan 2 - Komponen dasar Swing.
 * Form pendaftaran akun perpustakaan yang memuat JLabel, JTextField,
 * JPasswordField, JRadioButton + ButtonGroup, JComboBox, JCheckBox,
 * JTextArea di dalam JScrollPane, dan JButton.
 */
public class KomponenDasar extends JFrame {

    private final JTextField txtNama = new JTextField();
    private final JPasswordField txtSandi = new JPasswordField();
    private final JRadioButton rbLaki = new JRadioButton("Laki-laki", true);
    private final JRadioButton rbPerempuan = new JRadioButton("Perempuan");
    private final JComboBox<String> cmbProdi =
            new JComboBox<>(new String[]{"TRPL", "Teknik Komputer", "Akuntansi", "Administrasi Bisnis"});
    private final JCheckBox cbFiksi = new JCheckBox("Fiksi");
    private final JCheckBox cbTeknologi = new JCheckBox("Teknologi");
    private final JCheckBox cbSains = new JCheckBox("Sains");
    private final JTextArea areaCatatan = new JTextArea(3, 20);

    public KomponenDasar() {
        super("Komponen Dasar Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Radio button harus dimasukkan ke ButtonGroup agar hanya satu yang terpilih
        ButtonGroup grup = new ButtonGroup();
        grup.add(rbLaki);
        grup.add(rbPerempuan);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));   // 2 kolom, baris otomatis
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        form.add(new JLabel("Nama"));
        form.add(txtNama);

        form.add(new JLabel("Kata sandi"));
        form.add(txtSandi);

        form.add(new JLabel("Jenis kelamin"));
        JPanel panelJk = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelJk.add(rbLaki);
        panelJk.add(rbPerempuan);
        form.add(panelJk);

        form.add(new JLabel("Program studi"));
        form.add(cmbProdi);

        form.add(new JLabel("Minat bacaan"));
        JPanel panelMinat = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelMinat.add(cbFiksi);
        panelMinat.add(cbTeknologi);
        panelMinat.add(cbSains);
        form.add(panelMinat);

        // JTextArea ditaruh dalam JScrollPane agar dapat di-scroll
        JPanel panelCatatan = new JPanel(new BorderLayout(0, 5));
        panelCatatan.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        panelCatatan.add(new JLabel("Catatan"), BorderLayout.NORTH);
        areaCatatan.setLineWrap(true);
        panelCatatan.add(new JScrollPane(areaCatatan), BorderLayout.CENTER);

        JButton btnTampil = new JButton("Tampilkan Data");
        btnTampil.addActionListener(e -> tampilkan());
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelTombol.add(btnTampil);

        add(form, BorderLayout.NORTH);
        add(panelCatatan, BorderLayout.CENTER);
        add(panelTombol, BorderLayout.SOUTH);

        setSize(460, 420);
        setLocationRelativeTo(null);
    }

    private void tampilkan() {
        StringBuilder minat = new StringBuilder();
        if (cbFiksi.isSelected()) minat.append("Fiksi ");
        if (cbTeknologi.isSelected()) minat.append("Teknologi ");
        if (cbSains.isSelected()) minat.append("Sains ");

        String jk = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";
        // getPassword() menghasilkan char[]; jangan ditampilkan apa adanya
        String sandi = "*".repeat(txtSandi.getPassword().length);

        String pesan = "Nama           : " + txtNama.getText()
                + "\nKata sandi     : " + sandi
                + "\nJenis kelamin  : " + jk
                + "\nProgram studi  : " + cmbProdi.getSelectedItem()
                + "\nMinat          : " + (minat.length() == 0 ? "-" : minat.toString().trim())
                + "\nCatatan        : " + areaCatatan.getText();
        JOptionPane.showMessageDialog(this, pesan, "Data Anda", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                // bila Nimbus tidak tersedia, pakai tampilan bawaan
            }
            new KomponenDasar().setVisible(true);
        });
    }
}
