package sem3matkul.pertemuan10.model;

/** Pemetaan tabel buku(id, nama, penulis, tipe, genre, stok). */
public class Buku {
    private final int id;
    private final String nama;
    private final String penulis;
    private final String tipe;
    private final String genre;
    private final int stok;

    /** Untuk data baru: id dibuat otomatis oleh database. */
    public Buku(String nama, String penulis, String tipe, String genre, int stok) {
        this(0, nama, penulis, tipe, genre, stok);
    }

    public Buku(int id, String nama, String penulis, String tipe, String genre, int stok) {
        this.id = id;
        this.nama = nama;
        this.penulis = penulis;
        this.tipe = tipe;
        this.genre = genre;
        this.stok = stok;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getPenulis() { return penulis; }
    public String getTipe() { return tipe; }
    public String getGenre() { return genre; }
    public int getStok() { return stok; }
}
