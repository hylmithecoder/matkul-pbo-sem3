CREATE TABLE IF NOT EXISTS dipinjam (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_siswa INT NOT NULL,
    id_buku INT NOT NULL,
    id_petugas INT,
    tanggal_pinjam DATE NOT NULL,
    tanggal_jatuh_tempo DATE NOT NULL,
    tanggal_kembali DATE NULL,
    status ENUM('DIPINJAM', 'KEMBALI') NOT NULL DEFAULT 'DIPINJAM',
    denda DECIMAL(10,2) DEFAULT 0.00,
    CONSTRAINT fk_dipinjam_siswa FOREIGN KEY (id_siswa) REFERENCES siswa(id) ON DELETE CASCADE,
    CONSTRAINT fk_dipinjam_buku FOREIGN KEY (id_buku) REFERENCES buku(id) ON DELETE CASCADE,
    CONSTRAINT fk_dipinjam_petugas FOREIGN KEY (id_petugas) REFERENCES petugas(id) ON DELETE SET NULL
);
