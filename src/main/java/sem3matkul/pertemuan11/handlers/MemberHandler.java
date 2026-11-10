package sem3matkul.pertemuan11.handlers;

import java.sql.*;
import java.util.*;

import sem3matkul.core.KoneksiDB;
import sem3matkul.pertemuan11.models.Member;

public class MemberHandler {
    public int tambah(Member m) throws SQLException {
        String sql = "INSERT INTO tbl_member (nama, email, mobil, plat_nomor) VALUES (?,?,?,?)";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.nama);
            ps.setString(2, m.email);
            ps.setString(3, m.mobil);
            ps.setString(4, m.plat_no);
            return ps.executeUpdate();
        }
    }

    public List<Member> semua() throws SQLException {
        List<Member> hasil = new ArrayList<>();
        String sql = "SELECT * FROM tbl_member ORDER BY id";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Member(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("mobil"),
                        rs.getString("plat_nomor")));
            }
        }
        return hasil;
    }

    public int ubah(Member m) throws SQLException {
        String sql = "UPDATE tbl_member SET nama = ?, email = ?, mobil = ?, plat_nomor = ? WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.nama);
            ps.setString(2, m.email);
            ps.setString(3, m.mobil);
            ps.setString(4, m.plat_no);
            ps.setInt(5, m.id);
            return ps.executeUpdate();
        }
    }

    public int hapus(int id) throws SQLException {
        String sql = "DELETE FROM tbl_member WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
