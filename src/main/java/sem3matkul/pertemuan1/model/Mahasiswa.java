package sem3matkul.pertemuan1.model;

/**
 * Model Data Mahasiswa untuk Pertemuan 1
 * Memuat informasi identitas, nilai MID, nilai UAS, serta kalkulasi nilai akhir dan grade.
 */
public class Mahasiswa {
    private String nim;
    private String nama;
    private double nilaiMid;
    private double nilaiUas;
    private double nilaiAkhir;
    private String grade;
    private String status;

    public Mahasiswa(String nim, String nama, double nilaiMid, double nilaiUas) {
        this.nim = nim;
        this.nama = nama;
        this.nilaiMid = nilaiMid;
        this.nilaiUas = nilaiUas;
        hitungNilaiDanGrade();
    }

    public void hitungNilaiDanGrade() {
        // Bobot nilai: 40% MID + 60% UAS
        this.nilaiAkhir = (this.nilaiMid * 0.4) + (this.nilaiUas * 0.6);

        if (this.nilaiAkhir >= 80.0) {
            this.grade = "A";
            this.status = "LULUS";
        } else if (this.nilaiAkhir >= 70.0) {
            this.grade = "B";
            this.status = "LULUS";
        } else if (this.nilaiAkhir >= 60.0) {
            this.grade = "C";
            this.status = "LULUS";
        } else if (this.nilaiAkhir >= 50.0) {
            this.grade = "D";
            this.status = "TIDAK LULUS";
        } else {
            this.grade = "E";
            this.status = "TIDAK LULUS";
        }
    }

    // Getters and Setters
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getNilaiMid() {
        return nilaiMid;
    }

    public void setNilaiMid(double nilaiMid) {
        this.nilaiMid = nilaiMid;
        hitungNilaiDanGrade();
    }

    public double getNilaiUas() {
        return nilaiUas;
    }

    public void setNilaiUas(double nilaiUas) {
        this.nilaiUas = nilaiUas;
        hitungNilaiDanGrade();
    }

    public double getNilaiAkhir() {
        return nilaiAkhir;
    }

    public String getGrade() {
        return grade;
    }

    public String getStatus() {
        return status;
    }
}
