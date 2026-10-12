package sem3matkul.pertemuan5.model;

import java.time.LocalDate;

public class Dipinjam {
    private int id;
    private int idSiswa;
    private String namaSiswa;
    private String nimSiswa;
    private int idBuku;
    private String namaBuku;
    private Integer idPetugas;
    private String namaPetugas;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private String status;
    private double denda;

    public Dipinjam() {
    }

    public Dipinjam(int id, int idSiswa, String namaSiswa, String nimSiswa,
                    int idBuku, String namaBuku, Integer idPetugas, String namaPetugas,
                    LocalDate tanggalPinjam, LocalDate tanggalJatuhTempo, LocalDate tanggalKembali,
                    String status, double denda) {
        this.id = id;
        this.idSiswa = idSiswa;
        this.namaSiswa = namaSiswa;
        this.nimSiswa = nimSiswa;
        this.idBuku = idBuku;
        this.namaBuku = namaBuku;
        this.idPetugas = idPetugas;
        this.namaPetugas = namaPetugas;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.denda = denda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(int idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }

    public String getNimSiswa() {
        return nimSiswa;
    }

    public void setNimSiswa(String nimSiswa) {
        this.nimSiswa = nimSiswa;
    }

    public String getnimSiswa() {
        return nimSiswa;
    }

    public void setnimSiswa(String nimSiswa) {
        this.nimSiswa = nimSiswa;
    }

    public int getIdBuku() {
        return idBuku;
    }

    public void setIdBuku(int idBuku) {
        this.idBuku = idBuku;
    }

    public String getNamaBuku() {
        return namaBuku;
    }

    public void setNamaBuku(String namaBuku) {
        this.namaBuku = namaBuku;
    }

    public Integer getIdPetugas() {
        return idPetugas;
    }

    public void setIdPetugas(Integer idPetugas) {
        this.idPetugas = idPetugas;
    }

    public String getNamaPetugas() {
        return namaPetugas;
    }

    public void setNamaPetugas(String namaPetugas) {
        this.namaPetugas = namaPetugas;
    }

    public LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(LocalDate tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public void setTanggalJatuhTempo(LocalDate tanggalJatuhTempo) {
        this.tanggalJatuhTempo = tanggalJatuhTempo;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDenda() {
        return denda;
    }

    public void setDenda(double denda) {
        this.denda = denda;
    }

    public String getDendaFormat() {
        return String.format("Rp %,.0f", denda);
    }
}
