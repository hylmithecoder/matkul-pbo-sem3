package sem3matkul.pertemuan4.cli;

import java.util.*;
import java.time.LocalDate;

public class ApiSederhana {
    public void main() {
        System.out.print("Masukkan Nama anda: ");
        Scanner in = new Scanner(System.in);
        String nama = in.nextLine();

        AnalizeName(nama);

        // Method-method pada class Math
        System.out.println(Math.max(10, 25));        // 25
        System.out.println(Math.sqrt(64));           // 8.0
        System.out.println(Math.round(3.6));         // 4

        // java.time: mengambil tanggal hari ini
        LocalDate hariIni = LocalDate.now();
        System.out.println("Tanggal hari ini: " + hariIni);
    }

    void AnalizeName(String nama){
        // Method-method pada class String
        System.out.println(nama.length());          // 17
        System.out.println(nama.toUpperCase());      // PEMROGRAMAN JAVA
        System.out.println(nama.substring(0, 5));   // Pemrograman
        System.out.println(nama.contains("Mahdi"));   // true
    }
}
