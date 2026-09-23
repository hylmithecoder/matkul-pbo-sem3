package sem3matkul.pertemuan4.cli;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.Program;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.bubbletea.QuitMessage;
import com.williamcallahan.tui4j.compat.bubbletea.WindowSizeMessage;
import com.williamcallahan.tui4j.compat.bubbles.textarea.Textarea;
import com.williamcallahan.tui4j.compat.bubbles.viewport.Viewport;
import com.williamcallahan.tui4j.compat.bubbles.cursor.Cursor;
import com.williamcallahan.tui4j.compat.bubbles.spinner.Spinner;
import com.williamcallahan.tui4j.compat.bubbles.spinner.SpinnerType;
import com.williamcallahan.tui4j.compat.bubbles.spinner.TickMessage;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.Position;
import com.williamcallahan.tui4j.compat.lipgloss.Join;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.lipgloss.border.StandardBorder;
import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TUI ChatBot Pertemuan 4 menggunakan TUI4J (Bubble Tea port)
 * Terhubung ke router.ilmeee.com chat completion API.
 *
 * Tampilan memakai tema warna Politeknik Negeri Medan (biru navy + emas)
 * dengan aksen cyan untuk Program Studi TRPL.
 */
public class ChatBotCLI implements Model {

    private static final String API_URL = "https://router.ilmeee.com/api/v1/chat";
    private static String API_TOKEN = "";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    // --- Palet tema POLMED / TRPL (ANSI 256 color) ---
    private static final String POLMED_NAVY = "17";   // biru tua logo POLMED
    private static final String POLMED_BLUE = "26";   // biru medium
    private static final String POLMED_GOLD = "220";  // kuning emas logo
    private static final String TRPL_CYAN = "45";     // aksen prodi TRPL
    private static final String TEXT_SOFT = "252";
    private static final String TEXT_MUTED = "245";
    private static final String TEXT_DIM = "240";
    private static final String DANGER = "203";
    private static final String SUCCESS = "78";

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");

    // Pesan internal asinkron untuk Bubble Tea event loop
    public record BotReplyMessage(String content) implements Message {}
    public record BotErrorMessage(String error) implements Message {}

    public static void SetDotenv(){
        Dotenv dotenv = Dotenv.load();
        String keyRouter = dotenv.get("KEY_ROUTER");

        API_TOKEN = keyRouter;
    }

    private final Viewport viewport;
    private final Textarea textarea;
    private final Spinner spinner;
    private final List<String> renderedChat;
    private final List<JSONObject> conversationHistory;

    private boolean isLoading = false;
    private int frameWidth = initialWidth();
    private int totalTanya = 0;

    // --- Lipgloss styles ---
    private final Style brandBar;
    private final Style brandSub;
    private final Style prodiChip;
    private final Style pillDark;
    private final Style pillGold;
    private final Style pillCyan;
    private final Style pillLive;
    private final Style helpKey;
    private final Style keyCap;
    private final Style helpText;
    private final Style userLabel;
    private final Style userBar;
    private final Style botLabel;
    private final Style botBar;
    private final Style errorLabel;
    private final Style errorBar;
    private final Style timeStyle;
    private final Style bodyStyle;
    private final Style infoStyle;
    private final Style chatBox;
    private final Style inputBox;
    private final Style sectionLabel;

    public ChatBotCLI() {
        this.brandBar = Style.newStyle()
                .foreground(Color.color(POLMED_GOLD))
                .background(Color.color(POLMED_NAVY))
                .bold(true)
                .align(Position.Center);
        this.brandSub = Style.newStyle()
                .foreground(Color.color(TEXT_SOFT))
                .background(Color.color(POLMED_NAVY))
                .align(Position.Center);
        this.prodiChip = Style.newStyle()
                .foreground(Color.color(TRPL_CYAN))
                .background(Color.color(POLMED_NAVY))
                .bold(true)
                .align(Position.Center);

        this.pillDark = Style.newStyle()
                .foreground(Color.color(TEXT_SOFT))
                .background(Color.color(POLMED_NAVY))
                .padding(0, 1);
        this.pillGold = Style.newStyle()
                .foreground(Color.color(POLMED_NAVY))
                .background(Color.color(POLMED_GOLD))
                .bold(true)
                .padding(0, 1);
        this.pillCyan = Style.newStyle()
                .foreground(Color.color(POLMED_NAVY))
                .background(Color.color(TRPL_CYAN))
                .bold(true)
                .padding(0, 1);
        this.pillLive = Style.newStyle()
                .foreground(Color.color(SUCCESS))
                .background(Color.color(POLMED_NAVY))
                .bold(true)
                .padding(0, 1);

        this.helpKey = Style.newStyle().foreground(Color.color(POLMED_GOLD)).bold(true);
        this.keyCap = Style.newStyle()
                .foreground(Color.color(POLMED_NAVY))
                .background(Color.color(POLMED_GOLD))
                .bold(true)
                .padding(0, 1);
        this.helpText = Style.newStyle().foreground(Color.color(TEXT_DIM));

        this.userLabel = Style.newStyle().foreground(Color.color(TRPL_CYAN)).bold(true);
        this.userBar = Style.newStyle().foreground(Color.color(TRPL_CYAN));
        this.botLabel = Style.newStyle().foreground(Color.color(POLMED_GOLD)).bold(true);
        this.botBar = Style.newStyle().foreground(Color.color(POLMED_GOLD));
        this.errorLabel = Style.newStyle().foreground(Color.color(DANGER)).bold(true);
        this.errorBar = Style.newStyle().foreground(Color.color(DANGER));

        this.timeStyle = Style.newStyle().foreground(Color.color(TEXT_DIM));
        this.bodyStyle = Style.newStyle().foreground(Color.color(TEXT_SOFT));
        this.infoStyle = Style.newStyle().foreground(Color.color(TEXT_MUTED)).italic(true);
        this.sectionLabel = Style.newStyle().foreground(Color.color(POLMED_BLUE)).bold(true);

        this.chatBox = Style.newStyle()
                .border(StandardBorder.RoundedBorder)
                .borderForeground(Color.color(POLMED_BLUE))
                .padding(0, 1);
        this.inputBox = Style.newStyle()
                .border(StandardBorder.RoundedBorder)
                .borderForeground(Color.color(POLMED_GOLD))
                .padding(0, 1);

        this.spinner = new Spinner(SpinnerType.DOT);
        this.spinner.setStyle(Style.newStyle().foreground(Color.color(POLMED_GOLD)));

        this.textarea = new Textarea();
        this.textarea.setPlaceholder("Tulis pertanyaanmu di sini, lalu tekan Enter...");
        this.textarea.focus();
        this.textarea.setPrompt("❯ ");
        this.textarea.setCharLimit(1000);
        this.textarea.setWidth(frameWidth - 4);
        this.textarea.setHeight(3);
        this.textarea.setShowLineNumbers(false);
        this.textarea.focusedStyle()
                .prompt(Style.newStyle().foreground(Color.color(POLMED_GOLD)).bold(true))
                .text(Style.newStyle().foreground(Color.color(TEXT_SOFT)))
                .placeholder(Style.newStyle().foreground(Color.color(TEXT_DIM)).italic(true))
                .cursorLine(Style.newStyle());
        this.textarea.blurredStyle()
                .prompt(Style.newStyle().foreground(Color.color(TEXT_DIM)))
                .text(Style.newStyle().foreground(Color.color(TEXT_MUTED)))
                .placeholder(Style.newStyle().foreground(Color.color(TEXT_DIM)).italic(true));

        this.viewport = Viewport.create(frameWidth - 4, 14);
        this.renderedChat = new ArrayList<>();
        this.conversationHistory = new ArrayList<>();
    }

    @Override
    public Command init() {
        renderedChat.add(welcomeCard());
        updateViewportContent();
        return Cursor::blink;
    }

    // ------------------------------------------------------------------
    // Update
    // ------------------------------------------------------------------

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof WindowSizeMessage w) {
            this.frameWidth = Math.max(40, Math.min(w.width() - 2, 110));
            int innerWidth = frameWidth - 4;
            int targetHeight = Math.max(6, w.height() - 16);
            viewport.setWidth(innerWidth);
            viewport.setHeight(targetHeight);
            textarea.setWidth(innerWidth);
            updateViewportContent();
            return UpdateResult.from(this);
        }

        if (msg instanceof TickMessage) {
            // Animasi spinner hanya berjalan selama menunggu jawaban bot
            UpdateResult<?> res = spinner.update(msg);
            return UpdateResult.from(this, isLoading ? res.command() : null);
        }

        if (msg instanceof BotReplyMessage reply) {
            this.isLoading = false;
            conversationHistory.add(new JSONObject().put("role", "assistant").put("content", reply.content()));
            renderedChat.add(bubble(botLabel, botBar, "POLMED AI", reply.content()));
            updateViewportContent();
            textarea.focus();
            return UpdateResult.from(this);
        }

        if (msg instanceof BotErrorMessage err) {
            this.isLoading = false;
            renderedChat.add(bubble(errorLabel, errorBar, "Kesalahan", err.error()));
            updateViewportContent();
            textarea.focus();
            return UpdateResult.from(this);
        }

        if (msg instanceof KeyPressMessage key) {
            String keyStr = key.key();
            if ("ctrl+c".equals(keyStr) || "esc".equals(keyStr)) {
                return UpdateResult.from(this, QuitMessage::new);
            }

            if ("enter".equals(keyStr) || "ctrl+j".equals(keyStr) || "ctrl+m".equals(keyStr)) {
                String input = textarea.value().trim();
                textarea.reset();

                if (input.isEmpty()) {
                    return UpdateResult.from(this);
                }

                if (isLoading) {
                    renderedChat.add(notice("Sabar dulu ya, bot masih menyusun jawaban..."));
                    updateViewportContent();
                    return UpdateResult.from(this);
                }

                if (input.startsWith("/")) {
                    return handleSlashCommand(input);
                }

                // Tambahkan pesan user ke konteks dan tampilan
                conversationHistory.add(new JSONObject().put("role", "user").put("content", input));
                renderedChat.add(bubble(userLabel, userBar, "Mahasiswa TRPL", input));
                this.isLoading = true;
                this.totalTanya++;
                updateViewportContent();

                // Jalankan request API asinkron + animasi spinner
                List<JSONObject> historySnapshot = new ArrayList<>(conversationHistory);
                return UpdateResult.from(this,
                        Command.batch(createApiCallCommand(historySnapshot), spinner.init()));
            }
        }

        // Delegasikan update ke komponen anak (textarea & viewport)
        UpdateResult<? extends Model> taResult = textarea.update(msg);
        UpdateResult<? extends Model> vpResult = viewport.update(msg);

        Command combined = null;
        if (taResult.command() != null && vpResult.command() != null) {
            combined = Command.batch(taResult.command(), vpResult.command());
        } else if (taResult.command() != null) {
            combined = taResult.command();
        } else if (vpResult.command() != null) {
            combined = vpResult.command();
        }

        return UpdateResult.from(this, combined);
    }

    private UpdateResult<? extends Model> handleSlashCommand(String input) {
        switch (input.toLowerCase()) {
            case "/clear", "/bersih" -> {
                renderedChat.clear();
                conversationHistory.clear();
                totalTanya = 0;
                renderedChat.add(welcomeCard());
            }
            case "/help", "/bantuan" -> renderedChat.add(helpCard());
            case "/quit", "/keluar" -> {
                return UpdateResult.from(this, QuitMessage::new);
            }
            default -> renderedChat.add(notice("Perintah '" + input + "' tidak dikenal. Coba /bantuan."));
        }
        updateViewportContent();
        return UpdateResult.from(this);
    }

    private void updateViewportContent() {
        viewport.setContent(String.join("\n\n", renderedChat));
        viewport.gotoBottom();
    }

    // ------------------------------------------------------------------
    // Komponen tampilan
    // ------------------------------------------------------------------

    private int chatWidth() {
        return Math.max(20, viewport.getWidth());
    }

    /** Gelembung pesan: garis vertikal berwarna + label + jam + isi yang dibungkus rapi. */
    private String bubble(Style labelStyle, Style barStyle, String label, String body) {
        String head = barStyle.render("╭─ ") + labelStyle.render(label) + " "
                + timeStyle.render("· " + LocalTime.now().format(CLOCK));
        StringBuilder sb = new StringBuilder(head);
        for (String line : wrap(body, chatWidth() - 3)) {
            sb.append("\n").append(barStyle.render("│ ")).append(bodyStyle.render(line));
        }
        sb.append("\n").append(barStyle.render("╰─"));
        return sb.toString();
    }

    private String notice(String text) {
        return infoStyle.render("  ⓘ " + text);
    }

    private String welcomeCard() {
        String[] lines = {
            "Selamat datang di Laboratorium AI Prodi TRPL — Politeknik Negeri Medan.",
            "Modul Pertemuan 4: konsumsi REST API chat completion dengan Java 21.",
            "Endpoint  : router.ilmeee.com/api/v1/chat",
            "Perintah  : /bantuan · /bersih · /keluar"
        };
        StringBuilder sb = new StringBuilder(sectionLabel.render("  ┌ MULAI SESI"));
        for (String line : lines) {
            for (String w : wrap(line, chatWidth() - 6)) {
                sb.append("\n").append(sectionLabel.render("  │ ")).append(infoStyle.render(w));
            }
        }
        sb.append("\n").append(sectionLabel.render("  └"));
        return sb.toString();
    }

    private String helpCard() {
        String[][] items = {
            {"Enter", "kirim pertanyaan ke AI"},
            {"↑ / ↓", "gulir riwayat percakapan"},
            {"/bersih", "hapus riwayat & mulai sesi baru"},
            {"/bantuan", "tampilkan bantuan ini"},
            {"Esc", "keluar dari aplikasi"}
        };
        StringBuilder sb = new StringBuilder(sectionLabel.render("  ┌ BANTUAN"));
        for (String[] item : items) {
            sb.append("\n").append(sectionLabel.render("  │ "))
              .append(helpKey.render(pad(item[0], 10))).append(infoStyle.render(item[1]));
        }
        sb.append("\n").append(sectionLabel.render("  └"));
        return sb.toString();
    }

    /** Kop surat bergaya kampus: biru navy dengan teks emas. */
    private String header() {
        int w = frameWidth;
        String top = brandBar.width(w).render("◆  POLITEKNIK NEGERI MEDAN  ◆");
        String sub = brandSub.width(w).render("Jurusan Teknik Komputer & Informatika");
        String prodi = prodiChip.width(w).render("D-IV TEKNOLOGI REKAYASA PERANGKAT LUNAK");
        String rule = Style.newStyle()
                .foreground(Color.color(POLMED_GOLD))
                .background(Color.color(POLMED_NAVY))
                .render(repeat("─", w));
        return Join.joinVertical(Position.Left, top, sub, prodi, rule);
    }

    /** Baris status: prodi, mata kuliah, status koneksi, dan jumlah pertanyaan. */
    private String statusBar() {
        String status = isLoading
                ? pillDark.render(spinner.view() + " Memproses")
                : pillLive.render("● Siap");
        String left = Join.joinHorizontal(Position.Left,
                pillGold.render("TRPL"),
                pillCyan.render("PBO · Pertemuan 4"),
                status,
                pillDark.render("✉ " + totalTanya));
        String right = pillDark.render("router.ilmeee.com");
        int gap = frameWidth - visibleLength(left) - visibleLength(right);
        if (gap < 1) {
            return left;
        }
        return left + pillDark.copy().padding(0, 0).render(repeat(" ", gap)) + right;
    }

    private String footer() {
        return Join.joinHorizontal(Position.Left,
                keyCap.render("Enter"), helpText.render(" kirim    "),
                keyCap.render("\u2191\u2193"), helpText.render(" gulir    "),
                keyCap.render("/bantuan"), helpText.render(" perintah    "),
                keyCap.render("Esc"), helpText.render(" keluar"));
    }

    @Override
    public String view() {
        String chatTitle = sectionLabel.render("Riwayat Percakapan");
        String inputTitle = isLoading
                ? infoStyle.render("Menunggu jawaban " + spinner.view())
                : sectionLabel.render("Pesan Anda");

        return Join.joinVertical(Position.Left,
                header(),
                statusBar(),
                "",
                "  " + chatTitle,
                chatBox.width(frameWidth - 2).render(viewport.view()),
                "  " + inputTitle,
                inputBox.width(frameWidth - 2).render(textarea.view()),
                "  " + footer());
    }

    // ------------------------------------------------------------------
    // Utilitas teks
    // ------------------------------------------------------------------

    /** Bungkus teks per kata agar tidak terpotong lebar viewport. */
    private static List<String> wrap(String text, int width) {
        List<String> out = new ArrayList<>();
        if (width < 8) {
            width = 8;
        }
        for (String paragraph : text.split("\n", -1)) {
            if (paragraph.isBlank()) {
                out.add("");
                continue;
            }
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.trim().split("\\s+")) {
                while (word.length() > width) {
                    if (line.length() > 0) {
                        out.add(line.toString());
                        line.setLength(0);
                    }
                    out.add(word.substring(0, width));
                    word = word.substring(width);
                }
                if (line.length() == 0) {
                    line.append(word);
                } else if (line.length() + 1 + word.length() <= width) {
                    line.append(' ').append(word);
                } else {
                    out.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                }
            }
            if (line.length() > 0) {
                out.add(line.toString());
            }
        }
        return out;
    }

    /** Lebar awal sebelum WindowSizeMessage datang; ikut $COLUMNS bila tersedia. */
    private static int initialWidth() {
        try {
            int cols = Integer.parseInt(System.getenv("COLUMNS"));
            return Math.max(40, Math.min(cols - 2, 110));
        } catch (Exception ignored) {
            return 74;
        }
    }

    private static String pad(String text, int width) {
        return text.length() >= width ? text : text + repeat(" ", width - text.length());
    }

    private static String repeat(String unit, int times) {
        return times <= 0 ? "" : unit.repeat(times);
    }

    /** Panjang teks tanpa menghitung escape sequence ANSI. */
    private static int visibleLength(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    // ------------------------------------------------------------------
    // Pemanggilan API
    // ------------------------------------------------------------------

    private Command createApiCallCommand(List<JSONObject> history) {
        return () -> {
            try {
                JSONObject body = new JSONObject();
                body.put("provider", "auto");
                body.put("model", "auto");
                JSONArray messagesArray = new JSONArray();
                for (JSONObject item : history) {
                    messagesArray.put(item);
                }
                body.put("messages", messagesArray);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_TOKEN)
                        .timeout(Duration.ofSeconds(45))
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONObject resJson = new JSONObject(response.body());
                    JSONArray choices = resJson.optJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        String botReply = choices.getJSONObject(0).getJSONObject("message").getString("content");
                        return new BotReplyMessage(botReply);
                    } else {
                        return new BotErrorMessage("Format response API tidak memiliki pilihan jawaban ('choices').");
                    }
                } else {
                    return new BotErrorMessage("HTTP " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                return new BotErrorMessage("Gagal memanggil API: " + e.getMessage());
            }
        };
    }

    /**
     * Entry modular yang dipanggil oleh CliManager
     */
    public static void run() {
        // `gradle run` mem-pipe stdin/stdout sehingga JLine hanya dapat "dumb terminal":
        // warna dibuang, ukuran layar tidak terdeteksi, dan tampilan jadi berantakan.
        SetDotenv();
        if (System.console() == null) {
            System.out.println();
            System.out.println("  Terminal saat ini tidak mendukung mode TUI (dumb terminal).");
            System.out.println("  Biasanya ini terjadi karena dijalankan lewat 'gradle run'.");
            System.out.println();
            System.out.println("  Jalankan lewat script berikut agar dapat TTY asli:");
            System.out.println("      ./jalankan-cli.sh");
            System.out.println();
            return;
        }
        new Program(new ChatBotCLI()).withAltScreen().run();
    }

    public static void main(String[] args) {
        run();
    }
}
