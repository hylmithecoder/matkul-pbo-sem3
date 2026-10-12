package sem3matkul.pertemuan5.model;

public class Petugas {
    private int id;
    private String nama;
    private String username;
    private String password;
    private String createdAt;

    public Petugas() {
    }

    public Petugas(int id, String nama, String username, String password, String createdAt) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return nama + " (" + username + ")";
    }
}
