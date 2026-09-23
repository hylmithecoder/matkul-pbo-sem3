package sem3matkul.core;

import java.util.*;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CliHelper {
    public static void print(String message) {
        System.out.println(message);
    }

    public static void printWithoutNewline(String message) {
        System.out.print(message);
    }

    public static void error(String message) {
        System.out.println("Error: " + message);
    }

    private static final Scanner SCANNER = new Scanner(System.in);

    public static Scanner getScanner() {
        return SCANNER;
    }

    public static int inputInt() {
        while (true) {
            if (!SCANNER.hasNextLine()) {
                return -1;
            }
            String line = SCANNER.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                error("Please input only integer number");
            }
        }
    }

    public static String inputString() {
        while (true) {
            if (!SCANNER.hasNextLine()) {
                return "";
            }
            String input = SCANNER.nextLine();
            if (input.isBlank()) {
                error("Please input a string");
                continue;
            }
            return input;
        }
    }

    /**
     * Menggantikan System.out.print sepenuhnya dan melacak lokasi file serta baris kode.
     */
    public record Log() {
        private static final FileOutputStream CONSOLE_STREAM = new FileOutputStream(FileDescriptor.out);

        // Gunakan RETAIN_CLASS_REFERENCE
        private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        /**
         * Metode cetak kustom yang otomatis menyertakan informasi file dan baris.
         */
        public static void print(Object obj) {
            String location = getCallerLocation();
            writeToConsole(location + " " + String.valueOf(obj));
        }

        public static void print(String text) {
            String location = getCallerLocation();
            writeToConsole(location + " " + (text != null ? text : "null"));
        }

        public static void println(Object obj) {
            String location = getCallerLocation();
            writeToConsole(location + " " + String.valueOf(obj) + "\n");
        }

        /**
         * Mengambil informasi nama file dan baris kode dari pemanggil (Caller).
         */
        private static String getCallerLocation() {
            return WALKER.walk(frames -> frames
                    .skip(2) // Lewati frame getCallerLocation() dan frame print() itu sendiri
                    .findFirst()
                    .map(frame -> "[" + frame.getFileName() + ":" + frame.getLineNumber() + "] ->")
                    .orElse("[Unknown Source] ->"));
        }

        /**
         * Menulis byte langsung ke konsol tingkat OS (Tanpa System.out).
         */
        private static void writeToConsole(String message) {
            try {
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                CONSOLE_STREAM.write(bytes);
                CONSOLE_STREAM.flush();
            } catch (IOException e) {
                try {
                    new FileOutputStream(FileDescriptor.err).write("Gagal mencetak log".getBytes());
                } catch (IOException ignored) {}
            }
        }
    }
}
