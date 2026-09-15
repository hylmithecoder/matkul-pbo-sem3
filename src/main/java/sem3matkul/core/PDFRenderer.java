package sem3matkul.core;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * PDFRenderer bertugas membuka window terpisah untuk me-render semua halaman PDF.
 * Menyediakan fungsionalitas scroll, zoom (+/-/Fit Width), multi-page layout,
 * serta integrasi pemanggilan modular per-pertemuan.
 */
public class PDFRenderer {

    private static final Map<String, Stage> openStages = new HashMap<>();
    private static final double DEFAULT_PAGE_WIDTH = 750.0;
    private static final double MIN_WIDTH = 380.0;
    private static final double MAX_WIDTH = 2200.0;
    private static final double ZOOM_STEP = 80.0;
    private static final int RENDER_DPI = 135;

    /**
     * Membuka dokumen PDF dari file dan menampilkannya pada window baru.
     *
     * @param file  File PDF yang akan dirender
     * @param title Judul jendela
     * @return Stage jendela PDF, atau null jika gagal
     */
    public static Stage open(File file, String title) {
        if (file == null || !file.exists()) {
            System.err.println("[PDFRenderer] File PDF tidak ditemukan: " + (file != null ? file.getPath() : "null"));
            return null;
        }

        try {
            String canonicalKey = file.getCanonicalPath();
            if (openStages.containsKey(canonicalKey)) {
                Stage existing = openStages.get(canonicalKey);
                if (existing != null && existing.isShowing()) {
                    existing.setAlwaysOnTop(true);
                    existing.toFront();
                    existing.requestFocus();
                    return existing;
                }
            }

            PDDocument document = Loader.loadPDF(file);
            int totalPages = document.getNumberOfPages();
            if (totalPages == 0) {
                document.close();
                System.err.println("[PDFRenderer] PDF tidak memiliki halaman.");
                return null;
            }

            Stage stage = new Stage();
            String windowTitle = (title != null && !title.isEmpty()) ? title : "Modul PDF - " + file.getName();
            stage.setTitle(windowTitle);

            // Container daftar halaman
            VBox pagesContainer = new VBox(22);
            pagesContainer.setAlignment(Pos.TOP_CENTER);
            pagesContainer.setPadding(new Insets(20));
            pagesContainer.setStyle("-fx-background-color: #f1f5f9;");

            ScrollPane scrollPane = new ScrollPane(pagesContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            scrollPane.setStyle("-fx-background: #f1f5f9; -fx-background-color: #f1f5f9; -fx-border-color: transparent;");

            // Toolbar Controls
            Label titleLbl = new Label("📄 " + file.getName());
            titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusLbl = new Label("Memuat 1 dari " + totalPages + " halaman...");
            statusLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

            Label zoomLbl = new Label("100%");
            zoomLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-min-width: 48px; -fx-alignment: center;");

            Button zoomOutBtn = new Button("➖");
            zoomOutBtn.getStyleClass().addAll("btn", "btn-secondary");

            Button zoomInBtn = new Button("➕");
            zoomInBtn.getStyleClass().addAll("btn", "btn-secondary");

            Button fitWidthBtn = new Button("⤢ Pas Lebar");
            fitWidthBtn.getStyleClass().addAll("btn", "btn-secondary");

            Button openOtherBtn = new Button("📂 Buka PDF Lain");
            openOtherBtn.getStyleClass().addAll("btn", "btn-primary");

            HBox toolbar = new HBox(10, titleLbl, spacer, statusLbl, zoomOutBtn, zoomLbl, zoomInBtn, fitWidthBtn, openOtherBtn);
            toolbar.setAlignment(Pos.CENTER_LEFT);
            toolbar.setPadding(new Insets(10, 16, 10, 16));
            toolbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-width: 0 0 1px 0;");

            ProgressBar progressBar = new ProgressBar(0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.setPrefHeight(4);

            VBox topBar = new VBox(toolbar, progressBar);

            BorderPane root = new BorderPane();
            root.setTop(topBar);
            root.setCenter(scrollPane);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double pdfWidth = Math.max(MIN_WIDTH, Math.min(screenBounds.getWidth() * 0.44, 760.0));
            double pdfHeight = screenBounds.getHeight();

            Scene scene = new Scene(root, pdfWidth, pdfHeight);
            URL cssResource = PDFRenderer.class.getResource("/sem3matkul/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(400);

            // Set posisi di sebelah kiri layar dan melayang (Always on Top)
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(pdfWidth);
            stage.setHeight(pdfHeight);
            stage.setAlwaysOnTop(true);

            // Pengaturan Zoom berdasar lebar awal
            double initialPageWidth = Math.max(MIN_WIDTH - 70, pdfWidth - 70);
            final double[] currentWidth = { initialPageWidth };
            final List<ImageView> imageViews = new ArrayList<>();

            Runnable updateZoomDisplay = () -> {
                int percent = (int) Math.round((currentWidth[0] / DEFAULT_PAGE_WIDTH) * 100);
                zoomLbl.setText(percent + "%");
                for (ImageView iv : imageViews) {
                    iv.setFitWidth(currentWidth[0]);
                }
            };

            zoomInBtn.setOnAction(e -> {
                if (currentWidth[0] + ZOOM_STEP <= MAX_WIDTH) {
                    currentWidth[0] += ZOOM_STEP;
                    updateZoomDisplay.run();
                }
            });

            zoomOutBtn.setOnAction(e -> {
                if (currentWidth[0] - ZOOM_STEP >= MIN_WIDTH) {
                    currentWidth[0] -= ZOOM_STEP;
                    updateZoomDisplay.run();
                }
            });

            fitWidthBtn.setOnAction(e -> {
                double viewW = scrollPane.getViewportBounds().getWidth() - 70;
                if (viewW >= MIN_WIDTH) {
                    currentWidth[0] = Math.min(viewW, MAX_WIDTH);
                    updateZoomDisplay.run();
                }
            });

            openOtherBtn.setOnAction(e -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Pilih Dokumen PDF");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"));
                File chosen = chooser.showOpenDialog(stage);
                if (chosen != null) {
                    open(chosen, "Modul PDF - " + chosen.getName());
                }
            });

            // Status kontrol penutupan dokumen
            AtomicBoolean isClosed = new AtomicBoolean(false);

            stage.setOnCloseRequest(event -> {
                isClosed.set(true);
                openStages.remove(canonicalKey);
                try {
                    document.close();
                } catch (IOException ignored) {}
            });

            openStages.put(canonicalKey, stage);
            stage.show();

            // Pastikan posisi melayang di sebelah kiri layar setelah show
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(pdfWidth);
            stage.setHeight(pdfHeight);
            stage.setAlwaysOnTop(true);

            // Background thread rendering semua halaman agar UI tidak lag
            Thread renderThread = new Thread(() -> {
                try {
                    org.apache.pdfbox.rendering.PDFRenderer boxRenderer = new org.apache.pdfbox.rendering.PDFRenderer(document);

                    for (int pageIdx = 0; pageIdx < totalPages; pageIdx++) {
                        if (isClosed.get()) break;

                        final int pNum = pageIdx + 1;
                        BufferedImage bImg = boxRenderer.renderImageWithDPI(pageIdx, RENDER_DPI);
                        Image fxImage = SwingFXUtils.toFXImage(bImg, null);

                        Platform.runLater(() -> {
                            if (isClosed.get()) return;

                            // Halaman Card UI
                            VBox pageCard = new VBox(8);
                            pageCard.setAlignment(Pos.CENTER);
                            pageCard.setStyle(
                                    "-fx-background-color: #ffffff; " +
                                    "-fx-border-color: #cbd5e1; " +
                                    "-fx-border-width: 1px; " +
                                    "-fx-border-radius: 8px; " +
                                    "-fx-background-radius: 8px; " +
                                    "-fx-padding: 14px; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);"
                            );

                            ImageView iv = new ImageView(fxImage);
                            iv.setPreserveRatio(true);
                            iv.setFitWidth(currentWidth[0]);
                            imageViews.add(iv);

                            Label pageNumLabel = new Label("Halaman " + pNum + " dari " + totalPages);
                            pageNumLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

                            pageCard.getChildren().addAll(iv, pageNumLabel);
                            pagesContainer.getChildren().add(pageCard);

                            double progress = (double) pNum / totalPages;
                            progressBar.setProgress(progress);
                            statusLbl.setText("Halaman " + pNum + " dari " + totalPages + " dimuat");

                            if (pNum == totalPages) {
                                progressBar.setVisible(false);
                                progressBar.setManaged(false);
                                statusLbl.setText("Selesai (" + totalPages + " Halaman)");
                            }
                        });
                    }
                } catch (Exception ex) {
                    if (!isClosed.get()) {
                        System.err.println("[PDFRenderer] Kesalahan rendering PDF: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }, "PDFRenderer-Worker-" + file.getName());

            renderThread.setDaemon(true);
            renderThread.start();

            return stage;
        } catch (IOException e) {
            System.err.println("[PDFRenderer] Gagal membaca berkas PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Membuka file PDF berdasarkan nama berkas (misal file di dalam folder docs/)
     * Contoh: PDFRenderer.openPDF("Pert 2(Collection Lanj) Praktik.pdf");
     *
     * @param fileName Nama file di folder docs/ atau path file
     * @return Stage jika file ada dan berhasil dibuka, null jika tidak ditemukan
     */
    public static Stage openPDF(String fileName) {
        return openPDF(fileName, null);
    }

    /**
     * Membuka file PDF berdasarkan nama berkas dengan judul kustom.
     *
     * @param fileName Nama file di folder docs/ atau path file
     * @param title    Judul window
     * @return Stage jika file ada dan berhasil dibuka, null jika tidak ditemukan
     */
    public static Stage openPDF(String fileName, String title) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        File file = resolveFile(fileName);
        if (file != null && file.exists()) {
            String windowTitle = (title != null && !title.isEmpty()) ? title : "Modul PDF - " + file.getName();
            return open(file, windowTitle);
        } else {
            System.out.println("[PDFRenderer] File PDF '" + fileName + "' tidak ditemukan di folder docs/.");
            return null;
        }
    }

    /**
     * Membantu mencari file di root path, folder docs/, atau folder terkait.
     */
    public static File resolveFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return null;

        File direct = new File(fileName);
        if (direct.exists()) return direct;

        File inDocs = new File("docs", fileName);
        if (inDocs.exists()) return inDocs;

        File inParentDocs = new File("../docs", fileName);
        if (inParentDocs.exists()) return inParentDocs;

        File inResourceDocs = new File("src/main/resources/docs", fileName);
        if (inResourceDocs.exists()) return inResourceDocs;

        return null;
    }

    /**
     * Membuka dokumen PDF berdasarkan path String.
     */
    public static Stage open(String filePath, String title) {
        if (filePath == null) return null;
        return openPDF(filePath, title);
    }

    /**
     * Mencari dan membuka PDF untuk nomor pertemuan tertentu jika berkas tersedia.
     *
     * @param pertemuan Nomor pertemuan (misal: 1, 2, 3)
     * @param title     Judul window yang diinginkan
     * @return Stage jika PDF ada dan berhasil dibuka, null jika modul PDF tidak ditemukan
     */
    public static Stage openForPertemuan(int pertemuan, String title) {
        File pdfFile = findPdfForPertemuan(pertemuan);
        if (pdfFile != null && pdfFile.exists()) {
            return open(pdfFile, title);
        } else {
            System.out.println("[PDFRenderer] Modul PDF untuk Pertemuan " + pertemuan + " belum tersedia.");
            return null;
        }
    }

    /**
     * Overload openForPertemuan dengan default title.
     */
    public static Stage openForPertemuan(int pertemuan) {
        return openForPertemuan(pertemuan, "Modul Pertemuan " + pertemuan);
    }

    /**
     * Mencari file PDF yang cocok dengan nomor pertemuan di dalam folder docs/ atau direktori proyek.
     */
    public static File findPdfForPertemuan(int pertemuan) {
        File[] candidateDirs = new File[] {
                new File("docs"),
                new File("../docs"),
                new File("src/main/resources/docs")
        };

        String[] matchPatterns = new String[] {
                "pert " + pertemuan,
                "pert" + pertemuan,
                "pertemuan " + pertemuan,
                "pertemuan" + pertemuan,
                "pert_" + pertemuan,
                "pertemuan_" + pertemuan,
                "pert-" + pertemuan,
                "pertemuan-" + pertemuan
        };

        for (File dir : candidateDirs) {
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
                if (files != null) {
                    for (File f : files) {
                        String nameLower = f.getName().toLowerCase();
                        for (String pat : matchPatterns) {
                            if (nameLower.contains(pat)) {
                                return f;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
