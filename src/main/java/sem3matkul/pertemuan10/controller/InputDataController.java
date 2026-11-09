package sem3matkul.pertemuan10.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sem3matkul.pertemuan10.dao.AnggotaDAO;
import sem3matkul.pertemuan10.dao.BukuDAO;
import sem3matkul.pertemuan10.koneksi.KoneksiDB;
import sem3matkul.pertemuan10.model.Anggota;
import sem3matkul.pertemuan10.model.Buku;
import sem3matkul.pertemuan10.swing.FormAnggota;
import sem3matkul.pertemuan10.swing.FormBuku;
import sem3matkul.pertemuan7.model.HandlerSwing;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/** Panel JavaFX untuk membuka form Swing dan menampilkan isi tabel. */
public class InputDataController {

    @FXML private Label lblStatusMessage;
    @FXML private Label lblJumlahAktif;
    @FXML private TableView<Anggota> tblAnggota;
    @FXML private TableView<Buku> tblBuku;

    private final HandlerSwing handlerSwing = new HandlerSwing(this::onStatusSwing);

    @FXML
    public void initialize() {
        tblAnggota.getColumns().add(kolom("ID", Anggota::getIdAnggota));
        tblAnggota.getColumns().add(kolom("Nama", Anggota::getNama));
        tblAnggota.getColumns().add(kolom("Alamat", Anggota::getAlamat));
        tblAnggota.getColumns().add(kolom("No Telp", Anggota::getNoHp));

        tblBuku.getColumns().add(kolom("ID", Buku::getId));
        tblBuku.getColumns().add(kolom("Judul", Buku::getNama));
        tblBuku.getColumns().add(kolom("Penulis", Buku::getPenulis));
        tblBuku.getColumns().add(kolom("Tipe", Buku::getTipe));
        tblBuku.getColumns().add(kolom("Genre", Buku::getGenre));
        tblBuku.getColumns().add(kolom("Stok", Buku::getStok));

        tblAnggota.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblBuku.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        muatData(true);
    }

    private static <T, V> TableColumn<T, V> kolom(String judul, Function<T, V> nilai) {
        TableColumn<T, V> kolom = new TableColumn<>(judul);
        kolom.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nilai.apply(c.getValue())));
        return kolom;
    }

    @FXML
    private void handleBukaFormAnggota() {
        handlerSwing.bukaAtauFokus("P10FormAnggota", "Form Anggota",
                () -> new FormAnggota(() -> muatData(false)));
    }

    @FXML
    private void handleBukaFormBuku() {
        handlerSwing.bukaAtauFokus("P10FormBuku", "Form Buku",
                () -> new FormBuku(() -> muatData(false)));
    }

    @FXML
    private void handleTutupSemuaSwing() {
        handlerSwing.tutupSemuaSwing();
    }

    @FXML
    private void handleMuatUlang() {
        muatData(false);
    }

    private record DataTabel(List<Anggota> anggota, List<Buku> buku) {
    }

    /** Query berjalan di luar JavaFX Application Thread agar UI tidak membeku. */
    private void muatData(boolean siapkanTabel) {
        CompletableFuture.supplyAsync(() -> {
            try {
                if (siapkanTabel) {
                    KoneksiDB.siapkanTabel();
                }
                return new DataTabel(new AnggotaDAO().semua(), new BukuDAO().semua());
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }).whenComplete((data, error) -> Platform.runLater(() -> {
            if (error != null) {
                lblStatusMessage.setText("❌ Database tidak bisa diakses: " + error.getCause().getMessage());
                return;
            }
            tblAnggota.setItems(FXCollections.observableArrayList(data.anggota()));
            tblBuku.setItems(FXCollections.observableArrayList(data.buku()));
            lblStatusMessage.setText("✅ Data dimuat: " + data.anggota().size() + " anggota, "
                    + data.buku().size() + " buku.");
        }));
    }

    private void onStatusSwing(String pesan, int jumlahAktif) {
        Platform.runLater(() -> {
            lblStatusMessage.setText(pesan);
            lblJumlahAktif.setText(jumlahAktif + " jendela aktif");
        });
    }
}
