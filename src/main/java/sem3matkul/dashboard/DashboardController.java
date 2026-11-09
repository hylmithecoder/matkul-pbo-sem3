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
    private void handleBukaPertemuan4() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan4/api.fxml",
                "Pertemuan 4 - Java API (String & LocalDate)",
                1020,
                680
        );
    }

    @FXML
    private void handleBukaPertemuan5() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan5/perpustakaan.fxml",
                "Pertemuan 5 - Sistem Perpustakaan & Database MySQL",
                1080,
                720
        );
    }

    @FXML
    private void handleBukaPertemuan7() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan7/swing.fxml",
                "Pertemuan 7 - Pemrograman GUI dengan Java Swing",
                1020,
                680
        );
    }

    @FXML
    private void handleBukaPertemuan10() {
        WindowManager.openWindow(
                "/sem3matkul/pertemuan10/input_data.fxml",
                "Pertemuan 10 - Input Data ke Database",
                1020,
                680
        );
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
        System.exit(0);
    }
}
