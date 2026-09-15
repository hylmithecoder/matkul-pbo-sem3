package sem3matkul;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import sem3matkul.core.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFRendererTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Running PDFRenderer verification test...");

        // 1. Cek pencarian modul PDF untuk Pertemuan 3 (sesuai file yang ada di docs)
        File pdf3 = PDFRenderer.findPdfForPertemuan(3);
        if (pdf3 == null || !pdf3.exists()) {
            throw new AssertionError("PDF untuk Pertemuan 3 tidak ditemukan!");
        }
        System.out.println("✓ Menemukan file PDF Pertemuan 3: " + pdf3.getName());

        // 1b. Cek resolveFile dengan nama file langsung di folder docs/
        File resolved = PDFRenderer.resolveFile("Pert 3(Collection Lanj) Praktik.pdf");
        if (resolved == null || !resolved.exists()) {
            throw new AssertionError("resolveFile gagal menemukan file di folder docs/!");
        }
        System.out.println("✓ resolveFile('Pert 3(Collection Lanj) Praktik.pdf') sukses: " + resolved.getPath());

        // 2. Load berkas PDF dengan PDFBox
        try (PDDocument doc = Loader.loadPDF(pdf3)) {
            int pageCount = doc.getNumberOfPages();
            System.out.println("✓ Jumlah halaman: " + pageCount);
            if (pageCount <= 0) {
                throw new AssertionError("PDF tidak memiliki halaman!");
            }

            // 3. Render halaman pertama untuk memastikan rendering berfungsi tanpa exception
            org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
            BufferedImage image = renderer.renderImageWithDPI(0, 100);
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                throw new AssertionError("Gagal me-render BufferedImage halaman 1!");
            }
            System.out.println("✓ Sukses me-render halaman 1 (" + image.getWidth() + "x" + image.getHeight() + " px)");
        }

        // 4. Cek pertemuan yang belum memiliki PDF (misal Pertemuan 99)
        File pdf99 = PDFRenderer.findPdfForPertemuan(99);
        if (pdf99 != null) {
            throw new AssertionError("Seharusnya Pertemuan 99 tidak ditemukan!");
        }
        System.out.println("✓ Graceful check untuk pertemuan tanpa PDF berhasil.");

        System.out.println("ALL TESTS PASSED!");
    }
}
