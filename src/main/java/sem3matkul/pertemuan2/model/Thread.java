package sem3matkul.pertemuan2.model;

/**
 * Model Data Thread Class untuk Pertemuan 1
 * Memuat informasi identitas, nilai MID, nilai UAS, serta kalkulasi nilai akhir dan grade.
 */
public class Thread {
    public enum Prioritas { TINGGI, NORMAL, RENDAH }

    private int no;
    private String nim;
    private String nama;
    private Prioritas prioritas;
    private long urutanMasuk; // untuk stable sort dalam prioritas yang sama
    private static long counterMasuk = 0;

    private double nilaiMid;
    private double nilaiUas;
    private double nilaiAkhir;
    private String grade;
    private String status;

    public Thread(String nama) {
        this(nama, Prioritas.NORMAL);
    }

    public Thread(String nama, Prioritas prioritas) {
        this.nama = nama;
        this.prioritas = prioritas != null ? prioritas : Prioritas.NORMAL;
        this.urutanMasuk = ++counterMasuk;
        System.out.println("Nama Thread baru: "+this.nama+" | Prioritas: "+this.prioritas+" | urutan: "+this.urutanMasuk);
    }

    /** Cycle TINGGI -> NORMAL -> RENDAH -> TINGGI */
    public void cyclePrioritas() {
        switch (this.prioritas) {
            case TINGGI -> this.prioritas = Prioritas.NORMAL;
            case NORMAL -> this.prioritas = Prioritas.RENDAH;
            case RENDAH -> this.prioritas = Prioritas.TINGGI;
        }
    }

    /** Rank untuk sorting: TINGGI=0, NORMAL=1, RENDAH=2 */
    public int getRankPrioritas() {
        return switch (this.prioritas) {
            case TINGGI -> 0;
            case NORMAL -> 1;
            case RENDAH -> 2;
        };
    }

    /** Mapping ke java.lang.Thread priority sebagai demo PBO Thread */
    public int toJavaThreadPriority() {
        return switch (this.prioritas) {
            case TINGGI -> java.lang.Thread.MAX_PRIORITY; // 10
            case NORMAL -> java.lang.Thread.NORM_PRIORITY; // 5
            case RENDAH -> java.lang.Thread.MIN_PRIORITY; // 1
        };
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
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Prioritas getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(Prioritas prioritas) {
        this.prioritas = prioritas;
    }

    // Untuk PropertyValueFactory tetap butuh String
    public String getPrioritasString() {
        return prioritas.name();
    }

    public long getUrutanMasuk() {
        return urutanMasuk;
    }

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
