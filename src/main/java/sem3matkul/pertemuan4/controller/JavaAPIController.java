package sem3matkul.pertemuan4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sem3matkul.pertemuan4.model.JavaAPI;

/**
 * Controller untuk modul Pertemuan 4: Java API.
 * Menghubungkan antarmuka GUI (api.fxml) dengan operasi class String & LocalDate.
 */
public class JavaAPIController {

    @FXML private TextArea taInputKalimat;
    @FXML private TextField tfKataKunci;
    @FXML private CheckBox cbIgnoreCase;
    @FXML private Label lblStatusMessage;

    @FXML private Label lblTanggal;
    @FXML private Label lblJumlahKarakter;
    @FXML private Label lblHasilContains;
    @FXML private TextArea taKalimatKapital;

    @FXML
    public void initialize() {
        // Tampilkan tanggal sistem saat ini sejak modul pertama kali dibuka
        JavaAPI initModel = new JavaAPI();
        lblTanggal.setText(initModel.getTanggalFormatted() + " (" + initModel.getTanggalEksekusi() + ")");

        // Contoh kalimat awal untuk kemudahan pengujian
        taInputKalimat.setText("Pemrograman Berorientasi Objek dengan bahasa pemrograman Java.");
        tfKataKunci.setText("Java");

        // Jalankan pemrosesan awal
        handleProses();
    }

    @FXML
    private void handleProses() {
        String kalimat = taInputKalimat.getText() != null ? taInputKalimat.getText().trim() : "";
        String kataKunci = tfKataKunci.getText() != null ? tfKataKunci.getText().trim() : "";

        if (kalimat.isEmpty()) {
            lblStatusMessage.setText("⚠️ Silakan masukkan kalimat terlebih dahulu!");
            lblStatusMessage.setStyle("-fx-text-fill: #dc2626;");
            return;
        }

        JavaAPI apiModel = new JavaAPI(kalimat, kataKunci);

        // 1. Tanggal eksekusi program (LocalDate)
        lblTanggal.setText(apiModel.getTanggalFormatted() + " (" + apiModel.getTanggalEksekusi() + ")");

        // 2. Jumlah karakter (String.length())
        int totalKarakter = apiModel.getJumlahKarakter();
        lblJumlahKarakter.setText(totalKarakter + " karakter (termasuk spasi)");

        // 3. Mengubah menjadi huruf kapital (String.toUpperCase())
        taKalimatKapital.setText(apiModel.getKalimatKapital());

        // 4. Memeriksa keberadaan kata tertentu (String.contains())
        if (kataKunci.isEmpty()) {
            lblHasilContains.setText("⚠️ Kata kunci pencarian kosong. Masukkan kata untuk menguji contains().");
            lblHasilContains.setStyle("-fx-text-fill: #64748b;");
        } else {
            boolean ditemukan = (cbIgnoreCase != null && cbIgnoreCase.isSelected())
                    ? apiModel.mengandungKataIgnoreCase()
                    : apiModel.mengandungKata();

            if (ditemukan) {
                lblHasilContains.setText("✅ DITEMUKAN! Kalimat mengandung kata \"" + kataKunci + "\" (contains == true)");
                lblHasilContains.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            } else {
                lblHasilContains.setText("❌ TIDAK DITEMUKAN! Kalimat tidak mengandung kata \"" + kataKunci + "\" (contains == false)");
                lblHasilContains.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            }
        }

        lblStatusMessage.setText("✅ Kalimat berhasil dianalisis dengan Java API!");
        lblStatusMessage.setStyle("-fx-text-fill: #16a34a;");
    }

    @FXML
    private void handleReset() {
        taInputKalimat.clear();
        tfKataKunci.clear();
        lblJumlahKarakter.setText("0 karakter");
        taKalimatKapital.clear();
        lblHasilContains.setText("Belum diproses (masukkan kata lalu klik proses)");
        lblHasilContains.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        lblStatusMessage.setText("");
    }
}
