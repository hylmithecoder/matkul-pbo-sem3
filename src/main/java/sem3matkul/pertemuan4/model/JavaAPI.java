package sem3matkul.pertemuan4.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Model JavaAPI untuk Pertemuan 4.
 * Mengenkapsulasi operasi dasar Java API:
 * 1. String.length() -> menghitung jumlah karakter
 * 2. String.toUpperCase() -> mengubah menjadi huruf kapital
 * 3. String.contains() -> memeriksa keberadaan kata kunci dalam kalimat
 * 4. LocalDate -> menangani tanggal saat program dijalankan
 */
public class JavaAPI {
    private String kalimat;
    private String kataCari;
    private final LocalDate tanggalEksekusi;

    public JavaAPI() {
        this.kalimat = "";
        this.kataCari = "";
        this.tanggalEksekusi = LocalDate.now();
    }

    public JavaAPI(String kalimat, String kataCari) {
        this.kalimat = kalimat != null ? kalimat : "";
        this.kataCari = kataCari != null ? kataCari : "";
        this.tanggalEksekusi = LocalDate.now();
    }

    /**
     * Menghitung total karakter dalam kalimat menggunakan method String.length().
     */
    public int getJumlahKarakter() {
        return kalimat.length();
    }

    /**
     * Mengubah seluruh kalimat menjadi huruf kapital menggunakan String.toUpperCase().
     */
    public String getKalimatKapital() {
        return kalimat.toUpperCase();
    }

    /**
     * Memeriksa apakah kalimat mengandung kata tertentu menggunakan String.contains() (Case-Sensitive).
     */
    public boolean mengandungKata() {
        if (kataCari == null || kataCari.isEmpty()) {
            return false;
        }
        return kalimat.contains(kataCari);
    }

    /**
     * Memeriksa apakah kalimat mengandung kata tertentu tanpa memperhatikan huruf besar/kecil (Case-Insensitive).
     */
    public boolean mengandungKataIgnoreCase() {
        if (kataCari == null || kataCari.isEmpty()) {
            return false;
        }
        return kalimat.toLowerCase().contains(kataCari.toLowerCase());
    }

    /**
     * Mengembalikan tanggal eksekusi sistem saat ini menggunakan LocalDate.
     */
    public LocalDate getTanggalEksekusi() {
        return tanggalEksekusi;
    }

    /**
     * Format tanggal ke format bahasa Indonesia, misalnya: "Senin, 28 September 2026".
     */
    public String getTanggalFormatted() {
        Locale indo = Locale.of("id", "ID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", indo);
        return tanggalEksekusi.format(formatter);
    }

    // Getter & Setter
    public String getKalimat() {
        return kalimat;
    }

    public void setKalimat(String kalimat) {
        this.kalimat = kalimat != null ? kalimat : "";
    }

    public String getKataCari() {
        return kataCari;
    }

    public void setKataCari(String kataCari) {
        this.kataCari = kataCari != null ? kataCari : "";
    }
}
