package sem3matkul;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sem3matkul.pertemuan5.controller.PerpustakaanController;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PerpustakaanGuiTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Perpustakaan GUI & FXML Verification Test...");

        // Start JavaFX toolkit
        CountDownLatch initLatch = new CountDownLatch(1);
        try {
            Platform.startup(initLatch::countDown);
        } catch (IllegalStateException ignored) {
            initLatch.countDown();
        }
        initLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch fxmlLatch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = PerpustakaanGuiTest.class.getResource("/sem3matkul/pertemuan5/perpustakaan.fxml");
                if (fxmlUrl == null) {
                    throw new IllegalStateException("FXML /sem3matkul/pertemuan5/perpustakaan.fxml tidak ditemukan!");
                }
                System.out.println("✓ FXML resource ditemukan: " + fxmlUrl);

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                if (root == null) {
                    throw new IllegalStateException("Root node FXML null!");
                }
                System.out.println("✓ FXML berhasil di-load tanpa exception.");

                PerpustakaanController controller = loader.getController();
                if (controller == null) {
                    throw new IllegalStateException("Controller null!");
                }
                System.out.println("✓ PerpustakaanController berhasil diinisialisasi.");
            } catch (Throwable t) {
                errorRef.set(t);
            } finally {
                fxmlLatch.countDown();
            }
        });

        boolean completed = fxmlLatch.await(10, TimeUnit.SECONDS);
        if (!completed) {
            throw new AssertionError("Timeout saat memuat FXML!");
        }

        if (errorRef.get() != null) {
            errorRef.get().printStackTrace();
            throw new AssertionError("FXML Loading Error: " + errorRef.get().getMessage(), errorRef.get());
        }

        System.out.println("ALL GUI VERIFICATION TESTS PASSED!");
        Platform.exit();
        System.exit(0);
    }
}
