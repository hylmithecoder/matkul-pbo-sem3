package sem3matkul.pertemuan11.models;

public class User {
    public int id;
    public String nama, email, otoritas, no_hp;

    public User() {
    }

    /** Untuk data baru: id dibuat otomatis oleh database. */
    public User(String nama, String email, String otoritas, String no_hp) {
        this.nama = nama;
        this.email = email;
        this.otoritas = otoritas;
        this.no_hp = no_hp;
    }

    /** Untuk data yang dibaca dari database. */
    public User(int id, String nama, String email, String otoritas, String no_hp) {
        this(nama, email, otoritas, no_hp);
        this.id = id;
    }
}
