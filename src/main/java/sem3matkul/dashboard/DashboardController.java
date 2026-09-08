package sem3matkul.dashboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import sem3matkul.core.WindowManager;

public class DashboardController {

    @FXML
    private void handleBukaPertemuan1() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan1/mahasiswa.fxml",
                "Pertemuan 1 - Data & Nilai Mahasiswa",
                1020,
                680
        );
    }

    @FXML
    private void handleBukaPertemuan2() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan2/thread.fxml",
                "Pertemuan 2 - Thread",
                1020,
                680
        );
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pertemuan 2");
        alert.setHeaderText("Modul Pertemuan 2 Siap Digunakan");
        alert.setContentText("Anda dapat menambahkan modul dan window baru untuk Pertemuan 2 pada package 'sem3matkul.pertemuan2'.");
        alert.showAndWait();*/
    }

    @FXML
    private void handleBukaPertemuan3() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pertemuan 3");
        alert.setHeaderText("Modul Pertemuan 3");
        alert.setContentText("Placeholder modul untuk materi Pertemuan 3.");
        alert.showAndWait();
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }
}
