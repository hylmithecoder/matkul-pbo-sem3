package sem3matkul.pertemuan11.handlers;

import java.sql.*;
import java.util.*;

import sem3matkul.core.KoneksiDB;
import sem3matkul.pertemuan11.models.User;

public class UserHandler {
    public int tambah(User usr) throws SQLException {
        String sql = "INSERT INTO tbl_user (nama, email, otoritas, no_hp) VALUES (?,?,?,?)";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usr.nama);
            ps.setString(2, usr.email);
            ps.setString(3, usr.otoritas);
            ps.setString(4, usr.no_hp);
            return ps.executeUpdate();
        }
    }

    public List<User> semua() throws SQLException {
        List<User> hasil = new ArrayList<>();
        String sql = "SELECT * FROM tbl_user ORDER BY id";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new User(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("otoritas"),
                        rs.getString("no_hp")));
            }
        }
        return hasil;
    }

    public int ubah(User usr) throws SQLException {
        String sql = "UPDATE tbl_user SET nama = ?, email = ?, otoritas = ?, no_hp = ? WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usr.nama);
            ps.setString(2, usr.email);
            ps.setString(3, usr.otoritas);
            ps.setString(4, usr.no_hp);
            ps.setInt(5, usr.id);
            return ps.executeUpdate();
        }
    }

    public int hapus(int id) throws SQLException {
        String sql = "DELETE FROM tbl_user WHERE id = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
