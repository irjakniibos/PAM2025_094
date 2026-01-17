# 🏍️ Showroom Motor

Aplikasi Android untuk pengelolaan data motor di showroom. Dibangun menggunakan **Kotlin** dengan **Jetpack Compose** sebagai framework UI modern.

---

## 📋 Deskripsi

Showroom Motor adalah aplikasi manajemen inventaris motor untuk admin showroom. Aplikasi ini memungkinkan admin untuk:
- Mengelola brand motor (tambah, edit, hapus)
- Mengelola data motor per brand (tambah, edit, hapus)
- Mengelola stok motor (tambah/kurangi stok)
- Autentikasi admin dengan email dan password

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| 🔐 **Login Admin** | Autentikasi dengan email dan password |
| 🏷️ **Manajemen Brand** | CRUD (Create, Read, Update, Delete) brand motor |
| 🏍️ **Manajemen Motor** | CRUD motor dengan detail lengkap (nama, tipe, tahun, harga, warna, stok) |
| 📦 **Manajemen Stok** | Tambah dan kurangi stok motor dengan validasi |
| 🚪 **Logout** | Keluar dari sesi admin |

---

## 🛠️ Tech Stack

| Teknologi | Keterangan |
|-----------|------------|
| **Kotlin** | Bahasa pemrograman utama |
| **Jetpack Compose** | UI Framework modern untuk Android |
| **Material Design 3** | Design system |
| **Navigation Compose** | Navigasi antar screen |
| **ViewModel** | State management dengan MVVM pattern |
| **Retrofit** | HTTP client untuk REST API |
| **OkHttp** | HTTP client |
| **Kotlin Serialization** | JSON serialization |

---

## 📁 Struktur Project

```
app/src/main/java/com/example/pengelolaandatamotorshowroom/
├── MainActivity.kt                 # Entry point aplikasi
├── ShowroomApplication.kt          # Application class
├── apiservice/                     # Service untuk API calls
├── local/                          # Local data source
├── modeldata/                      # Data models
├── repositori/                     # Repository pattern
├── ui/                             # UI theme dan components
├── uicontroller/                   # UI controllers
├── view/                           # Composable screens
└── viewmodel/                      # ViewModels untuk setiap screen
```

---

## 📱 Screenshots

### 1. Login

| Before Login | After Login |
|:------------:|:-----------:|
| ![Before Login](docs/screenshots/BeforeLogin.png) | ![After Login](docs/screenshots/AfterLogin.png) |

> Halaman login untuk admin dengan validasi email dan password

---

### 2. Manajemen Brand

#### Tambah Brand
| Before | After |
|:------:|:-----:|
| ![Before Tambah Brand](docs/screenshots/BeforeTambahBrand.png) | ![After Tambah Brand](docs/screenshots/AfterTambahBrand.png) |

> Menambahkan brand motor baru ke dalam sistem

#### Edit Brand
| Before | After |
|:------:|:-----:|
| ![Before Edit Brand](docs/screenshots/BeforeEditBrand.png) | ![After Edit Brand](docs/screenshots/AfterEditBrand.png.png) |

> Mengubah nama brand yang sudah ada

#### Hapus Brand
| Before | After |
|:------:|:-----:|
| ![Before Hapus Brand](docs/screenshots/BeforeHapusBrand.png) | ![After Hapus Brand](docs/screenshots/AfterHapusBrand.png) |

> Menghapus brand beserta semua motor yang terkait

---

### 3. Manajemen Motor

#### Edit Motor
| Before | After |
|:------:|:-----:|
| ![Before Edit Motor](docs/screenshots/BeforeEditMotor.png) | ![After Edit Motor](docs/screenshots/AfterEditMotor.png) |

> Mengubah detail motor (nama, tipe, tahun, harga, warna)

#### Hapus Motor
| Before | After |
|:------:|:-----:|
| ![Before Hapus Motor](docs/screenshots/BeforeHapusMotor.png) | ![After Hapus Motor](docs/screenshots/AfterHapusMotor.png) |

> Menghapus motor dari sistem

---

### 4. Manajemen Stok

| Before | After |
|:------:|:-----:|
| ![Before Tambah Stok](docs/screenshots/BeforeTambahStok.png) | ![After Tambah Stok](docs/screenshots/AfterTambahStok.png) |

> Menambah atau mengurangi jumlah stok motor

---

### 5. Logout

| Before | After |
|:------:|:-----:|
| ![Before Logout](docs/screenshots/BeforeLogOut.png) | ![After Logout](docs/screenshots/AfterLogOut.png) |

> Konfirmasi dan proses logout dari aplikasi

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio (versi terbaru)
- JDK 11 atau lebih tinggi
- Android SDK dengan minimum API level 27

### Langkah-langkah

1. **Clone repository**
   ```bash
   git clone https://github.com/username/showroom-motor.git
   ```

2. **Buka project di Android Studio**
   ```
   File > Open > Pilih folder project
   ```

3. **Sync Gradle**
   - Tunggu Android Studio menyelesaikan proses sync dependencies

4. **Jalankan aplikasi**
   - Pilih emulator atau device
   - Klik tombol Run ▶️

---

## 📊 Tipe Motor

Aplikasi mendukung 4 tipe motor:

| Tipe | Deskripsi |
|------|-----------|
| 🛵 **Matic** | Motor matic/automatic |
| 🏎️ **Sport** | Motor sport |
| 🏍️ **Bebek** | Motor bebek/underbone |
| 🏔️ **Trail** | Motor trail/offroad |

---

## 📝 Validasi Data

Aplikasi menerapkan validasi pada setiap input:

- **Email**: Wajib diisi
- **Password**: Wajib diisi  
- **Nama Brand**: Tidak boleh kosong dan harus unik
- **Nama Motor**: Wajib diisi
- **Tahun Motor**: Minimal tahun 2000
- **Stok Motor**: Tidak boleh negatif

---

## 👨‍💻 Developer

**Pengembangan Aplikasi Mobile - Semester 5**

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
