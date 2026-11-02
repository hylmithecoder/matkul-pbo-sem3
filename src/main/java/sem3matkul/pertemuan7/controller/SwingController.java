package sem3matkul.pertemuan7.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sem3matkul.pertemuan7.model.HandlerSwing;

/**
 * Controller untuk modul Pertemuan 7: Pemrograman GUI dengan Java Swing.
 * Menyediakan 4 tombol untuk meluncurkan keempat percobaan Swing di folder pertemuan7/swing.
 */
public class SwingController {

    @FXML private Label lblStatusMessage;
    @FXML private Label lblJumlahAktif;

    private final HandlerSwing handlerSwing = new HandlerSwing(this::onSwingStatusChanged);

    @FXML
    public void initialize() {
        updateStatusUI("Pilih salah satu tombol di atas untuk membuka jendela praktikum Java Swing.", 0);
    }

    @FXML
    private void handleBukaHalloGUI() {
        handlerSwing.bukaHalloGUI();
    }

    @FXML
    private void handleBukaKomponenDasar() {
        handlerSwing.bukaKomponenDasar();
    }

    @FXML
    private void handleBukaDemoLayout() {
        handlerSwing.bukaDemoLayout();
    }

    @FXML
    private void handleBukaFormAnggota() {
        handlerSwing.bukaFormAnggota();
    }

    @FXML
    private void handleTutupSemuaSwing() {
        handlerSwing.tutupSemuaSwing();
    }

    private void onSwingStatusChanged(String message, int activeCount) {
        Platform.runLater(() -> updateStatusUI(message, activeCount));
    }

    private void updateStatusUI(String message, int activeCount) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText(message);
        }
        if (lblJumlahAktif != null) {
            lblJumlahAktif.setText(activeCount + " jendela aktif");
        }
    }
}
