package sem3matkul.pertemuan3.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sem3matkul.pertemuan3.model.*;

/**
 * Controller untuk Pertemuan 3: Java Collections Framework.
 * Mengelola koleksi:
 * 1. ListArray (ArrayList)
 * 2. Vec (Vector)
 * 3. Linked (LinkedList)
 * 4. Queue
 * 5. Stack
 * 6. Dequeue
 * 7. Map (Key-Value)
 * 8. TreeSet (Ascending A-Z)
 * 9. TreeSet Descending (Z-A)
 * 10. HashSet
 */
public class CollectionController {

    // === Komponen FXML ===
    @FXML private ComboBox<String> cbModel;
    @FXML private TextField tfInput;
    @FXML private Button btnTambah;
    @FXML private Button btnHapus;
    @FXML private Button btnCari;
    @FXML private Button btnClear;
    @FXML private VBox customActionBox;

    @FXML private Label lblModelInfo;
    @FXML private Label lblTotalItem;
    @FXML private Label lblStatusMessage;

    @FXML private ListView<String> lvData;
    @FXML private TextArea taLog;

    // === Objek Model Koleksi ===
    private final ListArray modelListArray = new ListArray();
    private final Vec modelVec = new Vec();
    private final Linked modelLinked = new Linked();
    private final Queue modelQueue = new Queue();
    private final Stack modelStack = new Stack();
    private final Dequeue modelDequeue = new Dequeue();
    private final Map modelMap = new Map();
    private final Treeset modelTreeset = new Treeset();
    private final TreesetDescend modelTreesetDesc = new TreesetDescend();
    private final Hashset modelHashset = new Hashset();

    // Observable list untuk tampilan ListView
    private final ObservableList<String> displayItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lvData.setItems(displayItems);

        // Inisialisasi daftar model sesuai urutan
        cbModel.getItems().addAll(
                "1. ListArray (ArrayList)",
                "2. Vec (Vector)",
                "3. Linked (LinkedList)",
                "4. Queue",
                "5. Stack",
                "6. Dequeue",
                "7. Map",
                "8. TreeSet",
                "9. TreeSet Descending",
                "10. HashSet"
        );

        // Pilih model pertama sebagai default
        cbModel.getSelectionModel().selectFirst();
        cbModel.setOnAction(e -> handleModelChange());

        // Setup awal
        handleModelChange();
        log("Sistem Pertemuan 3 (Collection Framework) siap digunakan.");
    }

    /**
     * Dipanggil saat pengguna memilih jenis model koleksi di ComboBox.
     */
    private void handleModelChange() {
        int index = cbModel.getSelectionModel().getSelectedIndex();
        String selected = cbModel.getValue();
        lblModelInfo.setText("Model: " + selected);

        // Sesuaikan tombol dinamis di customActionBox sesuai karakteristik model
        setupCustomButtons(index);

        // Perbarui prompt input agar ramah pengguna
        if (index == 6) {
            tfInput.setPromptText("Format: Key=Value atau Key: Value");
        } else {
            tfInput.setPromptText("contoh: Elemen Data");
        }

        // Perbarui tampilan ListView & Status
        refreshDisplay();
        showStatus("Beralih ke model: " + selected, false);
    }

    /**
     * Menyediakan tombol aksi spesifik sesuai koleksi terpilih di Java
     */
    private void setupCustomButtons(int modelIndex) {
        customActionBox.getChildren().clear();

        switch (modelIndex) {
            case 2 -> { // Linked (LinkedList)
                HBox box = new HBox(8);
                Button btnAddFirst = new Button("➕ Add First");
                btnAddFirst.getStyleClass().addAll("btn", "btn-secondary");
                btnAddFirst.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnAddFirst, javafx.scene.layout.Priority.ALWAYS);
                btnAddFirst.setOnAction(e -> handleAddFirst());

                Button btnAddLast = new Button("➕ Add Last");
                btnAddLast.getStyleClass().addAll("btn", "btn-secondary");
                btnAddLast.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnAddLast, javafx.scene.layout.Priority.ALWAYS);
                btnAddLast.setOnAction(e -> handleAddLast());

                box.getChildren().addAll(btnAddFirst, btnAddLast);
                customActionBox.getChildren().add(box);
            }
            case 4 -> { // Stack
                HBox box = new HBox(8);
                Button btnPeek = new Button("👁️ Peek (Atas)");
                btnPeek.getStyleClass().addAll("btn", "btn-secondary");
                btnPeek.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnPeek, javafx.scene.layout.Priority.ALWAYS);
                btnPeek.setOnAction(e -> {
                    String top = modelStack.peek();
                    if (top != null) {
                        showStatus("Top Stack: " + top, false);
                        log("stack.peek() -> " + top);
                    } else {
                        showStatus("Stack masih kosong!", true);
                    }
                });
                box.getChildren().add(btnPeek);
                customActionBox.getChildren().add(box);
            }
            case 5 -> { // Dequeue
                HBox box = new HBox(8);
                Button btnPollFirst = new Button("🗑️ Poll First");
                btnPollFirst.getStyleClass().addAll("btn", "btn-secondary");
                btnPollFirst.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnPollFirst, javafx.scene.layout.Priority.ALWAYS);
                btnPollFirst.setOnAction(e -> {
                    String val = modelDequeue.pollFirst();
                    if (val != null) {
                        showStatus("Poll First: " + val, false);
                        log("dequeue.pollFirst() -> " + val);
                        refreshDisplay();
                    } else {
                        showStatus("Dequeue kosong!", true);
                    }
                });

                Button btnPollLast = new Button("🗑️ Poll Last");
                btnPollLast.getStyleClass().addAll("btn", "btn-secondary");
                btnPollLast.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnPollLast, javafx.scene.layout.Priority.ALWAYS);
                btnPollLast.setOnAction(e -> {
                    String val = modelDequeue.pollLast();
                    if (val != null) {
                        showStatus("Poll Last: " + val, false);
                        log("dequeue.pollLast() -> " + val);
                        refreshDisplay();
                    } else {
                        showStatus("Dequeue kosong!", true);
                    }
                });

                box.getChildren().addAll(btnPollFirst, btnPollLast);
                customActionBox.getChildren().add(box);
            }
            case 6 -> { // Map (Key-Value)
                HBox box1 = new HBox(8);

                Button btnFormatAll = new Button("📋 Key => Value");
                btnFormatAll.getStyleClass().addAll("btn", "btn-secondary");
                btnFormatAll.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnFormatAll, javafx.scene.layout.Priority.ALWAYS);
                btnFormatAll.setOnAction(e -> {
                    displayItems.clear();
                    for (java.util.Map.Entry<String, String> entry : modelMap.entrySet()) {
                        displayItems.add(entry.getKey() + " => " + entry.getValue());
                    }
                    showStatus("Menampilkan seluruh pasangan Key => Value.", false);
                    log("map.entrySet() -> Format Key => Value");
                });

                Button btnShowKeys = new Button("🔑 Keys");
                btnShowKeys.getStyleClass().addAll("btn", "btn-secondary");
                btnShowKeys.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnShowKeys, javafx.scene.layout.Priority.ALWAYS);
                btnShowKeys.setOnAction(e -> {
                    displayItems.clear();
                    displayItems.addAll(modelMap.keySet());
                    showStatus("Menampilkan hanya daftar Key dalam Map.", false);
                    log("map.keySet() -> Tampil daftar Key");
                });

                box1.getChildren().addAll(btnFormatAll, btnShowKeys);

                HBox box2 = new HBox(8);
                Button btnGetVal = new Button("🔍 Get Value (by Key)");
                btnGetVal.getStyleClass().addAll("btn", "btn-secondary");
                btnGetVal.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnGetVal, javafx.scene.layout.Priority.ALWAYS);
                btnGetVal.setOnAction(e -> {
                    String query = tfInput.getText().trim();
                    if (query.contains("=")) query = query.split("=")[0].trim();
                    if (query.contains(":")) query = query.split(":")[0].trim();
                    if (query.isEmpty()) {
                        showStatus("⚠️ Masukkan nama key pada input untuk mengambil nilainya!", true);
                        return;
                    }
                    if (modelMap.containsKey(query)) {
                        String val = modelMap.get(query);
                        showStatus("🔑 Key: " + query + " | 📦 Value: " + val, false);
                        log("map.get(\"" + query + "\") -> " + val);
                    } else {
                        showStatus("Key '" + query + "' tidak ditemukan dalam Map!", true);
                    }
                });

                box2.getChildren().add(btnGetVal);
                customActionBox.getChildren().addAll(box1, box2);
            }
            case 7 -> { // TreeSet
                HBox box1 = new HBox(8);

                Button btnSortAsc = new Button("🔤 Urut Abjad (A-Z)");
                btnSortAsc.getStyleClass().addAll("btn", "btn-secondary");
                btnSortAsc.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnSortAsc, javafx.scene.layout.Priority.ALWAYS);
                btnSortAsc.setOnAction(e -> {
                    displayItems.clear();
                    displayItems.addAll(modelTreeset.getTreeSet());
                    showStatus("Menampilkan TreeSet urut abjad normal (A-Z).", false);
                    log("treeset (A-Z) -> Urut normal");
                });

                Button btnSortDesc = new Button("🔤 Urut Terbalik (Z-A)");
                btnSortDesc.getStyleClass().addAll("btn", "btn-secondary");
                btnSortDesc.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnSortDesc, javafx.scene.layout.Priority.ALWAYS);
                btnSortDesc.setOnAction(e -> {
                    displayItems.clear();
                    displayItems.addAll(modelTreeset.getDescending());
                    showStatus("Menampilkan TreeSet urut abjad terbalik (Z-A).", false);
                    log("treeset.descendingSet() -> Urut terbalik Z-A");
                });

                box1.getChildren().addAll(btnSortAsc, btnSortDesc);

                HBox box2 = new HBox(8);
                Button btnFirst = new Button("🔝 First (Awal)");
                btnFirst.getStyleClass().addAll("btn", "btn-secondary");
                btnFirst.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnFirst, javafx.scene.layout.Priority.ALWAYS);
                btnFirst.setOnAction(e -> {
                    String f = modelTreeset.first();
                    showStatus(f != null ? "Elemen Pertama (First): " + f : "TreeSet kosong!", f == null);
                    log("treeset.first() -> " + f);
                });

                Button btnLast = new Button("🔚 Last (Akhir)");
                btnLast.getStyleClass().addAll("btn", "btn-secondary");
                btnLast.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnLast, javafx.scene.layout.Priority.ALWAYS);
                btnLast.setOnAction(e -> {
                    String l = modelTreeset.last();
                    showStatus(l != null ? "Elemen Terakhir (Last): " + l : "TreeSet kosong!", l == null);
                    log("treeset.last() -> " + l);
                });

                box2.getChildren().addAll(btnFirst, btnLast);
                customActionBox.getChildren().addAll(box1, box2);
            }
            case 8 -> { // TreeSet Descending
                HBox box1 = new HBox(8);

                Button btnSortDesc = new Button("🔤 Urut Terbalik (Z-A)");
                btnSortDesc.getStyleClass().addAll("btn", "btn-secondary");
                btnSortDesc.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnSortDesc, javafx.scene.layout.Priority.ALWAYS);
                btnSortDesc.setOnAction(e -> {
                    displayItems.clear();
                    displayItems.addAll(modelTreesetDesc.getTreeSetDesc());
                    showStatus("Menampilkan TreeSet Descending (Z-A).", false);
                    log("treesetDesc (Z-A) -> Urut terbalik");
                });

                Button btnSortAsc = new Button("🔤 Urut Abjad (A-Z)");
                btnSortAsc.getStyleClass().addAll("btn", "btn-secondary");
                btnSortAsc.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnSortAsc, javafx.scene.layout.Priority.ALWAYS);
                btnSortAsc.setOnAction(e -> {
                    displayItems.clear();
                    displayItems.addAll(modelTreesetDesc.getAscending());
                    showStatus("Menampilkan TreeSet Descending urut normal (A-Z).", false);
                    log("treesetDesc.descendingSet() -> Urut A-Z");
                });

                box1.getChildren().addAll(btnSortDesc, btnSortAsc);

                HBox box2 = new HBox(8);
                Button btnFirst = new Button("🔝 First (Awal)");
                btnFirst.getStyleClass().addAll("btn", "btn-secondary");
                btnFirst.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnFirst, javafx.scene.layout.Priority.ALWAYS);
                btnFirst.setOnAction(e -> {
                    String f = modelTreesetDesc.first();
                    showStatus(f != null ? "Elemen Pertama (First): " + f : "TreeSet kosong!", f == null);
                    log("treesetDesc.first() -> " + f);
                });

                Button btnLast = new Button("🔚 Last (Akhir)");
                btnLast.getStyleClass().addAll("btn", "btn-secondary");
                btnLast.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnLast, javafx.scene.layout.Priority.ALWAYS);
                btnLast.setOnAction(e -> {
                    String l = modelTreesetDesc.last();
                    showStatus(l != null ? "Elemen Terakhir (Last): " + l : "TreeSet kosong!", l == null);
                    log("treesetDesc.last() -> " + l);
                });

                box2.getChildren().addAll(btnFirst, btnLast);
                customActionBox.getChildren().addAll(box1, box2);
            }
            case 9 -> { // HashSet
                HBox box = new HBox(8);

                Button btnRemoveIf = new Button("🗑️ Remove If (Input)");
                btnRemoveIf.getStyleClass().addAll("btn", "btn-secondary");
                btnRemoveIf.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnRemoveIf, javafx.scene.layout.Priority.ALWAYS);
                btnRemoveIf.setOnAction(e -> {
                    String query = tfInput.getText().trim();
                    if (query.isEmpty()) {
                        showStatus("⚠️ Masukkan teks pada input sebagai kriteria Remove If!", true);
                        return;
                    }
                    boolean removed = modelHashset.removeIf(item -> item.toLowerCase().contains(query.toLowerCase()));
                    if (removed) {
                        showStatus("✅ Elemen mengandung '" + query + "' berhasil dihapus.", false);
                        log("hashset.removeIf(item -> item.contains(\"" + query + "\"))");
                        tfInput.clear();
                        refreshDisplay();
                    } else {
                        showStatus("Tidak ada elemen yang cocok dengan '" + query + "'.", true);
                    }
                });

                Button btnRemoveSelected = new Button("Remove Selected");
                btnRemoveSelected.getStyleClass().addAll("btn", "btn-secondary");
                btnRemoveSelected.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnRemoveSelected, javafx.scene.layout.Priority.ALWAYS);
                btnRemoveSelected.setOnAction(e -> {
                    String selected = lvData.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        modelHashset.remove(selected);
                        showStatus("Dihapus: " + selected, false);
                        log("hashset.remove(\"" + selected + "\")");
                        refreshDisplay();
                    } else {
                        showStatus("Pilih elemen dari list untuk dihapus!", true);
                    }
                });

                box.getChildren().addAll(btnRemoveIf, btnRemoveSelected);
                customActionBox.getChildren().add(box);
            }
        }
    }

    // === Action Handler Utama ===

    @FXML
    private void handleTambah() {
        String data = tfInput.getText().trim();
        if (data.isEmpty()) {
            showStatus("⚠️ Harap isi input elemen terlebih dahulu!", true);
            return;
        }

        int index = cbModel.getSelectionModel().getSelectedIndex();
        switch (index) {
            case 0 -> {
                modelListArray.add(data);
                log("listArray.add(\"" + data + "\") | size = " + modelListArray.size());
            }
            case 1 -> {
                modelVec.add(data);
                log("vec.add(\"" + data + "\") | size = " + modelVec.size());
            }
            case 2 -> {
                modelLinked.add(data);
                log("linked.add(\"" + data + "\") | size = " + modelLinked.size());
            }
            case 3 -> {
                modelQueue.offer(data);
                log("queue.offer(\"" + data + "\") | size = " + modelQueue.size());
            }
            case 4 -> {
                modelStack.push(data);
                log("stack.push(\"" + data + "\") | size = " + modelStack.size());
            }
            case 5 -> {
                modelDequeue.addLast(data);
                log("dequeue.addLast(\"" + data + "\") | size = " + modelDequeue.size());
            }
            case 6 -> { // Map (Key=Value atau Key: Value)
                String key, value;
                if (data.contains("=")) {
                    String[] parts = data.split("=", 2);
                    key = parts[0].trim();
                    value = parts.length > 1 ? parts[1].trim() : "";
                } else if (data.contains(":")) {
                    String[] parts = data.split(":", 2);
                    key = parts[0].trim();
                    value = parts.length > 1 ? parts[1].trim() : "";
                } else {
                    key = data;
                    value = "Value-" + (modelMap.size() + 1);
                }
                modelMap.put(key, value);
                log("map.put(\"" + key + "\", \"" + value + "\") | size = " + modelMap.size());
            }
            case 7 -> { // TreeSet
                modelTreeset.add(data);
                log("treeset.add(\"" + data + "\") | size = " + modelTreeset.size());
            }
            case 8 -> { // TreeSet Descending
                modelTreesetDesc.add(data);
                log("treesetDesc.add(\"" + data + "\") | size = " + modelTreesetDesc.size());
            }
            case 9 -> { // HashSet
                modelHashset.add(data);
                log("hashset.add(\"" + data + "\") | size = " + modelHashset.size());
            }
        }

        tfInput.clear();
        showStatus("✅ Elemen '" + data + "' berhasil ditambahkan.", false);
        refreshDisplay();
    }

    @FXML
    private void handleHapus() {
        int index = cbModel.getSelectionModel().getSelectedIndex();
        String selectedItem = lvData.getSelectionModel().getSelectedItem();

        switch (index) {
            case 0 -> {
                if (selectedItem != null) {
                    modelListArray.remove(selectedItem);
                    log("listArray.remove(\"" + selectedItem + "\")");
                } else if (modelListArray.size() > 0) {
                    String removed = modelListArray.remove(modelListArray.size() - 1);
                    log("listArray.remove(last) -> " + removed);
                } else {
                    showStatus("⚠️ ListArray kosong!", true);
                    return;
                }
            }
            case 1 -> {
                if (selectedItem != null) {
                    modelVec.remove(selectedItem);
                    log("vec.remove(\"" + selectedItem + "\")");
                } else if (modelVec.size() > 0) {
                    String removed = modelVec.get(modelVec.size() - 1);
                    modelVec.remove(removed);
                    log("vec.remove(last) -> " + removed);
                } else {
                    showStatus("⚠️ Vector kosong!", true);
                    return;
                }
            }
            case 2 -> {
                String removed = modelLinked.removeFirst();
                if (removed != null) {
                    log("linked.removeFirst() -> " + removed);
                } else {
                    showStatus("⚠️ LinkedList kosong!", true);
                    return;
                }
            }
            case 3 -> {
                String polled = modelQueue.poll();
                if (polled != null) {
                    log("queue.poll() (FIFO) -> " + polled);
                } else {
                    showStatus("⚠️ Queue kosong!", true);
                    return;
                }
            }
            case 4 -> {
                String popped = modelStack.pop();
                if (popped != null) {
                    log("stack.pop() (LIFO) -> " + popped);
                } else {
                    showStatus("⚠️ Stack kosong!", true);
                    return;
                }
            }
            case 5 -> {
                String polled = modelDequeue.pollFirst();
                if (polled != null) {
                    log("dequeue.pollFirst() -> " + polled);
                } else {
                    showStatus("⚠️ Dequeue kosong!", true);
                    return;
                }
            }
            case 6 -> { // Map
                String keyToRemove = null;
                if (selectedItem != null) {
                    if (selectedItem.contains(" => ")) {
                        keyToRemove = selectedItem.split(" => ", 2)[0].trim();
                    } else {
                        keyToRemove = selectedItem.trim();
                    }
                } else {
                    String data = tfInput.getText().trim();
                    if (!data.isEmpty()) {
                        keyToRemove = data.contains("=") ? data.split("=")[0].trim() : (data.contains(":") ? data.split(":")[0].trim() : data);
                    } else if (modelMap.size() > 0) {
                        keyToRemove = modelMap.getMap().keySet().iterator().next();
                    }
                }
                if (keyToRemove != null && modelMap.containsKey(keyToRemove)) {
                    String val = modelMap.remove(keyToRemove);
                    log("map.remove(\"" + keyToRemove + "\") -> " + val);
                } else {
                    showStatus("⚠️ Key '" + keyToRemove + "' tidak ditemukan dalam Map!", true);
                    return;
                }
            }
            case 7 -> { // TreeSet
                String target = selectedItem != null ? selectedItem : (!tfInput.getText().trim().isEmpty() ? tfInput.getText().trim() : (modelTreeset.size() > 0 ? modelTreeset.getTreeSet().iterator().next() : null));
                if (target != null && modelTreeset.contains(target)) {
                    modelTreeset.remove(target);
                    log("treeset.remove(\"" + target + "\")");
                } else {
                    showStatus("⚠️ Elemen tidak ditemukan di TreeSet!", true);
                    return;
                }
            }
            case 8 -> { // TreeSet Descending
                String target = selectedItem != null ? selectedItem : (!tfInput.getText().trim().isEmpty() ? tfInput.getText().trim() : (modelTreesetDesc.size() > 0 ? modelTreesetDesc.getTreeSet().iterator().next() : null));
                if (target != null && modelTreesetDesc.contains(target)) {
                    modelTreesetDesc.remove(target);
                    log("treesetDesc.remove(\"" + target + "\")");
                } else {
                    showStatus("⚠️ Elemen tidak ditemukan di TreeSet Descending!", true);
                    return;
                }
            }
            case 9 -> { // HashSet
                if (selectedItem != null) {
                    modelHashset.remove(selectedItem);
                    log("hashset.remove(\"" + selectedItem + "\")");
                } else {
                    String data = tfInput.getText().trim();
                    if (!data.isEmpty()) {
                        String removed = modelHashset.remove(data);
                        if (removed != null) {
                            log("hashset.remove(\"" + data + "\")");
                        } else {
                            showStatus("⚠️ Elemen '" + data + "' tidak ada di HashSet!", true);
                            return;
                        }
                    } else if (modelHashset.size() > 0) {
                        String any = modelHashset.getHashset().iterator().next();
                        modelHashset.remove(any);
                        log("hashset.remove() -> " + any);
                    } else {
                        showStatus("⚠️ Hashset kosong!", true);
                        return;
                    }
                }
            }
        }

        showStatus("🗑️ Elemen berhasil dihapus.", false);
        refreshDisplay();
    }

    @FXML
    private void handleCari() {
        int index = cbModel.getSelectionModel().getSelectedIndex();
        String selectedItem = lvData.getSelectionModel().getSelectedItem();

        switch (index) {
            case 3 -> {
                String peek = modelQueue.peek();
                showStatus(peek != null ? "Head Queue: " + peek : "Queue kosong!", peek == null);
                log("queue.peek() -> " + peek);
            }
            case 4 -> {
                String top = modelStack.peek();
                showStatus(top != null ? "Top Stack: " + top : "Stack kosong!", top == null);
                log("stack.peek() -> " + top);
            }
            case 5 -> {
                String first = modelDequeue.peekFirst();
                showStatus(first != null ? "First Dequeue: " + first : "Dequeue kosong!", first == null);
                log("dequeue.peekFirst() -> " + first);
            }
            case 6 -> { // Map
                String query = tfInput.getText().trim();
                if (query.contains("=")) query = query.split("=")[0].trim();
                if (query.contains(":")) query = query.split(":")[0].trim();
                if (!query.isEmpty()) {
                    if (modelMap.containsKey(query)) {
                        String val = modelMap.get(query);
                        showStatus("Key '" + query + "' ditemukan! Value = " + val, false);
                        log("map.get(\"" + query + "\") -> " + val);
                    } else {
                        showStatus("Key '" + query + "' tidak ditemukan dalam Map.", true);
                        log("map.containsKey(\"" + query + "\") -> false");
                    }
                } else {
                    showStatus("Ketik nama key di input untuk mencari value dalam Map!", true);
                }
            }
            case 7 -> { // TreeSet
                String query = tfInput.getText().trim();
                String target = !query.isEmpty() ? query : selectedItem;
                if (target != null && !target.isEmpty()) {
                    boolean found = modelTreeset.contains(target);
                    showStatus(found ? "Ditemukan di TreeSet: " + target : "Tidak ada dalam TreeSet: " + target, !found);
                    log("treeset.contains(\"" + target + "\") -> " + found);
                } else {
                    showStatus("Ketik nilai di input untuk dicari (TreeSet)!", true);
                }
            }
            case 8 -> { // TreeSet Descending
                String query = tfInput.getText().trim();
                String target = !query.isEmpty() ? query : selectedItem;
                if (target != null && !target.isEmpty()) {
                    boolean found = modelTreesetDesc.contains(target);
                    showStatus(found ? "Ditemukan di TreeSet Desc: " + target : "Tidak ada dalam TreeSet Desc: " + target, !found);
                    log("treesetDesc.contains(\"" + target + "\") -> " + found);
                } else {
                    showStatus("Ketik nilai di input untuk dicari (TreeSet Desc)!", true);
                }
            }
            case 9 -> { // HashSet
                String query = tfInput.getText().trim();
                String target = !query.isEmpty() ? query : selectedItem;
                if (target != null && !target.isEmpty()) {
                    boolean found = modelHashset.contains(target);
                    showStatus(found ? "Ditemukan di HashSet: " + target : "Tidak ada dalam HashSet: " + target, !found);
                    log("hashset.contains(\"" + target + "\") -> " + found);
                } else {
                    showStatus("Ketik nilai di input atau pilih item untuk dicek (contains)!", true);
                }
            }
            default -> {
                String query = tfInput.getText().trim();
                if (query.isEmpty()) {
                    showStatus("Ketik nama elemen di input untuk dicari!", true);
                } else {
                    boolean found = displayItems.contains(query);
                    showStatus(found ? "Ditemukan: " + query : "Elemen tidak ada dalam koleksi.", !found);
                    log("Pencarian elemen: '" + query + "' -> " + (found ? "Ada" : "Tidak ada"));
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        int index = cbModel.getSelectionModel().getSelectedIndex();
        switch (index) {
            case 0 -> modelListArray.clear();
            case 1 -> modelVec.clear();
            case 2 -> modelLinked.clear();
            case 3 -> modelQueue.clear();
            case 4 -> modelStack.clear();
            case 5 -> modelDequeue.clear();
            case 6 -> modelMap.clear();
            case 7 -> modelTreeset.clear();
            case 8 -> modelTreesetDesc.clear();
            case 9 -> modelHashset.clear();
        }
        log("Koleksi di-reset (clear).");
        showStatus("🔄 Seluruh data koleksi telah dikosongkan.", false);
        refreshDisplay();
    }

    private void handleAddFirst() {
        String data = tfInput.getText().trim();
        if (data.isEmpty()) {
            showStatus("Harap isi input!", true);
            return;
        }
        modelLinked.addFirst(data);
        tfInput.clear();
        log("linked.addFirst(\"" + data + "\")");
        refreshDisplay();
    }

    private void handleAddLast() {
        String data = tfInput.getText().trim();
        if (data.isEmpty()) {
            showStatus("Harap isi input!", true);
            return;
        }
        modelLinked.addLast(data);
        tfInput.clear();
        log("linked.addLast(\"" + data + "\")");
        refreshDisplay();
    }

    // === Helper Methods ===

    private void refreshDisplay() {
        displayItems.clear();
        int index = cbModel.getSelectionModel().getSelectedIndex();
        int size = 0;

        switch (index) {
            case 0 -> {
                displayItems.addAll(modelListArray.getList());
                size = modelListArray.size();
            }
            case 1 -> {
                displayItems.addAll(modelVec.getVector());
                size = modelVec.size();
            }
            case 2 -> {
                displayItems.addAll(modelLinked.getLinkedList());
                size = modelLinked.size();
            }
            case 3 -> {
                displayItems.addAll(modelQueue.getQueue());
                size = modelQueue.size();
            }
            case 4 -> {
                displayItems.addAll(modelStack.getStack());
                size = modelStack.size();
            }
            case 5 -> {
                displayItems.addAll(modelDequeue.getDeque());
                size = modelDequeue.size();
            }
            case 6 -> {
                for (java.util.Map.Entry<String, String> entry : modelMap.entrySet()) {
                    displayItems.add(entry.getKey() + " => " + entry.getValue());
                }
                size = modelMap.size();
            }
            case 7 -> {
                displayItems.addAll(modelTreeset.getTreeSet());
                size = modelTreeset.size();
            }
            case 8 -> {
                displayItems.addAll(modelTreesetDesc.getTreeSetDesc());
                size = modelTreesetDesc.size();
            }
            case 9 -> {
                displayItems.addAll(modelHashset.getHashset());
                size = modelHashset.size();
            }
        }

        lblTotalItem.setText("Total Elemen: " + size);
    }

    private void showStatus(String message, boolean isError) {
        lblStatusMessage.setText(message);
        if (isError) {
            lblStatusMessage.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblStatusMessage.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    private void log(String message) {
        taLog.appendText("> " + message + "\n");
    }
}