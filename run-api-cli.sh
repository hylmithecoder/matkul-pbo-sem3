#!/usr/bin/env bash
# Jalankan mode CLI/TUI langsung di terminal (bukan lewat `gradle run`).
#
# `gradle run` mem-pipe stdin/stdout sehingga JLine jatuh ke "dumb terminal":
# warna hilang, ukuran layar tidak terdeteksi, dan TUI jadi berantakan.
# Script ini mem-build lalu menjalankan JVM langsung sehingga dapat TTY asli.
set -e

cd "$(dirname "$0")"
gradle installDist -q "$@"

# Pakai JDK yang sama dengan yang dipakai Gradle (java di PATH bisa lebih lama
# dan menolak bytecode Java 21).
JAVA_HOME="${JAVA_HOME_OVERRIDE:-$(gradle -q printJavaHome)}"

exec "$JAVA_HOME/bin/java" -cp "build/install/sem3matkul/lib/*" sem3matkul.CliLauncher
