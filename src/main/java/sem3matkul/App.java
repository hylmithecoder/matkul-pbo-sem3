package sem3matkul;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import sem3matkul.core.WindowManager;
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
        System.out.println("=== Pilih Mode Aplikasi ===");
        System.out.println("1. CLI");
        System.out.println("2. GUI");
        System.out.print("Masukkan pilihan (1/2): ");

        int choice = -1;
        // Jangan pakai try-with-resources agar System.in tidak tertutup (dibutuhkan CliManager)
        Scanner scanner = new Scanner(System.in);
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                try { choice = Integer.parseInt(line); } catch (NumberFormatException ignored) {}
            }
        } catch (Exception e) {
            System.out.println("Gagal baca input: " + e.getMessage());
        }

        switch (choice) {
            case 1:
                System.out.println("Cli");
                // Panggil modular CLI via CliManager -> ThreadCli (pertemuan 2)
                // Reuse scanner yang sama agar buffer tidak hilang (penting untuk piped input)
                sem3matkul.core.CliManager.start(scanner);
                break;
            case 2:
                System.out.println("GUI");
                launch(args);
                break;
            default:
                System.out.println("Invalid Choice");
                break;
        }
    }
}
