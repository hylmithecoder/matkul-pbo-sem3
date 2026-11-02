package sem3matkul.pertemuan7.swing;

/**
 * POJO (Plain Old Java Object) hasil pemetaan dari tabel ANGGOTA
 * pada Logical Record Structure (LRS) Sistem Peminjaman Buku Perpustakaan.
 *
 * Tabel ANGGOTA(id_anggota*, nama, alamat, no_hp)
 */
public class Anggota {
    private String idAnggota;
    private String nama;
    private String alamat;
    private String noHp;

    public Anggota(String idAnggota, String nama, String alamat, String noHp) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.alamat = alamat;
        this.noHp = noHp;
    }

    // ---- Getter ----
    public String getIdAnggota() { return idAnggota; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNoHp() { return noHp; }

    // ---- Setter ----
    public void setNama(String nama) { this.nama = nama; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    @Override
    public String toString() {
        return "Anggota{" +
                "idAnggota='" + idAnggota + '\'' +
                ", nama='" + nama + '\'' +
                ", alamat='" + alamat + '\'' +
                ", noHp='" + noHp + '\'' +
                '}';
    }
}
