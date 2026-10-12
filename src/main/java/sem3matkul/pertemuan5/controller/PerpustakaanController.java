package sem3matkul.pertemuan5.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sem3matkul.pertemuan5.Database;
import sem3matkul.pertemuan5.DbInitializer;
import sem3matkul.pertemuan5.model.Buku;
import sem3matkul.pertemuan5.model.Dipinjam;
import sem3matkul.pertemuan5.model.Petugas;
import sem3matkul.pertemuan5.model.Siswa;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class PerpustakaanController {

    // ==========================================
    // TAB BUKU
    // ==========================================
    @FXML private TableView<Buku> tableBuku;
    @FXML private TableColumn<Buku, Integer> colBukuId;
    @FXML private TableColumn<Buku, String> colBukuNama;
    @FXML private TableColumn<Buku, String> colBukuPenulis;
    @FXML private TableColumn<Buku, String> colBukuTipe;
    @FXML private TableColumn<Buku, String> colBukuGenre;
    @FXML private TableColumn<Buku, Integer> colBukuStok;

    @FXML private TextField tfBukuNama;
    @FXML private TextField tfBukuPenulis;
    @FXML private ComboBox<String> cbBukuTipe;
    @FXML private TextField tfBukuGenre;
    @FXML private Spinner<Integer> spBukuStok;
    @FXML private TextField tfSearchBuku;
    @FXML private Label lblBukuStatus;

    private final ObservableList<Buku> listBuku = FXCollections.observableArrayList();
    private FilteredList<Buku> filteredBuku;
    private Buku selectedBuku = null;

    // ==========================================
    // TAB SISWA
    // ==========================================
    @FXML private TableView<Siswa> tableSiswa;
    @FXML private TableColumn<Siswa, Integer> colSiswaId;
    @FXML private TableColumn<Siswa, String> colSiswanim;
    @FXML private TableColumn<Siswa, String> colSiswaNama;
    @FXML private TableColumn<Siswa, String> colSiswaKelas;
    @FXML private TableColumn<Siswa, String> colSiswaJurusan;

    @FXML private TextField tfSiswanim;
    @FXML private TextField tfSiswaNama;
    @FXML private TextField tfSiswaKelas;
    @FXML private TextField tfSiswaJurusan;
    @FXML private TextField tfSearchSiswa;
    @FXML private Label lblSiswaStatus;

    private final ObservableList<Siswa> listSiswa = FXCollections.observableArrayList();
    private FilteredList<Siswa> filteredSiswa;
    private Siswa selectedSiswa = null;

    // ==========================================
    // TAB PEMINJAMAN
    // ==========================================
    @FXML private TableView<Dipinjam> tablePinjam;
    @FXML private TableColumn<Dipinjam, Integer> colPinjamId;
    @FXML private TableColumn<Dipinjam, String> colPinjamSiswa;
    @FXML private TableColumn<Dipinjam, String> colPinjamBuku;
    @FXML private TableColumn<Dipinjam, String> colPinjamPetugas;
    @FXML private TableColumn<Dipinjam, LocalDate> colPinjamTglPinjam;
    @FXML private TableColumn<Dipinjam, LocalDate> colPinjamTglJatuhTempo;
    @FXML private TableColumn<Dipinjam, LocalDate> colPinjamTglKembali;
    @FXML private TableColumn<Dipinjam, String> colPinjamStatus;
    @FXML private TableColumn<Dipinjam, String> colPinjamDenda;

    @FXML private ComboBox<Siswa> cbPinjamSiswa;
    @FXML private ComboBox<Buku> cbPinjamBuku;
    @FXML private ComboBox<Petugas> cbPinjamPetugas;
    @FXML private DatePicker dpPinjamTglPinjam;
    @FXML private DatePicker dpPinjamTglJatuhTempo;
    @FXML private ComboBox<String> cbFilterPinjamStatus;
    @FXML private Label lblPinjamStatus;

    private final ObservableList<Dipinjam> listPinjam = FXCollections.observableArrayList();
    private FilteredList<Dipinjam> filteredPinjam;
    private Dipinjam selectedPinjam = null;

    // ==========================================
    // TAB DATABASE & PETUGAS
    // ==========================================
    @FXML private TableView<Petugas> tablePetugas;
    @FXML private TableColumn<Petugas, Integer> colPetugasId;
    @FXML private TableColumn<Petugas, String> colPetugasNama;
    @FXML private TableColumn<Petugas, String> colPetugasUsername;
    @FXML private TableColumn<Petugas, String> colPetugasCreatedAt;

    @FXML private TextField tfPetugasNama;
    @FXML private TextField tfPetugasUsername;
    @FXML private PasswordField pfPetugasPassword;
    @FXML private Label lblDbStatus;
    @FXML private Label lblDbUrl;

    private final ObservableList<Petugas> listPetugas = FXCollections.observableArrayList();
    private Petugas selectedPetugas = null;

    // Biaya denda per hari keterlambatan
    private static final double DENDA_PER_HARI = 1000.0;

    @FXML
    public void initialize() {
        setupBukuTab();
        setupSiswaTab();
        setupPinjamTab();
        setupDatabaseTab();

        // Muat data awal dari database
        refreshAllData();
    }

    // ==========================================
    // SETUP METODE
    // ==========================================
    private void setupBukuTab() {
        colBukuId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBukuNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colBukuPenulis.setCellValueFactory(new PropertyValueFactory<>("penulis"));
        colBukuTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colBukuGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colBukuStok.setCellValueFactory(new PropertyValueFactory<>("stok"));

        cbBukuTipe.getItems().addAll("Buku Ajar", "Referensi", "Novel", "Komik", "Sastra", "Ensiklopedia", "Majalah");
        spBukuStok.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        filteredBuku = new FilteredList<>(listBuku, p -> true);
        tableBuku.setItems(filteredBuku);

        tfSearchBuku.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredBuku.setPredicate(buku -> {
                if (newVal == null || newVal.isBlank()) return true;
                String query = newVal.toLowerCase();
                return (buku.getNama() != null && buku.getNama().toLowerCase().contains(query)) ||
                        (buku.getPenulis() != null && buku.getPenulis().toLowerCase().contains(query)) ||
                        (buku.getGenre() != null && buku.getGenre().toLowerCase().contains(query));
            });
        });

        tableBuku.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBuku = newVal;
            if (newVal != null) {
                tfBukuNama.setText(newVal.getNama());
                tfBukuPenulis.setText(newVal.getPenulis());
                cbBukuTipe.setValue(newVal.getTipe());
                tfBukuGenre.setText(newVal.getGenre());
                spBukuStok.getValueFactory().setValue(newVal.getStok());
            }
        });
    }

    private void setupSiswaTab() {
        colSiswaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSiswanim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        colSiswaNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colSiswaKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        colSiswaJurusan.setCellValueFactory(new PropertyValueFactory<>("jurusan"));

        filteredSiswa = new FilteredList<>(listSiswa, p -> true);
        tableSiswa.setItems(filteredSiswa);

        tfSearchSiswa.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredSiswa.setPredicate(siswa -> {
                if (newVal == null || newVal.isBlank()) return true;
                String query = newVal.toLowerCase();
                return (siswa.getNama() != null && siswa.getNama().toLowerCase().contains(query)) ||
                        (siswa.getnim() != null && siswa.getnim().toLowerCase().contains(query)) ||
                        (siswa.getKelas() != null && siswa.getKelas().toLowerCase().contains(query));
            });
        });

        tableSiswa.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedSiswa = newVal;
            if (newVal != null) {
                tfSiswanim.setText(newVal.getnim());
                tfSiswaNama.setText(newVal.getNama());
                tfSiswaKelas.setText(newVal.getKelas());
                tfSiswaJurusan.setText(newVal.getJurusan());
            }
        });
    }

    private void setupPinjamTab() {
        colPinjamId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPinjamSiswa.setCellValueFactory(new PropertyValueFactory<>("namaSiswa"));
        colPinjamBuku.setCellValueFactory(new PropertyValueFactory<>("namaBuku"));
        colPinjamPetugas.setCellValueFactory(new PropertyValueFactory<>("namaPetugas"));
        colPinjamTglPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colPinjamTglJatuhTempo.setCellValueFactory(new PropertyValueFactory<>("tanggalJatuhTempo"));
        colPinjamTglKembali.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        colPinjamStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPinjamDenda.setCellValueFactory(new PropertyValueFactory<>("dendaFormat"));

        dpPinjamTglPinjam.setValue(LocalDate.now());
        dpPinjamTglJatuhTempo.setValue(LocalDate.now().plusDays(7));

        cbFilterPinjamStatus.getItems().addAll("Semua", "DIPINJAM", "KEMBALI");
        cbFilterPinjamStatus.setValue("Semua");

        filteredPinjam = new FilteredList<>(listPinjam, p -> true);
        tablePinjam.setItems(filteredPinjam);

        cbFilterPinjamStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredPinjam.setPredicate(item -> {
                if (newVal == null || newVal.equals("Semua")) return true;
                return item.getStatus().equalsIgnoreCase(newVal);
            });
        });

        tablePinjam.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPinjam = newVal;
        });
    }

    private void setupDatabaseTab() {
        colPetugasId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPetugasNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colPetugasUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPetugasCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        tablePetugas.setItems(listPetugas);
        tablePetugas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPetugas = newVal;
            if (newVal != null) {
                tfPetugasNama.setText(newVal.getNama());
                tfPetugasUsername.setText(newVal.getUsername());
            }
        });

        lblDbUrl.setText("URL: " + Database.getUrl());
    }

    // ==========================================
    // REFRESH DATA DARI DATABASE
    // ==========================================
    public void refreshAllData() {
        loadBuku();
        loadSiswa();
        loadPetugas();
        loadPeminjaman();
        updateComboBoxes();
        updateDbStatus();
    }

    private void loadBuku() {
        listBuku.clear();
        String sql = "SELECT * FROM buku ORDER BY id DESC";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listBuku.add(new Buku(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("penulis"),
                        rs.getString("tipe"),
                        rs.getString("genre"),
                        rs.getInt("stok")
                ));
            }
        } catch (SQLException e) {
            showError("Gagal Memuat Buku", e.getMessage());
        }
    }

    private void loadSiswa() {
        listSiswa.clear();
        String sql = "SELECT * FROM siswa ORDER BY id DESC";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listSiswa.add(new Siswa(
                        rs.getInt("id"),
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("kelas"),
                        rs.getString("jurusan")
                ));
            }
        } catch (SQLException e) {
            showError("Gagal Memuat Siswa", e.getMessage());
        }
    }

    private void loadPetugas() {
        listPetugas.clear();
        String sql = "SELECT * FROM petugas ORDER BY id DESC";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listPetugas.add(new Petugas(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            showError("Gagal Memuat Petugas", e.getMessage());
        }
    }

    private void loadPeminjaman() {
        listPinjam.clear();
        String sql = "SELECT p.*, s.nama AS nama_siswa, s.nim AS nim_siswa, " +
                "b.nama AS nama_buku, pt.nama AS nama_petugas " +
                "FROM dipinjam p " +
                "JOIN siswa s ON p.id_siswa = s.id " +
                "JOIN buku b ON p.id_buku = b.id " +
                "LEFT JOIN petugas pt ON p.id_petugas = pt.id " +
                "ORDER BY p.id DESC";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Date tglPinjam = rs.getDate("tanggal_pinjam");
                Date tglTempo = rs.getDate("tanggal_jatuh_tempo");
                Date tglKembali = rs.getDate("tanggal_kembali");

                listPinjam.add(new Dipinjam(
                        rs.getInt("id"),
                        rs.getInt("id_siswa"),
                        rs.getString("nama_siswa"),
                        rs.getString("nim_siswa"),
                        rs.getInt("id_buku"),
                        rs.getString("nama_buku"),
                        rs.getObject("id_petugas") != null ? rs.getInt("id_petugas") : null,
                        rs.getString("nama_petugas") != null ? rs.getString("nama_petugas") : "-",
                        tglPinjam != null ? tglPinjam.toLocalDate() : null,
                        tglTempo != null ? tglTempo.toLocalDate() : null,
                        tglKembali != null ? tglKembali.toLocalDate() : null,
                        rs.getString("status"),
                        rs.getDouble("denda")
                ));
            }
        } catch (SQLException e) {
            showError("Gagal Memuat Transaksi Peminjaman", e.getMessage());
        }
    }

    private void updateComboBoxes() {
        cbPinjamSiswa.setItems(listSiswa);
        // Hanya tampilkan buku yang stoknya masih tersedia
        ObservableList<Buku> bukuTersedia = FXCollections.observableArrayList();
        for (Buku b : listBuku) {
            if (b.getStok() > 0) {
                bukuTersedia.add(b);
            }
        }
        cbPinjamBuku.setItems(bukuTersedia);
        cbPinjamPetugas.setItems(listPetugas);
    }

    private void updateDbStatus() {
        try (Connection conn = Database.connect()) {
            lblDbStatus.setText("🟢 Terhubung ke MariaDB / MySQL (" + (listBuku.size() + listSiswa.size() + listPinjam.size() + listPetugas.size()) + " total entri)");
            lblDbStatus.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        } catch (SQLException e) {
            lblDbStatus.setText("🔴 Gagal terhubung: " + e.getMessage());
            lblDbStatus.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        }
    }

    // ==========================================
    // AKSI CRUD: BUKU
    // ==========================================
    @FXML
    private void handleTambahBuku() {
        String nama = tfBukuNama.getText().trim();
        String penulis = tfBukuPenulis.getText().trim();
        String tipe = cbBukuTipe.getValue();
        String genre = tfBukuGenre.getText().trim();
        int stok = spBukuStok.getValue();

        if (nama.isEmpty()) {
            showWarning("Validasi", "Judul/Nama buku tidak boleh kosong!");
            return;
        }

        String sql = "INSERT INTO buku (nama, penulis, tipe, genre, stok) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, penulis);
            ps.setString(3, tipe != null ? tipe : "Umum");
            ps.setString(4, genre);
            ps.setInt(5, stok);
            ps.executeUpdate();

            showInfo("Sukses", "Buku berhasil ditambahkan.");
            handleResetBuku();
            loadBuku();
            updateComboBoxes();
        } catch (SQLException e) {
            showError("Gagal Tambah Buku", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateBuku() {
        if (selectedBuku == null) {
            showWarning("Peringatan", "Pilih buku dari tabel terlebih dahulu!");
            return;
        }

        String nama = tfBukuNama.getText().trim();
        String penulis = tfBukuPenulis.getText().trim();
        String tipe = cbBukuTipe.getValue();
        String genre = tfBukuGenre.getText().trim();
        int stok = spBukuStok.getValue();

        if (nama.isEmpty()) {
            showWarning("Validasi", "Judul/Nama buku tidak boleh kosong!");
            return;
        }

        String sql = "UPDATE buku SET nama = ?, penulis = ?, tipe = ?, genre = ?, stok = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, penulis);
            ps.setString(3, tipe != null ? tipe : "Umum");
            ps.setString(4, genre);
            ps.setInt(5, stok);
            ps.setInt(6, selectedBuku.getId());
            ps.executeUpdate();

            showInfo("Sukses", "Data buku berhasil diperbarui.");
            handleResetBuku();
            loadBuku();
            updateComboBoxes();
        } catch (SQLException e) {
            showError("Gagal Update Buku", e.getMessage());
        }
    }

    @FXML
    private void handleHapusBuku() {
        if (selectedBuku == null) {
            showWarning("Peringatan", "Pilih buku dari tabel terlebih dahulu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus buku: " + selectedBuku.getNama() + "?");
        alert.setContentText("Catatan: Riwayat peminjaman terkait buku ini juga akan terhapus.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM buku WHERE id = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedBuku.getId());
                ps.executeUpdate();

                showInfo("Sukses", "Buku berhasil dihapus.");
                handleResetBuku();
                loadBuku();
                loadPeminjaman();
                updateComboBoxes();
            } catch (SQLException e) {
                showError("Gagal Hapus Buku", e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetBuku() {
        selectedBuku = null;
        tfBukuNama.clear();
        tfBukuPenulis.clear();
        cbBukuTipe.setValue(null);
        tfBukuGenre.clear();
        spBukuStok.getValueFactory().setValue(1);
        tableBuku.getSelectionModel().clearSelection();
    }

    // ==========================================
    // AKSI CRUD: SISWA
    // ==========================================
    @FXML
    private void handleTambahSiswa() {
        String nim = tfSiswanim.getText().trim();
        String nama = tfSiswaNama.getText().trim();
        String kelas = tfSiswaKelas.getText().trim();
        String jurusan = tfSiswaJurusan.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || kelas.isEmpty()) {
            showWarning("Validasi", "nim, Nama, dan Kelas wajib diisi!");
            return;
        }

        String sql = "INSERT INTO siswa (nim, nama, kelas, jurusan) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            ps.setString(2, nama);
            ps.setString(3, kelas);
            ps.setString(4, jurusan);
            ps.executeUpdate();

            showInfo("Sukses", "Data siswa berhasil ditambahkan.");
            handleResetSiswa();
            loadSiswa();
            updateComboBoxes();
        } catch (SQLException e) {
            showError("Gagal Tambah Siswa", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateSiswa() {
        if (selectedSiswa == null) {
            showWarning("Peringatan", "Pilih siswa dari tabel terlebih dahulu!");
            return;
        }

        String nim = tfSiswanim.getText().trim();
        String nama = tfSiswaNama.getText().trim();
        String kelas = tfSiswaKelas.getText().trim();
        String jurusan = tfSiswaJurusan.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || kelas.isEmpty()) {
            showWarning("Validasi", "nim, Nama, dan Kelas wajib diisi!");
            return;
        }

        String sql = "UPDATE siswa SET nim = ?, nama = ?, kelas = ?, jurusan = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            ps.setString(2, nama);
            ps.setString(3, kelas);
            ps.setString(4, jurusan);
            ps.setInt(5, selectedSiswa.getId());
            ps.executeUpdate();

            showInfo("Sukses", "Data siswa berhasil diperbarui.");
            handleResetSiswa();
            loadSiswa();
            loadPeminjaman();
            updateComboBoxes();
        } catch (SQLException e) {
            showError("Gagal Update Siswa", e.getMessage());
        }
    }

    @FXML
    private void handleHapusSiswa() {
        if (selectedSiswa == null) {
            showWarning("Peringatan", "Pilih siswa dari tabel terlebih dahulu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus siswa: " + selectedSiswa.getNama() + " (" + selectedSiswa.getnim() + ")?");
        alert.setContentText("Catatan: Riwayat peminjaman siswa ini juga akan ikut terhapus.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM siswa WHERE id = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedSiswa.getId());
                ps.executeUpdate();

                showInfo("Sukses", "Data siswa berhasil dihapus.");
                handleResetSiswa();
                loadSiswa();
                loadPeminjaman();
                updateComboBoxes();
            } catch (SQLException e) {
                showError("Gagal Hapus Siswa", e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetSiswa() {
        selectedSiswa = null;
        tfSiswanim.clear();
        tfSiswaNama.clear();
        tfSiswaKelas.clear();
        tfSiswaJurusan.clear();
        tableSiswa.getSelectionModel().clearSelection();
    }

    // ==========================================
    // AKSI TRANSAKSI: PEMINJAMAN & PENGEMBALIAN
    // ==========================================
    @FXML
    private void handleCatatPinjam() {
        Siswa siswa = cbPinjamSiswa.getValue();
        Buku buku = cbPinjamBuku.getValue();
        Petugas petugas = cbPinjamPetugas.getValue();
        LocalDate tglPinjam = dpPinjamTglPinjam.getValue();
        LocalDate tglTempo = dpPinjamTglJatuhTempo.getValue();

        if (siswa == null || buku == null || tglPinjam == null || tglTempo == null) {
            showWarning("Validasi", "Pilih Siswa, Buku, serta tentukan Tanggal Pinjam & Jatuh Tempo!");
            return;
        }

        if (tglTempo.isBefore(tglPinjam)) {
            showWarning("Validasi", "Tanggal jatuh tempo tidak boleh sebelum tanggal pinjam!");
            return;
        }

        if (buku.getStok() <= 0) {
            showWarning("Stok Habis", "Buku '" + buku.getNama() + "' saat ini stoknya sedang habis!");
            return;
        }

        String insertPinjam = "INSERT INTO dipinjam (id_siswa, id_buku, id_petugas, tanggal_pinjam, tanggal_jatuh_tempo, status, denda) " +
                "VALUES (?, ?, ?, ?, ?, 'DIPINJAM', 0.00)";
        String kurangiStok = "UPDATE buku SET stok = stok - 1 WHERE id = ?";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertPinjam);
                 PreparedStatement ps2 = conn.prepareStatement(kurangiStok)) {

                ps1.setInt(1, siswa.getId());
                ps1.setInt(2, buku.getId());
                if (petugas != null) {
                    ps1.setInt(3, petugas.getId());
                } else {
                    ps1.setNull(3, Types.INTEGER);
                }
                ps1.setDate(4, Date.valueOf(tglPinjam));
                ps1.setDate(5, Date.valueOf(tglTempo));
                ps1.executeUpdate();

                ps2.setInt(1, buku.getId());
                ps2.executeUpdate();

                conn.commit();
                showInfo("Peminjaman Dicatat", "Peminjaman buku '" + buku.getNama() + "' untuk " + siswa.getNama() + " berhasil dicatat.");

                loadPeminjaman();
                loadBuku();
                updateComboBoxes();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError("Gagal Transaksi Peminjaman", e.getMessage());
        }
    }

    @FXML
    private void handleKembalikanBuku() {
        if (selectedPinjam == null) {
            showWarning("Peringatan", "Pilih transaksi peminjaman yang ingin dikembalikan dari tabel!");
            return;
        }

        if ("KEMBALI".equalsIgnoreCase(selectedPinjam.getStatus())) {
            showWarning("Sudah Kembali", "Transaksi ini sudah berstatus KEMBALI pada " + selectedPinjam.getTanggalKembali() + ".");
            return;
        }

        LocalDate hariIni = LocalDate.now();
        LocalDate tglTempo = selectedPinjam.getTanggalJatuhTempo();
        double denda = 0.0;

        if (hariIni.isAfter(tglTempo)) {
            long hariTerlambat = ChronoUnit.DAYS.between(tglTempo, hariIni);
            denda = hariTerlambat * DENDA_PER_HARI;
        }

        String updatePinjam = "UPDATE dipinjam SET status = 'KEMBALI', tanggal_kembali = ?, denda = ? WHERE id = ?";
        String tambahStok = "UPDATE buku SET stok = stok + 1 WHERE id = ?";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updatePinjam);
                 PreparedStatement ps2 = conn.prepareStatement(tambahStok)) {

                ps1.setDate(1, Date.valueOf(hariIni));
                ps1.setDouble(2, denda);
                ps1.setInt(3, selectedPinjam.getId());
                ps1.executeUpdate();

                ps2.setInt(1, selectedPinjam.getIdBuku());
                ps2.executeUpdate();

                conn.commit();

                String infoDenda = denda > 0
                        ? String.format("\n⚠️ Keterlambatan terdeteksi! Denda: Rp %,.0f", denda)
                        : "\nPengembalian tepat waktu (tanpa denda).";

                showInfo("Buku Dikembalikan", "Buku '" + selectedPinjam.getNamaBuku() + "' berhasil dikembalikan!" + infoDenda);

                loadPeminjaman();
                loadBuku();
                updateComboBoxes();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError("Gagal Pengembalian Buku", e.getMessage());
        }
    }

    @FXML
    private void handleHapusPeminjaman() {
        if (selectedPinjam == null) {
            showWarning("Peringatan", "Pilih transaksi peminjaman dari tabel terlebih dahulu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus catatan peminjaman ID #" + selectedPinjam.getId() + "?");
        alert.setContentText("Tindakan ini hanya menghapus log riwayat peminjaman.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM dipinjam WHERE id = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedPinjam.getId());
                ps.executeUpdate();

                showInfo("Sukses", "Data peminjaman berhasil dihapus.");
                loadPeminjaman();
            } catch (SQLException e) {
                showError("Gagal Hapus Peminjaman", e.getMessage());
            }
        }
    }

    // ==========================================
    // AKSI TAB: DATABASE & PETUGAS
    // ==========================================
    @FXML
    private void handleTambahPetugas() {
        String nama = tfPetugasNama.getText().trim();
        String username = tfPetugasUsername.getText().trim();
        String password = pfPetugasPassword.getText().trim();

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showWarning("Validasi", "Nama, Username, dan Password petugas wajib diisi!");
            return;
        }

        String sql = "INSERT INTO petugas (nama, username, password) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();

            showInfo("Sukses", "Petugas baru berhasil ditambahkan.");
            handleResetPetugas();
            loadPetugas();
            updateComboBoxes();
        } catch (SQLException e) {
            showError("Gagal Tambah Petugas", e.getMessage());
        }
    }

    @FXML
    private void handleHapusPetugas() {
        if (selectedPetugas == null) {
            showWarning("Peringatan", "Pilih petugas dari tabel terlebih dahulu!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus petugas: " + selectedPetugas.getNama() + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM petugas WHERE id = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedPetugas.getId());
                ps.executeUpdate();

                showInfo("Sukses", "Petugas berhasil dihapus.");
                handleResetPetugas();
                loadPetugas();
                updateComboBoxes();
            } catch (SQLException e) {
                showError("Gagal Hapus Petugas", e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetPetugas() {
        selectedPetugas = null;
        tfPetugasNama.clear();
        tfPetugasUsername.clear();
        pfPetugasPassword.clear();
        tablePetugas.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleReschemeDatabase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Re-Scheme Database");
        alert.setHeaderText("PERINGATAN: Re-Scheme Database!");
        alert.setContentText("Seluruh tabel perpustakaan (dipinjam, buku, siswa, petugas) akan di-DROP dan dibuat ulang dari skrip SQL, kemudian diisi data awal dummy.\n\nApakah kamu yakin ingin melanjutkan?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DbInitializer.rescheme(true);
                showInfo("Re-Scheme Berhasil", "Database perpustakaan berhasil di-reset dan diisi data seed dummy awal!");
                refreshAllData();
            } catch (SQLException e) {
                showError("Gagal Re-Scheme", e.getMessage());
            }
        }
    }

    @FXML
    private void handleInitDatabase() {
        try {
            DbInitializer.initDatabase();
            showInfo("Inimialisasi Berhasil", "Tabel-tabel skema telah dipastikan terbuat (CREATE TABLE IF NOT EXISTS).");
            refreshAllData();
        } catch (SQLException e) {
            showError("Gagal Inimialisasi", e.getMessage());
        }
    }

    // ==========================================
    // ALERT DIALOG HELPERS
    // ==========================================
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
