package sem3matkul.templatepertemuan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sem3matkul.templatepertemuan.model.Contoh;

/**
 * Controller untuk tampilan GUI contoh (template pertemuan).
 * Mengatur interaksi pengguna, validasi input form, serta manipulasi data pada TableView.
 */
public class ContohController {

    @FXML private TextField tfId;
    @FXML private TextField tfNama;
    @FXML private ComboBox<String> cbKategori;
    @FXML private TextArea taDeskripsi;
    @FXML private Label lblStatusMessage;

    @FXML private TableView<Contoh> tableContoh;
    @FXML private TableColumn<Contoh, Integer> colId;
    @FXML private TableColumn<Contoh, String> colNama;
    @FXML private TableColumn<Contoh, String> colKategori;
    @FXML private TableColumn<Contoh, String> colDeskripsi;

    @FXML private Label lblTotalData;

    private final ObservableList<Contoh> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inisialisasi opsi ComboBox
        cbKategori.getItems().addAll("Dasar", "Lanjutan", "Praktik", "Tugas");

        // Binding kolom TableView ke properti Model Contoh
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));

        // Alignment kolom
        colId.setStyle("-fx-alignment: CENTER;");
        colKategori.setStyle("-fx-alignment: CENTER;");

        // Hubungkan list data dengan tabel
        tableContoh.setItems(dataList);

        // Data awal sebagai demonstrasi template
        dataList.add(new Contoh(1, "Sample Data 1", "Dasar", "Contoh data inisial untuk demonstrasi template"));
        dataList.add(new Contoh(2, "Sample Data 2", "Praktik", "Contoh data kedua"));

        updateSummary();
    }

    @FXML
    private void handleTambah() {
        String idText = tfId.getText() != null ? tfId.getText().trim() : "";
        String nama = tfNama.getText() != null ? tfNama.getText().trim() : "";
        String kategori = cbKategori.getValue();
        String deskripsi = taDeskripsi.getText() != null ? taDeskripsi.getText().trim() : "";

        if (idText.isEmpty() || nama.isEmpty()) {
            setStatus("⚠️ ID dan Nama Item wajib diisi!", false);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            setStatus("⚠️ ID harus berupa angka bulat valid!", false);
            return;
        }

        if (kategori == null || kategori.isEmpty()) {
            kategori = "Umum";
        }

        Contoh itemBaru = new Contoh(id, nama, kategori, deskripsi);
        dataList.add(itemBaru);

        handleResetForm();
        setStatus("✅ Data '" + nama + "' berhasil ditambahkan!", true);
        updateSummary();
    }

    @FXML
    private void handleHapus() {
        Contoh selectedItem = tableContoh.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            setStatus("⚠️ Pilih baris tabel yang ingin dihapus terlebih dahulu.", false);
            return;
        }

        dataList.remove(selectedItem);
        setStatus("🗑️ Data '" + selectedItem.getNama() + "' berhasil dihapus.", true);
        updateSummary();
    }

    @FXML
    private void handleResetForm() {
        tfId.clear();
        tfNama.clear();
        cbKategori.getSelectionModel().clearSelection();
        taDeskripsi.clear();
        lblStatusMessage.setText("");
    }

    private void updateSummary() {
        if (lblTotalData != null) {
            lblTotalData.setText(String.valueOf(dataList.size()));
        }
    }

    private void setStatus(String message, boolean isSuccess) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText(message);
            lblStatusMessage.setStyle(isSuccess ? "-fx-text-fill: #16a34a;" : "-fx-text-fill: #dc2626;");
        }
    }
}
