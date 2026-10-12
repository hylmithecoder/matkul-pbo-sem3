package sem3matkul.pertemuan4.cli;

import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * HelperFormat - pembantu format keluaran Markdown dari AI menjadi teks
 * terminal berwarna (tema POLMED / TRPL).
 *
 * Jawaban model biasanya berisi Markdown mentah: **tebal**, *miring*,
 * `kode`, judul (#), daftar (-), garis (---), blok kode (```), dan tabel pipa
 * (| a | b |). Kelas ini mengubahnya jadi baris siap tampil yang sudah
 * dibungkus (word wrap) sesuai lebar panel chat.
 *
 * Dipakai oleh {@link ChatBotCLI#bubble}.
 */
public final class HelperFormat {

    // --- Palet warna (senada dengan tema POLMED di ChatBotCLI) ---
    private static final String GOLD = "220";
    private static final String CYAN = "45";
    private static final String BLUE = "26";
    private static final String SOFT = "252";
    private static final String MUTED = "245";
    private static final String DIM = "240";
    private static final String CODE_FG = "114";
    private static final String CODE_BG = "236";

    private static final Style TEXT = Style.newStyle().foreground(Color.color(SOFT));
    private static final Style BOLD = Style.newStyle().foreground(Color.color(GOLD)).bold(true);
    private static final Style ITALIC = Style.newStyle().foreground(Color.color(SOFT)).italic(true);
    private static final Style CODE = Style.newStyle()
            .foreground(Color.color(CODE_FG))
            .background(Color.color(CODE_BG));
    private static final Style H1 = Style.newStyle().foreground(Color.color(GOLD)).bold(true).underline(true);
    private static final Style H2 = Style.newStyle().foreground(Color.color(CYAN)).bold(true);
    private static final Style H3 = Style.newStyle().foreground(Color.color(BLUE)).bold(true);
    private static final Style RULE = Style.newStyle().foreground(Color.color(DIM));
    private static final Style BULLET = Style.newStyle().foreground(Color.color(GOLD)).bold(true);
    private static final Style QUOTE_BAR = Style.newStyle().foreground(Color.color(BLUE));
    private static final Style QUOTE_TEXT = Style.newStyle().foreground(Color.color(MUTED)).italic(true);
    private static final Style TABLE_BORDER = Style.newStyle().foreground(Color.color(BLUE));
    private static final Style TABLE_HEAD = Style.newStyle().foreground(Color.color(GOLD)).bold(true);
    private static final Style CODE_BLOCK = Style.newStyle().foreground(Color.color(CODE_FG));
    private static final Style CODE_GUTTER = Style.newStyle().foreground(Color.color(DIM));

    private HelperFormat() {
    }

    // ==================================================================
    // API utama
    // ==================================================================

    /**
     * Ubah teks Markdown menjadi baris-baris siap tampil (sudah berwarna dan
     * sudah dibungkus sesuai lebar).
     *
     * @param markdown teks mentah dari AI
     * @param width    lebar maksimum satu baris (kolom terminal yang tersedia)
     */
    public static List<String> render(String markdown, int width) {
        int w = Math.max(20, width);
        List<String> out = new ArrayList<>();
        String[] lines = markdown.replace("\t", "    ").split("\n", -1);

        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();

            // Blok kode: ```java ... ```
            if (trimmed.startsWith("```")) {
                i = renderCodeBlock(lines, i, w, out);
                continue;
            }

            // Tabel: baris pipa yang diikuti baris pemisah |---|---|
            if (isTableRow(trimmed) && i + 1 < lines.length && isTableSeparator(lines[i + 1])) {
                i = renderTable(lines, i, w, out);
                continue;
            }

            // Garis horizontal: --- atau ***
            if (trimmed.matches("^([-*_])\\1{2,}$")) {
                out.add(RULE.render(repeat("─", w)));
                i++;
                continue;
            }

            if (trimmed.isEmpty()) {
                out.add("");
                i++;
                continue;
            }

            // Judul: #, ##, ###
            if (trimmed.startsWith("#")) {
                int level = 0;
                while (level < trimmed.length() && trimmed.charAt(level) == '#') {
                    level++;
                }
                String judul = trimmed.substring(level).trim();
                Style style = level == 1 ? H1 : level == 2 ? H2 : H3;
                for (String piece : wrapPlain(judul, w)) {
                    out.add(style.render(piece));
                }
                i++;
                continue;
            }

            // Kutipan: > teks
            if (trimmed.startsWith(">")) {
                String isi = trimmed.substring(1).trim();
                for (String piece : wrapSpans(parseInline(isi), w - 2)) {
                    out.add(QUOTE_BAR.render("▏ ") + QUOTE_TEXT.render(stripAnsi(piece)));
                }
                i++;
                continue;
            }

            // Daftar berpoin / bernomor (indentasi menggantung)
            String bulletMark = null;
            String isiList = null;
            int indent = line.length() - line.stripLeading().length();
            if (trimmed.matches("^[-*+]\\s+.*")) {
                bulletMark = "•";
                isiList = trimmed.substring(1).trim();
            } else if (trimmed.matches("^\\d+[.)]\\s+.*")) {
                int sep = trimmed.indexOf(trimmed.matches("^\\d+\\..*") ? '.' : ')');
                bulletMark = trimmed.substring(0, sep + 1);
                isiList = trimmed.substring(sep + 1).trim();
            }
            if (bulletMark != null) {
                String pad = repeat(" ", Math.min(indent, w / 4));
                String gutter = pad + bulletMark + " ";
                int isiWidth = Math.max(10, w - visibleLength(gutter));
                List<String> potongan = wrapSpans(parseInline(isiList), isiWidth);
                for (int k = 0; k < potongan.size(); k++) {
                    out.add(k == 0
                            ? pad + BULLET.render(bulletMark) + " " + potongan.get(k)
                            : repeat(" ", visibleLength(gutter)) + potongan.get(k));
                }
                i++;
                continue;
            }

            // Paragraf biasa
            out.addAll(wrapSpans(parseInline(trimmed), w));
            i++;
        }

        // Buang baris kosong berlebih di ujung
        while (!out.isEmpty() && out.get(out.size() - 1).isEmpty()) {
            out.remove(out.size() - 1);
        }
        return out;
    }

    // ==================================================================
    // Blok kode
    // ==================================================================

    private static int renderCodeBlock(String[] lines, int start, int width, List<String> out) {
        String bahasa = lines[start].trim().substring(3).trim();
        if (!bahasa.isEmpty()) {
            out.add(CODE_GUTTER.render("┌─ " + bahasa));
        } else {
            out.add(CODE_GUTTER.render("┌─"));
        }

        int i = start + 1;
        while (i < lines.length && !lines[i].trim().startsWith("```")) {
            for (String piece : hardWrap(lines[i], width - 2)) {
                out.add(CODE_GUTTER.render("│ ") + CODE_BLOCK.render(piece));
            }
            i++;
        }
        out.add(CODE_GUTTER.render("└─"));
        return i < lines.length ? i + 1 : i;
    }

    // ==================================================================
    // Tabel
    // ==================================================================

    private static boolean isTableRow(String line) {
        return line.contains("|") && line.replace("|", "").length() < line.length();
    }

    private static boolean isTableSeparator(String line) {
        String t = line.trim();
        return t.contains("|") && t.matches("^\\|?[\\s:|-]+\\|?$") && t.contains("-");
    }

    /** Pecah satu baris tabel jadi sel (mengabaikan pipa pembuka/penutup). */
    private static List<String> splitRow(String line) {
        String t = line.trim();
        if (t.startsWith("|")) {
            t = t.substring(1);
        }
        if (t.endsWith("|")) {
            t = t.substring(0, t.length() - 1);
        }
        List<String> cells = new ArrayList<>();
        for (String cell : t.split("\\|", -1)) {
            cells.add(cell.trim());
        }
        return cells;
    }

    private static int renderTable(String[] lines, int start, int width, List<String> out) {
        List<String> header = splitRow(lines[start]);
        List<List<String>> body = new ArrayList<>();

        int i = start + 2; // lewati baris pemisah
        while (i < lines.length && isTableRow(lines[i].trim()) && !lines[i].trim().isEmpty()) {
            body.add(splitRow(lines[i]));
            i++;
        }

        int cols = header.size();
        for (List<String> row : body) {
            cols = Math.max(cols, row.size());
        }
        if (cols == 0) {
            return i;
        }

        // Lebar tiap kolom: proporsional terhadap isi, lalu diciutkan agar muat
        int[] need = new int[cols];
        for (int c = 0; c < cols; c++) {
            need[c] = visibleLength(plain(cell(header, c)));
            for (List<String> row : body) {
                need[c] = Math.max(need[c], visibleLength(plain(cell(row, c))));
            }
            need[c] = Math.max(3, need[c]);
        }

        int chrome = cols + 1 + cols * 2; // garis vertikal + padding kiri-kanan
        int tersedia = Math.max(cols * 3, width - chrome);
        int total = 0;
        for (int n : need) {
            total += n;
        }
        // Ciutkan hanya kolom terlebar supaya kolom pendek (mis. "Tahun")
        // tidak ikut terpotong.
        int[] w = new int[cols];
        System.arraycopy(need, 0, w, 0, cols);
        while (total > tersedia) {
            int lebar = 0;
            for (int c = 1; c < cols; c++) {
                if (w[c] > w[lebar]) {
                    lebar = c;
                }
            }
            if (w[lebar] <= 3) {
                break;
            }
            w[lebar]--;
            total--;
        }

        out.add(TABLE_BORDER.render(borderLine("┌", "┬", "┐", w)));
        out.addAll(tableRow(header, w, true));
        out.add(TABLE_BORDER.render(borderLine("├", "┼", "┤", w)));
        for (List<String> row : body) {
            out.addAll(tableRow(row, w, false));
        }
        out.add(TABLE_BORDER.render(borderLine("└", "┴", "┘", w)));
        return i;
    }

    private static String cell(List<String> row, int index) {
        return index < row.size() ? row.get(index) : "";
    }

    private static String borderLine(String kiri, String tengah, String kanan, int[] w) {
        StringBuilder sb = new StringBuilder(kiri);
        for (int c = 0; c < w.length; c++) {
            sb.append(repeat("─", w[c] + 2));
            sb.append(c == w.length - 1 ? kanan : tengah);
        }
        return sb.toString();
    }

    /** Render satu baris tabel; sel yang panjang dibungkus jadi beberapa baris. */
    private static List<String> tableRow(List<String> row, int[] w, boolean isHeader) {
        int cols = w.length;
        List<List<String>> wrapped = new ArrayList<>();
        int tinggi = 1;
        for (int c = 0; c < cols; c++) {
            List<String> isi = isHeader
                    ? wrapPlain(plain(cell(row, c)), w[c])
                    : wrapSpans(parseInline(cell(row, c)), w[c]);
            if (isi.isEmpty()) {
                isi = new ArrayList<>(List.of(""));
            }
            wrapped.add(isi);
            tinggi = Math.max(tinggi, isi.size());
        }

        List<String> out = new ArrayList<>();
        String pipe = TABLE_BORDER.render("│");
        for (int baris = 0; baris < tinggi; baris++) {
            StringBuilder sb = new StringBuilder(pipe);
            for (int c = 0; c < cols; c++) {
                List<String> isi = wrapped.get(c);
                String teks = baris < isi.size() ? isi.get(baris) : "";
                String tampil = isHeader ? TABLE_HEAD.render(teks) : teks;
                int isiLen = visibleLength(teks);
                sb.append(' ')
                  .append(tampil)
                  .append(repeat(" ", Math.max(0, w[c] - isiLen)))
                  .append(' ')
                  .append(pipe);
            }
            out.add(sb.toString());
        }
        return out;
    }

    // ==================================================================
    // Format inline: **tebal**, *miring*, `kode`
    // ==================================================================

    /** Potongan teks beserta gayanya. */
    private record Span(String text, Style style) {
    }

    private static List<Span> parseInline(String text) {
        List<Span> spans = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char ch = text.charAt(i);

            if (ch == '`') {
                int tutup = text.indexOf('`', i + 1);
                if (tutup > i) {
                    flush(spans, buf);
                    spans.add(new Span(text.substring(i + 1, tutup), CODE));
                    i = tutup + 1;
                    continue;
                }
            }

            if (ch == '*' || ch == '_') {
                boolean tebal = i + 1 < text.length() && text.charAt(i + 1) == ch;
                String penanda = tebal ? String.valueOf(ch).repeat(2) : String.valueOf(ch);
                int tutup = text.indexOf(penanda, i + penanda.length());
                if (tutup > i) {
                    String isi = text.substring(i + penanda.length(), tutup);
                    if (!isi.isBlank()) {
                        flush(spans, buf);
                        spans.add(new Span(isi, tebal ? BOLD : ITALIC));
                        i = tutup + penanda.length();
                        continue;
                    }
                }
            }

            buf.append(ch);
            i++;
        }
        flush(spans, buf);
        return spans;
    }

    private static void flush(List<Span> spans, StringBuilder buf) {
        if (buf.length() > 0) {
            spans.add(new Span(buf.toString(), TEXT));
            buf.setLength(0);
        }
    }

    /** Hilangkan penanda Markdown tanpa mewarnai (untuk judul & header tabel). */
    private static String plain(String text) {
        StringBuilder sb = new StringBuilder();
        for (Span span : parseInline(text)) {
            sb.append(span.text());
        }
        return sb.toString().trim();
    }

    // ==================================================================
    // Pembungkus baris (word wrap)
    // ==================================================================

    /**
     * Bungkus daftar span per kata. Satu kata bisa terdiri dari beberapa span
     * dengan gaya berbeda (mis. <code>*miring*.</code> — tanda baca tidak boleh
     * terlempar ke kata sendiri), jadi pemenggalan hanya terjadi di spasi.
     */
    private static List<String> wrapSpans(List<Span> spans, int width) {
        int w = Math.max(1, width);

        // 1. Kumpulkan kata; tiap kata = daftar potongan bergaya
        List<List<Span>> kata = new ArrayList<>();
        List<Span> berjalan = new ArrayList<>();
        for (Span span : spans) {
            String text = span.text();
            int i = 0;
            while (i < text.length()) {
                if (Character.isWhitespace(text.charAt(i))) {
                    if (!berjalan.isEmpty()) {
                        kata.add(berjalan);
                        berjalan = new ArrayList<>();
                    }
                    i++;
                } else {
                    int j = i;
                    while (j < text.length() && !Character.isWhitespace(text.charAt(j))) {
                        j++;
                    }
                    berjalan.add(new Span(text.substring(i, j), span.style()));
                    i = j;
                }
            }
        }
        if (!berjalan.isEmpty()) {
            kata.add(berjalan);
        }

        // 2. Susun jadi baris selebar w
        List<String> out = new ArrayList<>();
        StringBuilder baris = new StringBuilder();
        int panjang = 0;
        for (List<Span> k : kata) {
            int panjangKata = 0;
            for (Span p : k) {
                panjangKata += p.text().length();
            }

            if (panjang > 0 && panjang + 1 + panjangKata > w) {
                out.add(baris.toString());
                baris.setLength(0);
                panjang = 0;
            }
            if (panjang > 0) {
                baris.append(' ');
                panjang++;
            }

            for (Span p : k) {
                // Kata yang lebih panjang dari lebar kolom dipotong paksa
                for (String bagian : hardWrap(p.text(), w)) {
                    if (panjang > 0 && panjang + bagian.length() > w) {
                        out.add(baris.toString());
                        baris.setLength(0);
                        panjang = 0;
                    }
                    baris.append(p.style().render(bagian));
                    panjang += bagian.length();
                }
            }
        }
        if (panjang > 0) {
            out.add(baris.toString());
        }
        return out;
    }

    /** Bungkus teks polos (tanpa gaya) per kata. */
    private static List<String> wrapPlain(String text, int width) {
        List<Span> spans = new ArrayList<>();
        spans.add(new Span(text, Style.newStyle()));
        List<String> out = new ArrayList<>();
        for (String baris : wrapSpans(spans, width)) {
            out.add(stripAnsi(baris));
        }
        return out;
    }

    /** Potong kata yang lebih panjang dari lebar kolom. */
    private static List<String> hardWrap(String kata, int width) {
        List<String> out = new ArrayList<>();
        int w = Math.max(1, width);
        String sisa = kata;
        while (sisa.length() > w) {
            out.add(sisa.substring(0, w));
            sisa = sisa.substring(w);
        }
        out.add(sisa);
        return out;
    }

    // ==================================================================
    // Utilitas
    // ==================================================================

    static String repeat(String unit, int times) {
        return times <= 0 ? "" : unit.repeat(times);
    }

    static String stripAnsi(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /** Panjang tampilan teks tanpa menghitung escape sequence ANSI. */
    static int visibleLength(String text) {
        return stripAnsi(text).length();
    }
}
