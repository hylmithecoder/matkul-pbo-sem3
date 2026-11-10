package sem3matkul.pertemuan11.models;

public class Member {
    public int id;
    public String nama, email, mobil, plat_no;

    public Member() {
    }

    /** Untuk data baru: id dibuat otomatis oleh database. */
    public Member(String nama, String email, String mobil, String plat_no) {
        this.nama = nama;
        this.email = email;
        this.mobil = mobil;
        this.plat_no = plat_no;
    }

    /** Untuk data yang dibaca dari database. */
    public Member(int id, String nama, String email, String mobil, String plat_no) {
        this(nama, email, mobil, plat_no);
        this.id = id;
    }

    /** Teks yang tampil di pilihan member pada Form Transaksi. */
    @Override
    public String toString() {
        return id + " - " + nama + " (" + plat_no + ")";
    }
}
