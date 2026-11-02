package sem3matkul.pertemuan7.model;

import sem3matkul.pertemuan7.swing.DemoLayout;
import sem3matkul.pertemuan7.swing.FormAnggota;
import sem3matkul.pertemuan7.swing.HalloGUI;
import sem3matkul.pertemuan7.swing.KomponenDasar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Handler peluncur jendela-jendela praktikum Java Swing pada Pertemuan 7.
 * Menjalankan seluruh pembuatan/manipulasi JFrame di Event Dispatch Thread (EDT)
 * dan mengatur DISPOSE_ON_CLOSE agar penutupan jendela Swing tidak mematikan aplikasi JavaFX utama.
 */
public class HandlerSwing {

    public static final String KEY_HALLO_GUI = "HalloGUI";
    public static final String KEY_KOMPONEN_DASAR = "KomponenDasar";
    public static final String KEY_DEMO_LAYOUT = "DemoLayout";
    public static final String KEY_FORM_ANGGOTA = "FormAnggota";

    private static volatile boolean lafInitialized = false;

    private final Map<String, JFrame> activeFrames = new ConcurrentHashMap<>();
    private final BiConsumer<String, Integer> statusListener;

    public HandlerSwing() {
        this(null);
    }

    public HandlerSwing(BiConsumer<String, Integer> statusListener) {
        this.statusListener = statusListener;
    }

    public void bukaHalloGUI() {
        bukaAtauFokus(KEY_HALLO_GUI, "Percobaan 1 - Halo GUI", HalloGUI::createFrame);
    }

    public void bukaKomponenDasar() {
        bukaAtauFokus(KEY_KOMPONEN_DASAR, "Percobaan 2 - Komponen Dasar Swing", KomponenDasar::new);
    }

    public void bukaDemoLayout() {
        bukaAtauFokus(KEY_DEMO_LAYOUT, "Percobaan 3 - Demo Layout Manager", DemoLayout::new);
    }

    public void bukaFormAnggota() {
        bukaAtauFokus(KEY_FORM_ANGGOTA, "Percobaan 4 - Form Anggota Perpustakaan", FormAnggota::new);
    }

    public void tutupSemuaSwing() {
        SwingUtilities.invokeLater(() -> {
            int closed = 0;
            for (JFrame frame : activeFrames.values()) {
                if (frame != null && frame.isDisplayable()) {
                    frame.dispose();
                    closed++;
                }
            }
            activeFrames.clear();
            notifyStatus(
                    closed > 0
                            ? "🧹 Menutup " + closed + " jendela Swing yang aktif."
                            : "ℹ️ Tidak ada jendela Swing yang sedang terbuka.",
                    0
            );
        });
    }

    public int getJumlahAktif() {
        return activeFrames.size();
    }

    private void bukaAtauFokus(String key, String label, Supplier<JFrame> frameSupplier) {
        SwingUtilities.invokeLater(() -> {
            initLookAndFeel();

            JFrame existing = activeFrames.get(key);
            if (existing != null && existing.isDisplayable()) {
                if ((existing.getExtendedState() & Frame.ICONIFIED) != 0) {
                    existing.setExtendedState(existing.getExtendedState() & ~Frame.ICONIFIED);
                }
                existing.setVisible(true);
                existing.toFront();
                existing.requestFocus();
                notifyStatus("🔄 Menampilkan ke depan: " + label, activeFrames.size());
                return;
            }

            JFrame frame = frameSupplier.get();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    activeFrames.remove(key, frame);
                    notifyStatus("❎ Jendela ditutup: " + label, activeFrames.size());
                }
            });

            activeFrames.put(key, frame);
            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
            notifyStatus("✅ Berhasil membuka: " + label, activeFrames.size());
        });
    }

    private static void initLookAndFeel() {
        if (!lafInitialized) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {
                // Gunakan tampilan bawaan jika Nimbus tidak tersedia
            }
            lafInitialized = true;
        }
    }

    private void notifyStatus(String message, int activeCount) {
        if (statusListener != null) {
            statusListener.accept(message, activeCount);
        }
    }
}
