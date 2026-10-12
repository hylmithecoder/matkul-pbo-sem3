package sem3matkul.pertemuan5.model;

public class Buku {
    private int id;
    private String nama;
    private String penulis;
    private String tipe;
    private String genre;
    private int stok;

    public Buku() {
    }

    public Buku(int id, String nama, String penulis, String tipe, String genre, int stok) {
        this.id = id;
        this.nama = nama;
        this.penulis = penulis;
        this.tipe = tipe;
        this.genre = genre;
        this.stok = stok;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nama + " (Sisa: " + stok + ")";
    }
}
