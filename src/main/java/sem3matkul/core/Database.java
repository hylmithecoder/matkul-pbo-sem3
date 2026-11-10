package sem3matkul.core;

import io.github.cdimascio.dotenv.Dotenv;
import sem3matkul.pertemuan5.DbInitializer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi Database MariaDB / MySQL untuk Pertemuan 5.
 * Mendukung konfigurasi via .env dan fallback ke konfigurasi lokal default.
 */
public class Database {

    private static String url;
    private static String user;
    private static String pass;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception ignored) {
        }

        String dbHost = getEnvOrDefault(dotenv, "DB_HOST", "localhost");
        String dbPort = getEnvOrDefault(dotenv, "DB_PORT", "3306");
        String dbName = getEnvOrDefault(dotenv, "DB_NAME", "perpustakaan");

        user = getEnvOrDefault(dotenv, "DB_USER", "root");
        pass = getEnvOrDefault(dotenv, "DB_PASS", "");

        String customUrl = getEnvOrDefault(dotenv, "DB_URL", null);
        if (customUrl != null && !customUrl.isBlank()) {
            url = customUrl;
        } else {
            url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta",
                    dbHost, dbPort, dbName);
        }
    }

    private static String getEnvOrDefault(Dotenv dotenv, String key, String defaultValue) {
        if (dotenv != null) {
            String val = dotenv.get(key);
            if (val != null && !val.isBlank()) {
                return val;
            }
        }
        String sysVal = System.getenv(key);
        if (sysVal != null && !sysVal.isBlank()) {
            return sysVal;
        }
        return defaultValue;
    }

    /**
     * Membuka koneksi baru ke database.
     */
    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver (com.mysql.cj.jdbc.Driver) tidak ditemukan", e);
        }
        return DriverManager.getConnection(url, user, pass);
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    /**
     * Helper cepat untuk menginisialisasi tabel skema.
     */
    public static void init() throws SQLException {
        DbInitializer.initDatabase();
    }

    /**
     * Helper cepat untuk mereset dan membuat ulang tabel skema perpustakaan.
     */
    public static void rescheme(boolean seedDummyData) throws SQLException {
        DbInitializer.rescheme(seedDummyData);
    }

    public static void main(String[] args) {
        System.out.println("[Database Test Runner]");
        try (Connection conn = connect()) {
            System.out.println("✅ Terhubung ke database: " + url);
            DbInitializer.initDatabase();
            DbInitializer.printTableStatus();
        } catch (SQLException e) {
            System.err.println("❌ Kesalahan database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
