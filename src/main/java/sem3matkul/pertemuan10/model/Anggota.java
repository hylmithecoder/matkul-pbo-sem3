package sem3matkul.pertemuan10.model;

/** Pemetaan tabel anggota(id_anggota, nama, alamat, no_hp). */
public class Anggota {
    private final String idAnggota;
    private final String nama;
    private final String alamat;
    private final String noHp;

    public Anggota(String idAnggota, String nama, String alamat, String noHp) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.alamat = alamat;
        this.noHp = noHp;
    }

    public String getIdAnggota() { return idAnggota; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNoHp() { return noHp; }
}
