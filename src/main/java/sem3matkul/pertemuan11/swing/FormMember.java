package sem3matkul.pertemuan11.swing;

import sem3matkul.pertemuan11.handlers.MemberHandler;
import sem3matkul.pertemuan11.models.Member;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class FormMember extends FormCrud<Member> {

    private final JTextField txtNama = new JTextField(28);
    private final JTextField txtEmail = new JTextField(28);
    private final JTextField txtMobil = new JTextField(28);
    private final JTextField txtPlat = new JTextField(28);

    private final MemberHandler handler = new MemberHandler();

    public FormMember() {
        this(() -> { });
    }

    public FormMember(Runnable setelahUbah) {
        super("Member", new String[]{"ID", "Nama", "Email", "Tipe Mobil", "Plat No"}, setelahUbah);
        tambahBaris("Nama", txtNama);
        tambahBaris("Email", txtEmail);
        tambahBaris("Tipe Mobil", txtMobil);
        tambahBaris("Plat No", txtPlat);
        selesaikan();
    }

    @Override
    protected List<Member> muat() throws SQLException {
        return handler.semua();
    }

    @Override
    protected Object[] keBaris(Member m) {
        return new Object[]{m.id, m.nama, m.email, m.mobil, m.plat_no};
    }

    @Override
    protected void isiForm(Member m) {
        txtNama.setText(m.nama);
        txtEmail.setText(m.email);
        txtMobil.setText(m.mobil);
        txtPlat.setText(m.plat_no);
    }

    @Override
    protected void bersihForm() {
        txtNama.setText("");
        txtEmail.setText("");
        txtMobil.setText("");
        txtPlat.setText("");
    }

    @Override
    protected Member bacaForm() {
        String nama = wajib("Nama", txtNama);
        String email = txtEmail.getText().trim();
        if (!email.isEmpty()) {
            cekEmail(email);
        }
        String mobil = wajib("Tipe Mobil", txtMobil);
        String plat = wajib("Plat No", txtPlat).toUpperCase();
        return new Member(nama, email, mobil, plat);
    }

    @Override
    protected int tambah(Member baru) throws SQLException {
        return handler.tambah(baru);
    }

    @Override
    protected int ubah(Member lama, Member baru) throws SQLException {
        baru.id = lama.id;
        return handler.ubah(baru);
    }

    @Override
    protected int hapus(Member m) throws SQLException {
        return handler.hapus(m.id);
    }
}
