package sem3matkul.pertemuan10.dao;

import sem3matkul.pertemuan10.koneksi.KoneksiDB;
import sem3matkul.pertemuan10.model.Buku;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BukuDAO {

    /** ID buku dibuat otomatis, jadi duplikat dicek dari judul dan penulis. */
    public boolean cariByJudulPenulis(String nama, String penulis) throws SQLException {
        String sql = "SELECT id FROM buku WHERE nama = ? AND penulis = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, penulis);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int tambah(Buku b) throws SQLException {
        String sql = "INSERT INTO buku (nama, penulis, tipe, genre, stok) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getNama());
            ps.setString(2, b.getPenulis());
            ps.setString(3, b.getTipe());
            ps.setString(4, b.getGenre());
            ps.setInt(5, b.getStok());
            return ps.executeUpdate();
        }
    }

    public List<Buku> semua() throws SQLException {
        List<Buku> hasil = new ArrayList<>();
        String sql = "SELECT * FROM buku ORDER BY id";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Buku(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("penulis"),
                        rs.getString("tipe"),
                        rs.getString("genre"),
                        rs.getInt("stok")));
            }
        }
        return hasil;
    }
}
