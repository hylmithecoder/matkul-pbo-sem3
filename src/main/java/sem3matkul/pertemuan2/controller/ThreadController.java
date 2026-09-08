package sem3matkul.pertemuan2.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sem3matkul.pertemuan2.model.Thread;

public class ThreadController {

    @FXML private TextField tfNama;

    @FXML private Label lblStatusMessage;
    @FXML private Label lblTotalThread;

    @FXML private TableView<Thread> tableThread;
    @FXML private TableColumn<Thread, Integer> colNo;
    @FXML private TableColumn<Thread, String> colNama;
    @FXML private TableColumn<Thread, String> colPrioritas;
    @FXML private TableColumn<Thread, Void> colAksi;

    private final ObservableList<Thread> listThread = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Kolom No = nomor urut baris (1,2,3...) agar tidak kosong & selalu rapi
        colNo.setCellFactory(col -> new TableCell<Thread, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));

        // Kolom Prioritas: badge warna TINGGI/NORMAL/RENDAH
        colPrioritas.setCellValueFactory(new PropertyValueFactory<>("prioritasString"));
        colPrioritas.setCellFactory(col -> new TableCell<Thread, String>() {
            private final Label badge = new Label();
            {
                badge.setMaxWidth(Double.MAX_VALUE);
                badge.setAlignment(Pos.CENTER);
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    badge.setText(item);
                    switch (item) {
                        case "TINGGI" -> badge.getStyleClass().setAll("badge-prioritas-tinggi");
                        case "NORMAL" -> badge.getStyleClass().setAll("badge-prioritas-normal");
                        case "RENDAH" -> badge.getStyleClass().setAll("badge-prioritas-rendah");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Kolom Aksi: tombol cycle prioritas
        colAksi.setCellFactory(col -> new TableCell<Thread, Void>() {
            private final Button btn = new Button();
            {
                btn.getStyleClass().add("btn-aksi");
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;
                    Thread data = getTableView().getItems().get(idx);
                    data.cyclePrioritas();
                    showStatus("🔄 Prioritas " + data.getNama() + " → " + data.getPrioritas().name(), false);
                    sortByPriorityWithThread();
                });
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Thread data = getTableView().getItems().get(getIndex());
                    // Tombol menampilkan prioritas berikutnya atau ikon
                    String next = switch (data.getPrioritas()) {
                        case TINGGI -> "→ NORMAL";
                        case NORMAL -> "→ RENDAH";
                        case RENDAH -> "→ TINGGI";
                    };
                    btn.setText(next);
                    // Warna tombol mengikuti prioritas saat ini agar kontras
                    btn.getStyleClass().removeAll("btn-aksi-tinggi","btn-aksi-normal","btn-aksi-rendah");
                    switch (data.getPrioritas()) {
                        case TINGGI -> btn.getStyleClass().add("btn-aksi-rendah"); // next is rendah-ish? keep visual
                        case NORMAL -> btn.getStyleClass().add("btn-aksi-normal");
                        case RENDAH -> btn.getStyleClass().add("btn-aksi-tinggi");
                    }
                    // tooltip
                    btn.setTooltip(new Tooltip("Klik untuk ubah prioritas " + data.getNama()));
                    setGraphic(btn);
                }
            }
        });

        tableThread.setItems(listThread);

        // Data awal sebagai contoh dengan prioritas berbeda untuk demo
        listThread.add(new Thread("Fulan", Thread.Prioritas.NORMAL));
        listThread.add(new Thread("Alex", Thread.Prioritas.RENDAH));
        listThread.add(new Thread("Budi", Thread.Prioritas.TINGGI));

        sortByPriorityWithThread();
        updateStatistics();
    }

    @FXML
    private void handleTambah() {
        String nama = tfNama.getText().trim();
        if (nama.isEmpty()) {
            showStatus("⚠️ Harap lengkapi nama pelanggan!", true);
            return;
        }
        Thread mhs = new Thread(nama, Thread.Prioritas.NORMAL);
        listThread.add(mhs);
        System.out.println("Thread Count: "+listThread.size());
        handleResetForm();
        showStatus("✅ " + nama + " ditambahkan (NORMAL)", false);
        sortByPriorityWithThread();
        updateStatistics();
    }

    @FXML
    private void handleHapus() {
        Thread selected = tableThread.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("⚠️ Pilih baris yang ingin dihapus!", true);
            return;
        }
        listThread.remove(selected);
        tableThread.refresh();
        showStatus("🗑️ " + selected.getNama() + " dihapus.", false);
        updateStatistics();
    }

    @FXML
    private void handleResetForm() {
        tfNama.clear();
    }

    private void showStatus(String message, boolean isError) {
        lblStatusMessage.setText(message);
        if (isError) {
            lblStatusMessage.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblStatusMessage.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    private void updateStatistics() {
        lblTotalThread.setText(String.valueOf(listThread.size()));
    }

    /**
     * Sorting antrian berdasarkan prioritas menggunakan java.lang.Thread
     * TINGGI (rank 0) paling atas, lalu NORMAL, lalu RENDAH.
     * Dalam prioritas sama, urut FIFO (urutanMasuk).
     * Dijalankan di background Thread untuk memenuhi requirement PBO Thread,
     * hasil sorting di-apply via Platform.runLater (JavaFX thread-safe).
     */
    private void sortByPriorityWithThread() {
        // Demo penggunaan java.lang.Thread + prioritas Thread
        java.lang.Thread worker = new java.lang.Thread(() -> {
            try {
                // simulasi proses penjadwalan prioritas (blocking)
                java.lang.Thread.sleep(120);
            } catch (InterruptedException ignored) {}

            // Set priority java thread sesuai mayoritas prioritas (demo)
            long tinggiCount = listThread.stream().filter(t -> t.getPrioritas() == Thread.Prioritas.TINGGI).count();
            if (tinggiCount > 0) {
                java.lang.Thread.currentThread().setPriority(java.lang.Thread.MAX_PRIORITY);
            } else {
                java.lang.Thread.currentThread().setPriority(java.lang.Thread.NORM_PRIORITY);
            }
            System.out.println("[WorkerThread] " + java.lang.Thread.currentThread().getName()
                    + " priority=" + java.lang.Thread.currentThread().getPriority()
                    + " sedang sort " + listThread.size() + " data...");

            Platform.runLater(() -> {
                FXCollections.sort(listThread, (a, b) -> {
                    int cmp = Integer.compare(a.getRankPrioritas(), b.getRankPrioritas());
                    if (cmp != 0) return cmp;
                    return Long.compare(a.getUrutanMasuk(), b.getUrutanMasuk());
                });
                tableThread.refresh();
            });
        }, "Prioritas-Sorter");
        worker.setDaemon(true);
        worker.start();
    }
}
