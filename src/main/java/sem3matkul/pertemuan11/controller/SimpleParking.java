package sem3matkul.pertemuan11.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sem3matkul.core.KoneksiDB;
import sem3matkul.pertemuan11.handlers.MemberHandler;
import sem3matkul.pertemuan11.handlers.TransaksiHandler;
import sem3matkul.pertemuan11.handlers.UserHandler;
import sem3matkul.pertemuan11.models.Member;
import sem3matkul.pertemuan11.models.Transaksi;
import sem3matkul.pertemuan11.models.User;
import sem3matkul.pertemuan11.swing.FormMember;
import sem3matkul.pertemuan11.swing.FormTransaksi;
import sem3matkul.pertemuan11.swing.FormUser;
import sem3matkul.pertemuan7.model.HandlerSwing;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/** Panel JavaFX untuk membuka form Swing dan menampilkan isi tabel. */
public class SimpleParking {

    private static final String SKEMA_PARKIR = "/sem3matkul/pertemuan11/scheme/parkir.sql";

    @FXML private Label lblStatusMessage;
    @FXML private Label lblJumlahAktif;
    @FXML private TableView<User> tblUser;
    @FXML private TableView<Member> tblMember;
    @FXML private TableView<Transaksi> tblTransaksi;

    private final HandlerSwing handlerSwing = new HandlerSwing(this::onStatusSwing);

    @FXML
    public void initialize() {
        tblUser.getColumns().add(kolom("ID", (User u) -> u.id));
        tblUser.getColumns().add(kolom("Nama", (User u) -> u.nama));
        tblUser.getColumns().add(kolom("Email", (User u) -> u.email));
        tblUser.getColumns().add(kolom("Otoritas", (User u) -> u.otoritas));
        tblUser.getColumns().add(kolom("No Telp", (User u) -> u.no_hp));

        tblMember.getColumns().add(kolom("ID", (Member m) -> m.id));
        tblMember.getColumns().add(kolom("Nama", (Member m) -> m.nama));
        tblMember.getColumns().add(kolom("Email", (Member m) -> m.email));
        tblMember.getColumns().add(kolom("Tipe Mobil", (Member m) -> m.mobil));
        tblMember.getColumns().add(kolom("Plat No", (Member m) -> m.plat_no));

        tblTransaksi.getColumns().add(kolom("ID", (Transaksi t) -> t.id));
        tblTransaksi.getColumns().add(kolom("No Tiket", (Transaksi t) -> t.no_tiket));
        tblTransaksi.getColumns().add(kolom("Plat No", (Transaksi t) -> t.plat_no));
        tblTransaksi.getColumns().add(kolom("Mobil", (Transaksi t) -> t.mobil));
        tblTransaksi.getColumns().add(kolom("Masuk", Transaksi::masukTeks));
        tblTransaksi.getColumns().add(kolom("Keluar", Transaksi::keluarTeks));
        tblTransaksi.getColumns().add(kolom("ID Member", Transaksi::memberTeks));

        kunciLebarKolomId(tblUser);
        kunciLebarKolomId(tblMember);
        kunciLebarKolomId(tblTransaksi);

        tblUser.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblMember.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        muatData(true);
    }

    private static void kunciLebarKolomId(TableView<?> tabel) {
        TableColumn<?, ?> kolomId = tabel.getColumns().get(0);
        kolomId.setMinWidth(60);
        kolomId.setMaxWidth(60);
    }

    private static <T, V> TableColumn<T, V> kolom(String judul, Function<T, V> nilai) {
        TableColumn<T, V> kolom = new TableColumn<>(judul);
        kolom.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nilai.apply(c.getValue())));
        return kolom;
    }

    @FXML
    private void handleBukaFormUser() {
        handlerSwing.bukaAtauFokus("P11FormUser", "Form User",
                () -> new FormUser(() -> muatData(false)));
    }

    @FXML
    private void handleBukaFormMember() {
        handlerSwing.bukaAtauFokus("P11FormMember", "Form Member",
                () -> new FormMember(() -> muatData(false)));
    }

    @FXML
    private void handleBukaFormTrx() {
        handlerSwing.bukaAtauFokus("P11FormTransaksi", "Form Transaksi",
                () -> new FormTransaksi(() -> muatData(false)));
    }

    @FXML
    private void handleTutupSemuaSwing() {
        handlerSwing.tutupSemuaSwing();
    }

    @FXML
    private void handleMuatUlang() {
        muatData(false);
    }

    private record DataTabel(List<User> user, List<Member> member, List<Transaksi> transaksi) {
    }

    /** Query berjalan di luar JavaFX Application Thread agar UI tidak membeku. */
    private void muatData(boolean siapkanTabel) {
        CompletableFuture.supplyAsync(() -> {
            try {
                if (siapkanTabel) {
                    KoneksiDB.siapkanTabel(SKEMA_PARKIR);
                }
                return new DataTabel(new UserHandler().semua(), new MemberHandler().semua(),
                        new TransaksiHandler().semua());
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }).whenComplete((data, error) -> Platform.runLater(() -> {
            if (error != null) {
                lblStatusMessage.setText("❌ Database tidak bisa diakses: " + error.getCause().getMessage());
                return;
            }
            tblUser.setItems(FXCollections.observableArrayList(data.user()));
            tblMember.setItems(FXCollections.observableArrayList(data.member()));
            tblTransaksi.setItems(FXCollections.observableArrayList(data.transaksi()));
            lblStatusMessage.setText("✅ Data dimuat: " + data.user().size() + " user, "
                    + data.member().size() + " member, " + data.transaksi().size() + " transaksi.");
        }));
    }

    private void onStatusSwing(String pesan, int jumlahAktif) {
        Platform.runLater(() -> {
            lblStatusMessage.setText(pesan);
            lblJumlahAktif.setText(jumlahAktif + " jendela aktif");
        });
    }
}
