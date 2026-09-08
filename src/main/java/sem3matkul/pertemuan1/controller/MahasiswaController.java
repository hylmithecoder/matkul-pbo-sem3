package sem3matkul.pertemuan1.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sem3matkul.pertemuan1.model.Mahasiswa;

public class MahasiswaController {

    @FXML private TextField tfNim;
    @FXML private TextField tfNama;
    @FXML private TextField tfNilaiMid;
    @FXML private TextField tfNilaiUas;

    @FXML private Label lblStatusMessage;
    @FXML private Label lblTotalMahasiswa;
    @FXML private Label lblRataRata;
    @FXML private Label lblPersenLulus;

    @FXML private TableView<Mahasiswa> tableMahasiswa;
    @FXML private TableColumn<Mahasiswa, String> colNim;
    @FXML private TableColumn<Mahasiswa, String> colNama;
    @FXML private TableColumn<Mahasiswa, Double> colNilaiMid;
    @FXML private TableColumn<Mahasiswa, Double> colNilaiUas;
    @FXML private TableColumn<Mahasiswa, Double> colNilaiAkhir;
    @FXML private TableColumn<Mahasiswa, String> colGrade;
    @FXML private TableColumn<Mahasiswa, String> colStatus;

    private final ObservableList<Mahasiswa> listMahasiswa = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Property Value Factories
        colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNilaiMid.setCellValueFactory(new PropertyValueFactory<>("nilaiMid"));
        colNilaiUas.setCellValueFactory(new PropertyValueFactory<>("nilaiUas"));
        colNilaiAkhir.setCellValueFactory(new PropertyValueFactory<>("nilaiAkhir"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Alignment kolom tabel
        colNim.setStyle("-fx-alignment: CENTER;");
        colNilaiMid.setStyle("-fx-alignment: CENTER;");
        colNilaiUas.setStyle("-fx-alignment: CENTER;");
        colNilaiAkhir.setStyle("-fx-alignment: CENTER;");
        colGrade.setStyle("-fx-alignment: CENTER;");
        colStatus.setStyle("-fx-alignment: CENTER;");

        // Format angka desimal pada kolom Nilai Akhir
        colNilaiAkhir.setCellFactory(col -> new TableCell<Mahasiswa, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });

        // Format badge warna status (Lulus / Tidak Lulus)
        colStatus.setCellFactory(col -> new TableCell<Mahasiswa, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("LULUS".equalsIgnoreCase(status)) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #15803d; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #b91c1c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableMahasiswa.setItems(listMahasiswa);

        // Data awal sebagai contoh
        listMahasiswa.add(new Mahasiswa("230001", "Muhammad Hylmi", 85.0, 90.0));
        listMahasiswa.add(new Mahasiswa("230002", "Budi Santoso", 65.0, 75.0));
        listMahasiswa.add(new Mahasiswa("230003", "Siti Rahma", 45.0, 50.0));

        updateStatistics();
    }

    @FXML
    private void handleTambah() {
        String nim = tfNim.getText().trim();
        String nama = tfNama.getText().trim();
        String strMid = tfNilaiMid.getText().trim();
        String strUas = tfNilaiUas.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || strMid.isEmpty() || strUas.isEmpty()) {
            showStatus("⚠️ Harap lengkapi semua kolom form!", true);
            return;
        }

        try {
            double nilaiMid = Double.parseDouble(strMid);
            double nilaiUas = Double.parseDouble(strUas);

            if (nilaiMid < 0 || nilaiMid > 100 || nilaiUas < 0 || nilaiUas > 100) {
                showStatus("⚠️ Nilai harus berada dalam rentang 0 - 100!", true);
                return;
            }

            Mahasiswa mhs = new Mahasiswa(nim, nama, nilaiMid, nilaiUas);
            listMahasiswa.add(mhs);

            handleResetForm();
            showStatus("✅ Data mahasiswa berhasil ditambahkan!", false);
            updateStatistics();
        } catch (NumberFormatException e) {
            showStatus("⚠️ Nilai MID dan UAS harus berupa angka valid!", true);
        }
    }

    @FXML
    private void handleHapus() {
        Mahasiswa selected = tableMahasiswa.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("⚠️ Pilih baris mahasiswa yang ingin dihapus terlebih dahulu!", true);
            return;
        }

        listMahasiswa.remove(selected);
        showStatus("🗑️ Data mahasiswa berhasil dihapus.", false);
        updateStatistics();
    }

    @FXML
    private void handleResetForm() {
        tfNim.clear();
        tfNama.clear();
        tfNilaiMid.clear();
        tfNilaiUas.clear();
        tfNim.requestFocus();
    }

    private void showStatus(String message, boolean isError) {
        lblStatusMessage.setText(message);
        if (isError) {
            lblStatusMessage.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblStatusMessage.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    private void updateStatistics() {
        int total = listMahasiswa.size();
        lblTotalMahasiswa.setText(String.valueOf(total));

        if (total == 0) {
            lblRataRata.setText("0.00");
            lblPersenLulus.setText("0%");
            return;
        }

        double sum = 0;
        int lulusCount = 0;
        for (Mahasiswa m : listMahasiswa) {
            sum += m.getNilaiAkhir();
            if ("LULUS".equalsIgnoreCase(m.getStatus())) {
                lulusCount++;
            }
        }

        double rata = sum / total;
        double persenLulus = ((double) lulusCount / total) * 100;

        lblRataRata.setText(String.format("%.2f", rata));
        lblPersenLulus.setText(String.format("%.1f%%", persenLulus));
    }
}
