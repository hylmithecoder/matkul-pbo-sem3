package sem3matkul;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import sem3matkul.core.WindowManager;
import sem3matkul.core.CliHelper;
import sem3matkul.core.CliHelper.Log;
import java.util.*;
import java.io.IOException;
import java.net.URL;

/**
 * Entry point utama aplikasi JavaFX sem3matkul.
 * Memuat Dashboard utama yang menjadi pusat navigasi ke setiap jendela pertemuan/materi.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlLocation = getClass().getResource("/sem3matkul/dashboard.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("FXML Dashboard tidak ditemukan di /sem3matkul/dashboard.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root, 880, 600);

        URL cssLocation = getClass().getResource("/sem3matkul/style.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        primaryStage.setTitle("Dashboard Perkuliahan PBO - Semester 3");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(500);

        // Daftarkan dashboard stage ke WindowManager agar bisa disembunyikan saat modul dibuka
        WindowManager.setDashboardStage(primaryStage);

        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    public static void main(String[] args) {
        // Tanya mode via CLI saat gradle run (sesuai request: pilih GUI atau CLI)
        Log.println("=== Pilih Mode Aplikasi ===");
        Log.println("1. CLI");
        Log.println("2. GUI");
        Log.print("Masukkan pilihan (1/2): ");

        int choice = CliHelper.inputInt();

        switch (choice) {
            case 1:
                Log.println("Cli");
                // Panggil modular CLI via CliManager -> ThreadCli (pertemuan 2)
                // Reuse scanner yang sama agar buffer tidak hilang (penting untuk piped input)
                sem3matkul.core.CliManager.start();
                break;
            case 2:
                Log.println("GUI");
                launch(args);
                break;
            default:
                Log.println("Invalid Choice");
                break;
        }
    }
}
