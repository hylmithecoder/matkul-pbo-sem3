package sem3matkul;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sem3matkul.dashboard.DashboardController;
import sem3matkul.pertemuan7.controller.SwingController;
import sem3matkul.pertemuan7.swing.DemoLayout;
import sem3matkul.pertemuan7.swing.FormAnggota;
import sem3matkul.pertemuan7.swing.HalloGUI;
import sem3matkul.pertemuan7.swing.KomponenDasar;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Pertemuan7GuiTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Pertemuan 7 GUI & FXML Verification Test...");

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
                // 1. Verifikasi dashboard.fxml + DashboardController
                URL dashboardUrl = Pertemuan7GuiTest.class.getResource("/sem3matkul/dashboard.fxml");
                if (dashboardUrl == null) {
                    throw new IllegalStateException("FXML /sem3matkul/dashboard.fxml tidak ditemukan!");
                }
                FXMLLoader dashLoader = new FXMLLoader(dashboardUrl);
                Parent dashRoot = dashLoader.load();
                DashboardController dashController = dashLoader.getController();
                if (dashRoot == null || dashController == null) {
                    throw new IllegalStateException("Gagal memuat dashboard.fxml / DashboardController!");
                }
                System.out.println("✓ dashboard.fxml & DashboardController berhasil dimuat.");

                // 2. Verifikasi swing.fxml + SwingController
                URL swingUrl = Pertemuan7GuiTest.class.getResource("/sem3matkul/pertemuan7/swing.fxml");
                if (swingUrl == null) {
                    throw new IllegalStateException("FXML /sem3matkul/pertemuan7/swing.fxml tidak ditemukan!");
                }
                FXMLLoader swingLoader = new FXMLLoader(swingUrl);
                Parent swingRoot = swingLoader.load();
                SwingController swingController = swingLoader.getController();
                if (swingRoot == null || swingController == null) {
                    throw new IllegalStateException("Gagal memuat swing.fxml / SwingController!");
                }
                System.out.println("✓ swing.fxml & SwingController (4 tombol) berhasil dimuat.");
            } catch (Throwable t) {
                errorRef.set(t);
            } finally {
                fxmlLatch.countDown();
            }
        });

        if (!fxmlLatch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Timeout saat memuat FXML!");
        }
        if (errorRef.get() != null) {
            errorRef.get().printStackTrace();
            throw new AssertionError("FXML Loading Error: " + errorRef.get().getMessage(), errorRef.get());
        }

        // 3. Verifikasi konstruksi 4 JFrame Swing bila display tersedia
        if (!GraphicsEnvironment.isHeadless()) {
            CountDownLatch swingLatch = new CountDownLatch(1);
            SwingUtilities.invokeLater(() -> {
                try {
                    JFrame f1 = HalloGUI.createFrame();
                    f1.dispose();
                    JFrame f2 = new KomponenDasar();
                    f2.dispose();
                    JFrame f3 = new DemoLayout();
                    f3.dispose();
                    JFrame f4 = new FormAnggota();
                    f4.dispose();
                    System.out.println("✓ Keempat JFrame Swing (HalloGUI, KomponenDasar, DemoLayout, FormAnggota) berhasil dibuat & di-dispose.");
                } catch (Throwable t) {
                    errorRef.set(t);
                } finally {
                    swingLatch.countDown();
                }
            });
            if (!swingLatch.await(10, TimeUnit.SECONDS)) {
                throw new AssertionError("Timeout saat menguji JFrame Swing!");
            }
            if (errorRef.get() != null) {
                errorRef.get().printStackTrace();
                throw new AssertionError("Swing Frame Error: " + errorRef.get().getMessage(), errorRef.get());
            }
        } else {
            System.out.println("ℹ Lingkungan headless terdeteksi, konstruksi JFrame dilewati.");
        }

        System.out.println("ALL PERTEMUAN 7 GUI VERIFICATION TESTS PASSED!");
        Platform.exit();
        System.exit(0);
    }
}
