package sem3matkul.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * WindowManager mengatur navigasi jendela per-pertemuan/materi.
 * Membantu membuka window terpisah untuk tiap pertemuan secara modular.
 */
public class WindowManager {

    private static final Map<String, Stage> openStages = new HashMap<>();
    private static Stage dashboardStage;

    public static void setDashboardStage(Stage stage) {
        dashboardStage = stage;
    }

    public static Stage getDashboardStage() {
        return dashboardStage;
    }

    public static Stage openWindow(String fxmlPath, String title, double width, double height) {
        // Sembunyikan window dashboard utama saat modul dibuka
        if (dashboardStage != null) {
            dashboardStage.hide();
        }

        // Jika jendela dengan judul/kunci tersebut sudah dibuka, bawa ke depan
        if (openStages.containsKey(fxmlPath)) {
            Stage existingStage = openStages.get(fxmlPath);
            if (existingStage.isShowing()) {
                existingStage.setMaximized(true);
                existingStage.toFront();
                existingStage.requestFocus();
                return existingStage;
            }
        }

        try {
            URL resource = WindowManager.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);
            // Muat CSS tema global
            URL cssResource = WindowManager.class.getResource("/sem3matkul/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(width * 0.85);
            stage.setMinHeight(height * 0.85);

            stage.setOnCloseRequest(event -> {
                openStages.remove(fxmlPath);
                // Jika tidak ada jendela modul lain yang aktif, tampilkan kembali dashboard
                if (dashboardStage != null && openStages.isEmpty()) {
                    dashboardStage.show();
                    dashboardStage.setMaximized(true);
                }
            });

            openStages.put(fxmlPath, stage);
            stage.setMaximized(true);
            stage.show();
            stage.setMaximized(true);
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            if (dashboardStage != null) {
                dashboardStage.show();
                dashboardStage.setMaximized(true);
            }
            return null;
        }
    }
}
