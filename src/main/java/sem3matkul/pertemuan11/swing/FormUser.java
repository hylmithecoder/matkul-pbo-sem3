package sem3matkul.pertemuan11.swing;

import sem3matkul.pertemuan11.handlers.UserHandler;
import sem3matkul.pertemuan11.models.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class FormUser extends FormCrud<User> {

    private final JTextField txtNama = new JTextField(28);
    private final JTextField txtEmail = new JTextField(28);
    private final JComboBox<String> cmbOtoritas = new JComboBox<>(new String[]{"admin", "petugas"});
    private final JTextField txtNoHp = new JTextField(28);

    private final UserHandler handler = new UserHandler();

    public FormUser() {
        this(() -> { });
    }

    public FormUser(Runnable setelahUbah) {
        super("User", new String[]{"ID", "Nama", "Email", "Otoritas", "No Telp"}, setelahUbah);
        tambahBaris("Nama", txtNama);
        tambahBaris("Email", txtEmail);
        tambahBaris("Otoritas", cmbOtoritas);
        tambahBaris("No Telp", txtNoHp);
        selesaikan();
    }

    @Override
    protected List<User> muat() throws SQLException {
        return handler.semua();
    }

    @Override
    protected Object[] keBaris(User u) {
        return new Object[]{u.id, u.nama, u.email, u.otoritas, u.no_hp};
    }

    @Override
    protected void isiForm(User u) {
        txtNama.setText(u.nama);
        txtEmail.setText(u.email);
        cmbOtoritas.setSelectedItem(u.otoritas);
        txtNoHp.setText(u.no_hp);
    }

    @Override
    protected void bersihForm() {
        txtNama.setText("");
        txtEmail.setText("");
        cmbOtoritas.setSelectedIndex(0);
        txtNoHp.setText("");
    }

    @Override
    protected User bacaForm() {
        String nama = wajib("Nama", txtNama);
        String email = wajib("Email", txtEmail);
        cekEmail(email);
        String noHp = txtNoHp.getText().trim();
        if (!noHp.isEmpty() && !noHp.matches("\\d{10,13}")) {
            throw new IllegalArgumentException("No. Telp harus berupa 10 sampai 13 digit angka.");
        }
        return new User(nama, email, (String) cmbOtoritas.getSelectedItem(), noHp);
    }

    @Override
    protected int tambah(User baru) throws SQLException {
        return handler.tambah(baru);
    }

    @Override
    protected int ubah(User lama, User baru) throws SQLException {
        baru.id = lama.id;
        return handler.ubah(baru);
    }

    @Override
    protected int hapus(User u) throws SQLException {
        return handler.hapus(u.id);
    }
}
