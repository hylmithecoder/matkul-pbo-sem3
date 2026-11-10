package sem3matkul.pertemuan11.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaksi {
    public static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public int id;
    public String no_tiket, plat_no, mobil;
    public LocalDateTime masuk_parkir = LocalDateTime.now(), keluar_parkir;
    /** null berarti kendaraan bukan milik member. */
    public Integer id_member;

    public Transaksi() {
    }

    /** Untuk data baru: id dibuat otomatis oleh database. */
    public Transaksi(String no_tiket, String plat_no, String mobil, LocalDateTime masuk_parkir,
                     LocalDateTime keluar_parkir, Integer id_member) {
        this.no_tiket = no_tiket;
        this.plat_no = plat_no;
        this.mobil = mobil;
        this.masuk_parkir = masuk_parkir;
        this.keluar_parkir = keluar_parkir;
        this.id_member = id_member;
    }

    /** Untuk data yang dibaca dari database. */
    public Transaksi(int id, String no_tiket, String plat_no, String mobil, LocalDateTime masuk_parkir,
                     LocalDateTime keluar_parkir, Integer id_member) {
        this(no_tiket, plat_no, mobil, masuk_parkir, keluar_parkir, id_member);
        this.id = id;
    }

    public String masukTeks() {
        return masuk_parkir.format(FORMAT_WAKTU);
    }

    public String keluarTeks() {
        return keluar_parkir == null ? "Masih parkir" : keluar_parkir.format(FORMAT_WAKTU);
    }

    public String memberTeks() {
        return id_member == null ? "-" : String.valueOf(id_member);
    }
}
