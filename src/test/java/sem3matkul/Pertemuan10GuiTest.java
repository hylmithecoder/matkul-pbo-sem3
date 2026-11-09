package sem3matkul;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sem3matkul.pertemuan10.swing.FormAnggota;
import sem3matkul.pertemuan10.swing.FormBuku;

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Memastikan FXML panel JavaFX dan kedua form Swing Pertemuan 10 bisa dibuat. */
public class Pertemuan10GuiTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch mulai = new CountDownLatch(1);
        Platform.startup(mulai::countDown);
        if (!mulai.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX gagal dimulai");
        }

        AtomicReference<Throwable> galat = new AtomicReference<>();
        CountDownLatch fxml = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                new FXMLLoader(Pertemuan10GuiTest.class.getResource("/sem3matkul/pertemuan10/input_data.fxml")).load();
                new FXMLLoader(Pertemuan10GuiTest.class.getResource("/sem3matkul/dashboard.fxml")).load();
                System.out.println("OK: input_data.fxml dan dashboard.fxml dimuat");
            } catch (Throwable t) {
                galat.set(t);
            } finally {
                fxml.countDown();
            }
        });
        fxml.await(15, TimeUnit.SECONDS);

        SwingUtilities.invokeAndWait(() -> {
            try {
                new FormAnggota().dispose();
                new FormBuku().dispose();
                System.out.println("OK: FormAnggota dan FormBuku dibuat");
            } catch (Throwable t) {
                galat.compareAndSet(null, t);
            }
        });

        if (galat.get() != null) {
            throw new AssertionError("Verifikasi gagal", galat.get());
        }
        System.out.println("PERTEMUAN 10 GUI VERIFICATION PASSED");
        Platform.exit();
        System.exit(0);
    }
}
