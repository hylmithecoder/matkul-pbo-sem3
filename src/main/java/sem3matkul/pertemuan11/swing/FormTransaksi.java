package sem3matkul.pertemuan11.swing;

import sem3matkul.pertemuan11.handlers.MemberHandler;
import sem3matkul.pertemuan11.handlers.TransaksiHandler;
import sem3matkul.pertemuan11.models.Member;
import sem3matkul.pertemuan11.models.Transaksi;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormTransaksi extends FormCrud<Transaksi> {

    private static final DateTimeFormatter FORMAT_TIKET = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final Member BUKAN_MEMBER = new Member() {
        @Override
        public String toString() {
            return "Bukan member";
        }
    };

    private final JTextField txtNoTiket = new JTextField(28);
    private final JTextField txtPlat = new JTextField(28);
    private final JTextField txtMobil = new JTextField(28);
    private final JTextField txtMasuk = new JTextField(28);
    private final JTextField txtKeluar = new JTextField(28);
    private final JComboBox<Member> cmbMember = new JComboBox<>();

    private final TransaksiHandler handler = new TransaksiHandler();
    private final MemberHandler memberHandler = new MemberHandler();

    public FormTransaksi() {
        this(() -> { });
    }

    public FormTransaksi(Runnable setelahUbah) {
        super("Transaksi", new String[]{"ID", "No Tiket", "Plat No", "Mobil", "Masuk", "Keluar", "ID Member"},
                setelahUbah);
        tambahBaris("No Tiket", txtNoTiket);
        tambahBaris("Plat No", txtPlat);
        tambahBaris("Mobil", txtMobil);
        tambahBaris("Masuk (yyyy-MM-dd HH:mm)", txtMasuk);
        tambahBaris("Keluar (kosong = masih parkir)", txtKeluar);
        tambahBaris("Member", cmbMember);
        muatMember();
        selesaikan();

        // Member bisa berubah dari Form Member saat jendela ini masih terbuka.
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                muatMember();
            }
        });
    }

    private void muatMember() {
        Member dipilih = (Member) cmbMember.getSelectedItem();
        cmbMember.removeAllItems();
        cmbMember.addItem(BUKAN_MEMBER);
        try {
            for (Member m : memberHandler.semua()) {
                cmbMember.addItem(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormTransaksi.class.getName()).log(Level.WARNING, "Gagal memuat daftar member", ex);
        }
        pilihMember(dipilih == null ? null : dipilih.id);
    }

    private void pilihMember(Integer id) {
        cmbMember.setSelectedIndex(0);
        for (int i = 1; id != null && i < cmbMember.getItemCount(); i++) {
            if (cmbMember.getItemAt(i).id == id) {
                cmbMember.setSelectedIndex(i);
            }
        }
    }

    @Override
    protected List<Transaksi> muat() throws SQLException {
        return handler.semua();
    }

    @Override
    protected Object[] keBaris(Transaksi t) {
        return new Object[]{t.id, t.no_tiket, t.plat_no, t.mobil, t.masukTeks(), t.keluarTeks(), t.memberTeks()};
    }

    @Override
    protected void isiForm(Transaksi t) {
        txtNoTiket.setText(t.no_tiket);
        txtPlat.setText(t.plat_no);
        txtMobil.setText(t.mobil);
        txtMasuk.setText(t.masukTeks());
        txtKeluar.setText(t.keluar_parkir == null ? "" : t.keluarTeks());
        pilihMember(t.id_member);
    }

    @Override
    protected void bersihForm() {
        LocalDateTime sekarang = LocalDateTime.now();
        txtNoTiket.setText("TKT-" + sekarang.format(FORMAT_TIKET));
        txtPlat.setText("");
        txtMobil.setText("");
        txtMasuk.setText(sekarang.format(Transaksi.FORMAT_WAKTU));
        txtKeluar.setText("");
        pilihMember(null);
    }

    @Override
    protected Transaksi bacaForm() {
        String noTiket = wajib("No Tiket", txtNoTiket);
        String plat = wajib("Plat No", txtPlat).toUpperCase();
        String mobil = wajib("Mobil", txtMobil);
        LocalDateTime masuk = bacaWaktu("Waktu masuk", wajib("Waktu masuk", txtMasuk));
        String teksKeluar = txtKeluar.getText().trim();
        LocalDateTime keluar = teksKeluar.isEmpty() ? null : bacaWaktu("Waktu keluar", teksKeluar);
        if (keluar != null && keluar.isBefore(masuk)) {
            throw new IllegalArgumentException("Waktu keluar tidak boleh sebelum waktu masuk.");
        }
        Member member = (Member) cmbMember.getSelectedItem();
        Integer idMember = member == null || member == BUKAN_MEMBER ? null : member.id;
        return new Transaksi(noTiket, plat, mobil, masuk, keluar, idMember);
    }

    private static LocalDateTime bacaWaktu(String label, String teks) {
        try {
            return LocalDateTime.parse(teks, Transaksi.FORMAT_WAKTU);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(label + " harus berformat yyyy-MM-dd HH:mm.");
        }
    }

    @Override
    protected int tambah(Transaksi baru) throws SQLException {
        return handler.tambah(baru);
    }

    @Override
    protected int ubah(Transaksi lama, Transaksi baru) throws SQLException {
        baru.id = lama.id;
        return handler.ubah(baru);
    }

    @Override
    protected int hapus(Transaksi t) throws SQLException {
        return handler.hapus(t.id);
    }
}
