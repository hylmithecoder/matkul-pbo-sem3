package sem3matkul.pertemuan10.dao;

import sem3matkul.pertemuan10.koneksi.KoneksiDB;
import sem3matkul.pertemuan10.model.Anggota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnggotaDAO {

    public boolean cariById(String kataKunci) throws SQLException {
        boolean hasil = false;
        String sql = "SELECT * FROM anggota WHERE id_anggota = ?";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kataKunci);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil = true;
                }
            }
        }
        return hasil;
    }

    public int tambah(Anggota a) throws SQLException {
        String sql = "INSERT INTO anggota (id_anggota, nama, alamat, no_hp) VALUES (?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getIdAnggota());
            ps.setString(2, a.getNama());
            ps.setString(3, a.getAlamat());
            ps.setString(4, a.getNoHp());
            return ps.executeUpdate();
        }
    }

    public List<Anggota> semua() throws SQLException {
        List<Anggota> hasil = new ArrayList<>();
        String sql = "SELECT * FROM anggota ORDER BY id_anggota";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Anggota(
                        rs.getString("id_anggota"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("no_hp")));
            }
        }
        return hasil;
    }
}
