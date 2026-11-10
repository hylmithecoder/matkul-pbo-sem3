package sem3matkul.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Koneksi database bersama, memakai konfigurasi dari {@link Database}. */
public final class KoneksiDB {

    private KoneksiDB() {
    }

    public static Connection getKoneksi() throws SQLException {
        return Database.connect();
    }

    /**
     * Menjalankan berkas skema dari resources, tanpa menghapus data lama.
     * Setiap perintah di berkas skema dipisahkan titik koma.
     */
    public static void siapkanTabel(String resourceSkema) throws SQLException {
        try (Connection conn = getKoneksi(); Statement st = conn.createStatement()) {
            for (String perintah : bacaSkema(resourceSkema).split(";")) {
                if (!perintah.isBlank()) {
                    st.execute(perintah);
                }
            }
        }
    }

    private static String bacaSkema(String resourceSkema) throws SQLException {
        try (InputStream in = KoneksiDB.class.getResourceAsStream(resourceSkema)) {
            if (in == null) {
                throw new SQLException("Skema tidak ditemukan: " + resourceSkema);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Gagal membaca skema: " + resourceSkema, e);
        }
    }
}
