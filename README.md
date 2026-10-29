# AutoFleet Direct (VehicleRent)
> Sistem Manajemen Operasional Rental Kendaraan dan Pemeliharaan Bengkel Internal Terintegrasi  
> Dikembangkan sebagai proyek aplikasi Android Native berbasis Kotlin.

---

## 1. Deskripsi Sistem

**AutoFleet Direct (VehicleRent)** adalah aplikasi sistem informasi operasional mobilitas yang dirancang untuk menjembatani dua kebutuhan bisnis utama:
1. **Layanan Pelanggan (Front-Office):** Menampilkan ketersediaan armada secara real-time, transparansi tarif sewa harian, paket *Smart Bundle* (perlengkapan perjalanan), opsi sopir/lepas kunci, hingga penerbitan bukti reservasi digital.
2. **Operasional Bengkel Internal & QC (Back-Office):** Mengelola siklus serah-terima unit, inspeksi fisik kendaraan sebelum dan sesudah sewa, pencatatan telemetri bahan bakar dan odometer, kalkulasi otomatis denda/kompensasi, antrean bengkel berbasis 6-stall kapasitas, serta kamus pemecahan masalah mesin berbasis standar diagnostik internasional **SAE J2012 (OBD-II)**.

Aplikasi ini dibangun menggunakan arsitektur **Dual-Role**, memungkinkan pergantian mode secara langsung (*runtime role switching*) antara **Mode Pelanggan** dan **Mode Pemangku Usaha (Fleet Manager & QC Bengkel)** tanpa perlu keluar (*logout*) dari aplikasi.

---

## 2. Pemetaan Capaian Praktikum: Tugas 1 s/d Tugas 7

Seluruh materi akademik silabus Pemrograman Mobile (Pertemuan 1 hingga 7) diimplementasikan secara komprehensif ke dalam modul fungsional:

| Tugas / Pertemuan | Topik Materi Akademik | Berkas Implementasi | Fitur Nyata dalam Aplikasi |
| :--- | :--- | :--- | :--- |
| **Tugas 1** | Dasar Android, Activity Lifecycle, TextView, Button, Toast | [`MainActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/MainActivity.kt)<br>[`BerandaFleetActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/BerandaFleetActivity.kt) | Pengelolaan siklus hidup Activity (`onCreate`, `onResume`), kustomisasi Top App Bar, penyesuaian status bar insets (`fitsSystemWindows`), serta notifikasi interaktif menggunakan Toast dan Snackbar. |
| **Tugas 2** | UI Controls & Layouts (LinearLayout, ScrollView, CheckBox, RadioGroup, Spinner, Segmented Controls) | [`activity_beranda_fleet.xml`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/res/layout/activity_beranda_fleet.xml)<br>[`activity_form_pemesanan.xml`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/res/layout/activity_form_pemesanan.xml)<br>[`activity_inspeksi_pickup.xml`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/res/layout/activity_inspeksi_pickup.xml) | Penyusunan antarmuka terstruktur dengan nesting yang efisien, kontrol tersegmentasi untuk metode pengemudi (Lepas Kunci vs +Sopir), pemilihan level bahan bakar (25%–100%), dan CheckBox checklist kelengkapan dokumen armada (STNK, Toolkit, P3K). |
| **Tugas 3** | Intent (Explicit Intent & Implicit Intent) | [`DetailReservasiActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/DetailReservasiActivity.kt)<br>[`FormPemesananActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/FormPemesananActivity.kt) | **Explicit Intent**: Pengiriman objek model data (`VehicleModel`, `ReservationModel`) antar Activity.<br>**Implicit Intent**: Kirim struk reservasi via WhatsApp (`ACTION_SEND`), panggilan telepon ke penyewa (`ACTION_DIAL`), dan navigasi pool armada via Google Maps (`ACTION_VIEW`). |
| **Tugas 4** | Event Handling, Input Validation, Stepper, DatePickerDialog, Real-Time Calculation | [`FormPemesananActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/FormPemesananActivity.kt)<br>[`InspeksiReturnActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/InspeksiReturnActivity.kt) | Validasi formulir identitas (Nama, No. WhatsApp, SIM A/KTP), kontrol stepper hari (`-` / `+`), pemilihan kalender tanggal sewa (`DatePickerDialog`), `TextWatcher` pada input kilometer odometer, serta kalkulasi real-time biaya sewa, denda selisih BBM, dan deposit garansi. |
| **Tugas 5** | Advanced UI & Data Binding (CustomAdapter & Horizontal Filter Chips) | [`VehicleAdapter.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/adapter/VehicleAdapter.kt)<br>[`item_vehicle_card.xml`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/res/layout/item_vehicle_card.xml)<br>[`item_vehicle_operational_row.xml`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/res/layout/item_vehicle_operational_row.xml) | Adaptor kustom berbasis `BaseAdapter` dengan pemformatan mata uang Rupiah (`Locale("id", "ID")`), indikator status berbasis warna semantik, badge plat nomor timbul, serta filter kategori dinamis (*MPV Family*, *Premium SUV*, *City Car & EV*). |
| **Tugas 6** | Menu & Dialog (Context Menu, Popup Menu, MaterialAlertDialog) | [`StallBengkelActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/StallBengkelActivity.kt)<br>[`BerandaFleetActivity.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/BerandaFleetActivity.kt)<br>[`ProfileDialogHelper.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/util/ProfileDialogHelper.kt) | **Context Menu**: Tekan lama kartu tiket bengkel untuk rilis QC, panggilan supplier, atau pembatalan servis.<br>**Popup Menu**: Menu navigasi opsi cepat pada Toolbar.<br>**MaterialAlertDialog**: Dialog konfirmasi sahkan rilis armada, dialog reset data, dan dialog ubah identitas profil petugas. |
| **Tugas 7** | Local Storage Persistence via SharedPreferences | [`VehicleRentPrefs.kt`](file:///c:/Users/Mika/AndroidStudioProjects/VehicleRent/app/src/main/java/id/ac/stmi/vehiclerent/util/VehicleRentPrefs.kt) | Penyimpanan data lokal terpusat: Profil pengguna aktif, status peran (`isWorkshopAdmin`), auto-save draft formulir pelanggan, persistensi serialisasi JSON katalog 25 unit mobil (`KEY_FLEET_STATE_JSON`), antrean tiket servis, dan manajemen versi skema preferensi. |

---

## 3. Alur Kerja Operasional Aplikasi (Workflow)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       ALUR SISTEM AUTOFLEET DIRECT                          │
└─────────────────────────────────────────────────────────────────────────────┘

 [1. Beranda Fleet] 
      │ 
      ├──> Filter Kategori (Semua, MPV, SUV, EV) & Telemetri Kesiapan (44dp)
      ├──> Pilih Metode Pengemudi (Lepas Kunci / +Sopir) & Opsi Add-ons
      └──> Tekan "Pilih & Pesan" pada Unit Mobil
             │
             ▼
 [2. Form Pemesanan]
      │
      ├──> Auto-save Draft Data Pemesan (Nama, WA, No. SIM)
      ├──> Stepper Durasi Sewa & DatePickerDialog Tanggal Pengambilan
      └──> Kalkulasi Otomatis: (Hari x Tarif) + Sopir + Add-on + Deposit Garansi
             │
             ▼
 [3. Detail Reservasi]
      │
      ├──> Penerbitan Kode Booking Unik (#AF-2026-XXX)
      ├──> Aksi Cepat Implicit Intent: Kirim WhatsApp, Telepon, Google Maps Pool
      └──> Lanjut ke Serah-Terima Kendaraan
             │
             ▼
 [4. Inspeksi Pick-up (Serah-Terima)]
      │
      ├──> Verifikasi Grid Foto 4 Sisi (Depan, Kiri, Kanan, Belakang)
      ├──> Catat Odometer Awal & Konfirmasi Level BBM (100% Full Tank)
      ├──> Checklist Dokumen & Tanda Tangan Digital Dua Pihak
      └──> Unit Berubah Status: "DISEWA (ON_RENT)"
             │
             ▼
 [5. Inspeksi Return & Evaluasi Bengkel]
      │
      ├──> Verifikasi Foto Pengembalian & Catat Odometer Masuk
      ├──> Deteksi Level BBM Masuk (Kalkulasi Otomatis Denda Rp 15.000/liter)
      ├──> Deteksi Kondisi Fisik & Diagnostik Masalah Mesin
      │      ├── Jika Normal: Unit Langsung "SIAP SEWA (READY)"
      │      └── Jika Rusak: Input Kode OBD-II (J2012) & Jam Estimasi
      │             │
      │             ▼
      │      Unit Masuk ke "BENGKEL (IN_WORKSHOP)"
      │             │
      ▼             ▼
 [6. Kamus Diagnostik OBD-II]  <════>  [7. Antrean Stall Bengkel & QC]
  - Simulator Parser Kode SAE J2012      - Monitoring 6 Stall Bengkel
  - Uraian D1, D2, D3 & Tindakan        - Total Estimasi Biaya & Jam Flat-rate
  - Estimasi Biaya Suku Cadang           - Sahkan QC: Unit Kembali ke Siap Sewa
```

---

## 4. Fitur-Fitur Utama Aplikasi

### A. Telemetri Kesiapan Armada (Executive Sleek Strip)
* Baris metrik ringkas horizontal berukuran 44dp yang memvisualisasikan jumlah unit: **Siap Sewa**, **Disewa**, dan **Bengkel**.
* Dilengkapi animasi angka bergulir (*number rolling sweep*) dan dapat diklik secara interaktif untuk memfilter daftar armada secara instan.

### B. Segmented Chips Filter Kategori
* Menggantikan dropdown spinner tradisional dengan *horizontal scrollable pill chips* (*Semua Kategori*, *MPV Family*, *Premium SUV*, *City Car & EV*).
* Memungkinkan navigasi cepat dengan satu ketukan (*single-tap tactile selection*).

### C. Inspeksi Fisik Spasial Berbasis Foto 4 Sisi
* Memetakan bodi mobil secara spasial (Depan, Kiri, Kanan, Belakang) sesuai urutan pergerakan fisik staf rental saat mengelilingi kendaraan.
* Menjamin transparansi kondisi bodi sebelum dan sesudah unit disewa untuk menghindari perselisihan biaya lecet.

### D. Mesin Diagnostik OBD-II Standar SAE J2012
* Simulator parser kode gangguan diagnostik (*Diagnostic Trouble Code / DTC*) standar otomotif:
  * **Digit 1 (Sistem):** `P` (Powertrain), `C` (Chassis), `B` (Body), `U` (Network).
  * **Digit 2 (Standar):** `0` (SAE Core Standard), `1` (OEM Specific).
  * **Digit 3 (Subsistem):** Identifikasi sensor pengapian, bahan bakar, atau komunikasi CAN-Bus.
* Menampilkan estimasi jam kerja perbaikan (*flat-rate hours*) dan biaya suku cadang resmi.

### E. Manajemen Kapasitas 6-Stall Bengkel
* Matriks kapasitas visual untuk 6 area stall bengkel (*Quick*, *Sasis*, *Mesin*, *Listrik*, *EV*, *Bodi/Cat*).
* Tiket servis memuat nama teknisi, estimasi jam selesai, dan tombol otorisasi *Quality Control (QC)* untuk mengembalikan unit ke katalog publik.

---

## 5. Struktur Berkas Proyek

```
VehicleRent/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/id/ac/stmi/vehiclerent/
│           │   ├── MainActivity.kt                 # Router kompatibilitas
│           │   ├── BerandaFleetActivity.kt         # Layar Beranda & Katalog
│           │   ├── FormPemesananActivity.kt        # Layar Formulir Reservasi
│           │   ├── DetailReservasiActivity.kt      # Layar Bukti Struk & Implicit Intent
│           │   ├── InspeksiPickUpActivity.kt       # Layar Serah-Terima Kendaraan
│           │   ├── InspeksiReturnActivity.kt       # Layar Pengembalian & Kalkulasi Denda
│           │   ├── StallBengkelActivity.kt         # Layar Antrean Stall Bengkel Internal
│           │   ├── ObdDiagnosticActivity.kt        # Layar Kamus Mesin OBD-II SAE J2012
│           │   ├── adapter/
│           │   │   └── VehicleAdapter.kt           # Custom BaseAdapter Armada
│           │   ├── model/
│           │   │   ├── VehicleModel.kt             # Data Model Kendaraan
│           │   │   ├── VehicleStatus.kt            # Enum Status (READY, ON_RENT, IN_WORKSHOP)
│           │   │   ├── ReservationModel.kt         # Data Model Transaksi
│           │   │   ├── WorkshopTicketModel.kt      # Data Model Tiket Servis
│           │   │   └── DtcModel.kt                 # Data Model Kode Diagnostik SAE
│           │   └── util/
│           │       ├── VehicleRentPrefs.kt         # SharedPreferences Manager
│           │       ├── MotionUtils.kt              # Animasi Taktil & Rolling Numbers
│           │       ├── ObdParser.kt                # Parser Aturan Standar SAE J2012
│           │       ├── ProfileDialogHelper.kt      # Dialog Pengaturan Profil Petugas
│           │       └── BottomNavHelper.kt          # Pengatur Navigasi Bawah 4 Tab
│           └── res/
│               ├── drawable/                       # Vector drawable & micro-radii shapes (4dp)
│               ├── font/                           # Plus Jakarta Sans & JetBrains Mono
│               ├── layout/                         # Berkas XML seluruh antarmuka
│               └── values/                         # Palet warna, strings, dan themes.xml
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
└── README.md
```

---

## 6. Spesifikasi Lingkungan Pengembangan

* **Bahasa Pemrograman:** Kotlin Native (`JVM 17`)
* **Minimum SDK:** Android API Level 24 (Android 7.0 Nougat)
* **Target / Compile SDK:** Android API Level 34 (Android 14 UpsideDownCake)
* **Theme Foundation:** `Theme.Material3.DayNight.NoActionBar`
* **Desain Geometri:** Micro-Radii `3dp–4dp` (Chassis Geometry) dengan rasio kontras tinggi WCAG AA
* **Tipografi:** 
  * `Plus Jakarta Sans`: Judul kendaraan, label formulir, dan teks navigasi.
  * `JetBrains Mono`: Nomor plat, angka odometer, kode OBD-II, dan kalkulasi tarif nominal tabular.

---

## 7. Batasan Ruang Lingkup Pengembangan (Limitations)

Untuk menjaga fokus pada kompetensi utama pemrograman Android tingkat lanjut, batasan pengembangan sistem ini didefinisikan sebagai berikut:

1. **Penyimpanan Data Lokal (Standalone Local Persistence):**
   * Data armada, profil petugas, transaksi, dan tiket bengkel disimpan secara lokal pada perangkat menggunakan `SharedPreferences` berbasis format JSON terenkapsulasi.
   * Aplikasi belum terintegrasi dengan REST API backend cloud atau database server terpusat (MySQL/PostgreSQL), sehingga sinkronisasi antarperangkat yang berbeda memerlukan ekspor draf data.
2. **Simulasi Modul Kamera & Sensor Lapangan:**
   * Fitur inspeksi bodi 4 sisi dan barcode scanner disimulasikan menggunakan interaksi slot terverifikasi dan pratinjau aset resolusi tinggi (belum mengakses library hardware `CameraX` atau koneksi dongle scanner Bluetooth ELM327 OBD-II fisik langsung).
3. **Gateway Pembayaran:**
   * Transaksi sewa dan deposit garansi dihitung secara otomatis oleh mesin kalkulator internal aplikasi, namun belum terhubung ke payment gateway perbankan pihak ketiga (seperti Midtrans atau Xendit).
4. **Manajemen Pengguna:**
   * Pergantian antara Mode Pelanggan dan Mode Pemangku Usaha dijalankan melalui *Role Switcher Dialog* di dalam aplikasi untuk mempermudah pengujian skenario bisnis pada satu perangkat mobile.
