package sem3matkul.pertemuan11.handlers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import sem3matkul.core.KoneksiDB;
import sem3matkul.pertemuan11.models.Transaksi;

public class TransaksiHandler {
    public int tambah(Transaksi t) throws SQLException {
        String sql = "INSERT INTO tbl_transaksi (no_tiket, plat_no, mobil, masuk_parkir, keluar_parkir, id_member) "
                + "VALUES (?,?,?,?,?,?)";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            isiKolom(ps, t);
            return ps.executeUpdate();
        }
    }

    public List<Transaksi> semua() throws SQLException {
        List<Transaksi> hasil = new ArrayList<>();
        String sql = "SELECT * FROM tbl_transaksi ORDER BY id";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Transaksi(
                        rs.getInt("id"),
                        rs.getString("no_tiket"),
                        rs.getString("plat_no"),
                        rs.getString("mobil"),
                        rs.getObject("masuk_parkir", LocalDateTime.class),
                        rs.getObject("keluar_parkir", LocalDateTime.class),
                        rs.getObject("id_member", Integer.class)));
            }
        }
        return hasil;
    }

    public int ubah(Transaksi t) throws SQLException {
        String sql = "UPDATE tbl_transaksi SET no_tiket = ?, plat_no = ?, mobil = ?, masuk_parkir = ?, "
                + "keluar_parkir = ?, id_member = ? WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            isiKolom(ps, t);
            ps.setInt(7, t.id);
            return ps.executeUpdate();
        }
    }

    public int hapus(int id) throws SQLException {
        String sql = "DELETE FROM tbl_transaksi WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private void isiKolom(PreparedStatement ps, Transaksi t) throws SQLException {
        ps.setString(1, t.no_tiket);
        ps.setString(2, t.plat_no);
        ps.setString(3, t.mobil);
        ps.setObject(4, t.masuk_parkir);
        ps.setObject(5, t.keluar_parkir, Types.TIMESTAMP);
        ps.setObject(6, t.id_member, Types.INTEGER);
    }
}
