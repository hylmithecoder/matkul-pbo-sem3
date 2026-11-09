package sem3matkul.pertemuan10.koneksi;

import sem3matkul.pertemuan5.Database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Koneksi database Pertemuan 10.
 * Memakai konfigurasi yang sama dengan Pertemuan 5 (.env atau default lokal).
 */
public final class KoneksiDB {

    private static final String SKEMA_ANGGOTA = "/sem3matkul/pertemuan10/scheme/anggota.sql";

    private KoneksiDB() {
    }

    public static Connection getKoneksi() throws SQLException {
        return Database.connect();
    }

    /** Membuat tabel yang dibutuhkan bila belum ada, tanpa menghapus data lama. */
    public static void siapkanTabel() throws SQLException {
        Database.init(); // tabel buku berasal dari skema Pertemuan 5
        try (Connection conn = getKoneksi(); Statement st = conn.createStatement()) {
            st.execute(bacaSkemaAnggota());
        }
    }

    private static String bacaSkemaAnggota() throws SQLException {
        try (InputStream in = KoneksiDB.class.getResourceAsStream(SKEMA_ANGGOTA)) {
            if (in == null) {
                throw new SQLException("Skema tidak ditemukan: " + SKEMA_ANGGOTA);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Gagal membaca skema: " + SKEMA_ANGGOTA, e);
        }
    }
}
