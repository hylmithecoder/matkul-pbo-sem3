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

    public static Stage openWindow(String fxmlPath, String title, double width, double height) {
        // Jika jendela dengan judul/kunci tersebut sudah dibuka, bawa ke depan
        if (openStages.containsKey(fxmlPath)) {
            Stage existingStage = openStages.get(fxmlPath);
            if (existingStage.isShowing()) {
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

            stage.setOnCloseRequest(event -> openStages.remove(fxmlPath));

            openStages.put(fxmlPath, stage);
            stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
