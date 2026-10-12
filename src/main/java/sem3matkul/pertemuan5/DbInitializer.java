package sem3matkul.pertemuan5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DbInitializer - Penanggung jawab inisialisasi dan rescheme skema database perpustakaan.
 * Membaca DDL dari berkas SQL (petugas.sql, siswa.sql, buku.sql, dipinjam.sql)
 * serta menyediakan data seeding awal.
 */
public class DbInitializer {

    private static final String[] SCHEMA_SCRIPTS = {
            "petugas.sql",
            "siswa.sql",
            "buku.sql",
            "dipinjam.sql"
    };

    private static final String RESOURCE_BASE_PATH = "/sem3matkul/pertemuan5/scheme/";
    private static final String[] FS_BASE_PATHS = {
            "src/main/resources/sem3matkul/pertemuan5/scheme/",
            "src/main/java/sem3matkul/pertemuan5/scheme/"
    };

    /**
     * Inisialisasi skema database (CREATE TABLE IF NOT EXISTS).
     * Aman dipanggil berkali-kali tanpa menghapus data yang ada.
     */
    public static void initDatabase() throws SQLException {
        System.out.println("==================================================");
        System.out.println(" [DbInitializer] Memulai Inisialisasi Database...");
        System.out.println("==================================================");

        try (Connection conn = Database.connect()) {
            for (String scriptName : SCHEMA_SCRIPTS) {
                System.out.printf("-> Mengeksekusi skema: %s ... ", scriptName);
                String sqlContent = readScriptContent(scriptName);
                executeSqlStatements(conn, sqlContent);
                System.out.println("BERHASIL");
            }
            System.out.println("\n[DbInitializer] Semua tabel berhasil diinisialisasi!\n");
        }
    }

    /**
     * Melakukan rescheme penuh:
     * 1. Men-drop semua tabel perpustakaan secara aman (nonaktifkan FK checks).
     * 2. Membuat ulang tabel dari berkas SQL.
     * 3. (Opsional) Mengisi data awal/dummy (seeding).
     */
    public static void rescheme(boolean seedInitialData) throws SQLException {
        System.out.println("==================================================");
        System.out.println(" [DbInitializer] Memulai RESCHEME Database...");
        System.out.println("==================================================");

        try (Connection conn = Database.connect()) {
            // Drop tabel terkait perpustakaan
            System.out.println("-> Membersihkan tabel lama (DROP TABLES)...");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                stmt.execute("DROP TABLE IF EXISTS dipinjam;");
                stmt.execute("DROP TABLE IF EXISTS buku;");
                stmt.execute("DROP TABLE IF EXISTS siswa;");
                stmt.execute("DROP TABLE IF EXISTS petugas;");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }
            System.out.println("-> Tabel lama berhasil dibersihkan.");

            // Eksekusi DDL baru
            for (String scriptName : SCHEMA_SCRIPTS) {
                System.out.printf("-> Membuat ulang skema: %s ... ", scriptName);
                String sqlContent = readScriptContent(scriptName);
                executeSqlStatements(conn, sqlContent);
                System.out.println("BERHASIL");
            }

            // Seeding data dummy
            if (seedInitialData) {
                System.out.println("-> Memasukkan seed data awal (dummy perpustakaan)...");
                seedData(conn);
                System.out.println("-> Seed data berhasil dimasukkan.");
            }

            System.out.println("\n[DbInitializer] Rescheme selesai dengan sukses!\n");
        }
    }

    /**
     * Memasukkan data awal (dummy/sample) ke dalam tabel.
     */
    public static void seedData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Petugas
            stmt.executeUpdate("INSERT INTO petugas (nama, username, password) VALUES " +
                    "('Administrator Perpustakaan', 'admin', 'admin123'), " +
                    "('Siti Rahmawati', 'petugas1', 'petugas123');");

            // Siswa
            stmt.executeUpdate("INSERT INTO siswa (nim, nama, kelas, jurusan) VALUES " +
                    "('2024001', 'Ahmad Dani', 'XII RPL 1', 'Rekayasa Perangkat Lunak'), " +
                    "('2024002', 'Budi Santoso', 'XI TKJ 2', 'Teknik Komputer dan Jaringan'), " +
                    "('2024003', 'Citra Lestari', 'X MM 1', 'Multimedia');");

            // Buku
            stmt.executeUpdate("INSERT INTO buku (nama, penulis, tipe, genre, stok) VALUES " +
                    "('Pemrograman Berorientasi Objek dengan Java', 'Dr. Indrajit', 'Buku Ajar', 'Teknologi Informasi', 5), " +
                    "('Struktur Data & Algoritma Modern', 'Rinaldi Munir', 'Referensi', 'Ilmu Komputer', 3), " +
                    "('Laskar Pelangi', 'Andrea Hirata', 'Novel', 'Fiksi Sastra', 4), " +
                    "('Bumi Manusia', 'Pramoedya Ananta Toer', 'Sastra', 'Fiksi Sejarah', 2);");

            // Dipinjam
            stmt.executeUpdate("INSERT INTO dipinjam (id_siswa, id_buku, id_petugas, tanggal_pinjam, tanggal_jatuh_tempo, tanggal_kembali, status, denda) VALUES " +
                    "(1, 1, 1, CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 4 DAY, NULL, 'DIPINJAM', 0.00), " +
                    "(2, 3, 2, CURDATE() - INTERVAL 10 DAY, CURDATE() - INTERVAL 3 DAY, CURDATE() - INTERVAL 1 DAY, 'KEMBALI', 2000.00);");
        }
    }

    /**
     * Menampilkan rangkuman status dan jumlah baris masing-masing tabel.
     */
    public static void printTableStatus() throws SQLException {
        System.out.println("==================================================");
        System.out.println(" STATUS TABEL PERPUSTAKAAN (perpustakaan)");
        System.out.println("==================================================");

        String[] tables = {"petugas", "siswa", "buku", "dipinjam"};
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + table)) {
                    if (rs.next()) {
                        System.out.printf(" - Tabel %-12s : %3d baris data\n", table, rs.getInt("total"));
                    }
                } catch (SQLException e) {
                    System.out.printf(" - Tabel %-12s : BELUM DIBUAT (%s)\n", table, e.getMessage());
                }
            }
        }
        System.out.println("==================================================\n");
    }

    /**
     * Membaca isi berkas SQL dari classpath resource atau filesystem fallback.
     */
    private static String readScriptContent(String scriptName) {
        // 1. Coba baca dari classpath resource
        String resourcePath = RESOURCE_BASE_PATH + scriptName;
        try (InputStream in = DbInitializer.class.getResourceAsStream(resourcePath)) {
            if (in != null) {
                return readFromInputStream(in);
            }
        } catch (Exception ignored) {
        }

        // 2. Fallback baca langsung dari sistem berkas lokal
        for (String basePath : FS_BASE_PATHS) {
            File file = new File(basePath + scriptName);
            if (file.exists() && file.isFile()) {
                try (InputStream in = new FileInputStream(file)) {
                    return readFromInputStream(in);
                } catch (Exception ignored) {
                }
            }
        }

        throw new RuntimeException("Berkas SQL tidak ditemukan: " + scriptName +
                " (sudah dicari di classpath " + resourcePath + " dan filesystem)");
    }

    private static String readFromInputStream(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Memecah konten skrip SQL berdasarkan titik koma (;) dan mengeksekusinya.
     */
    private static void executeSqlStatements(Connection conn, String sqlContent) throws SQLException {
        List<String> statements = splitStatements(sqlContent);
        try (Statement stmt = conn.createStatement()) {
            for (String query : statements) {
                if (!query.isBlank()) {
                    stmt.execute(query);
                }
            }
        }
    }

    private static List<String> splitStatements(String sqlContent) {
        List<String> list = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        String[] lines = sqlContent.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();
            // Abaikan komentar baris tunggal
            if (trimmed.startsWith("--") || trimmed.startsWith("#")) {
                continue;
            }
            // Hapus trailing komentar bila ada
            int commentIdx = line.indexOf("--");
            if (commentIdx >= 0) {
                line = line.substring(0, commentIdx);
            }

            current.append(line).append(" ");

            if (line.contains(";")) {
                String[] parts = current.toString().split(";");
                for (int i = 0; i < parts.length - 1; i++) {
                    String statement = parts[i].trim();
                    if (!statement.isEmpty()) {
                        list.add(statement);
                    }
                }
                current = new StringBuilder(parts[parts.length - 1]);
            }
        }

        String remaining = current.toString().trim();
        if (!remaining.isEmpty()) {
            list.add(remaining);
        }

        return list;
    }

    /**
     * Entry point mandiri jika ingin menjalankan test/rescheme langsung via CLI.
     */
    public static void main(String[] args) {
        try {
            System.out.println("[DbInitializer Runner]");
            rescheme(true);
            printTableStatus();
        } catch (Exception e) {
            System.err.println("Gagal menjalankan DbInitializer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
