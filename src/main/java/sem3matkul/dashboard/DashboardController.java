package sem3matkul.dashboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import sem3matkul.core.PDFRenderer;
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
        // openPDF("Pert 1.pdf");
    }

    @FXML
    private void handleBukaPertemuan2() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan2/thread.fxml",
                "Pertemuan 2 - Thread",
                1020,
                680
        );
        PDFRenderer.openPDF("Pert 2 (Thread) Praktik.pdf");
    }

    @FXML
    private void handleBukaPertemuan3() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan3/collection.fxml",
                "Pertemuan 3 - Java Collections Framework",
                1020,
                680
        );
        PDFRenderer.openPDF("Pert 3(Collection) Praktik.pdf");
        PDFRenderer.openPDF("Pert 3(Collection Lanj) Praktik.pdf");
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }
}
