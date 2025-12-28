<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Hanya terima POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// DIPERBAIKI: Path ke config/database.php
require_once '../../config/database.php';

// Get posted data
$input = file_get_contents("php://input");
$data = json_decode($input);

// Validasi JSON
if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Invalid JSON: " . json_last_error_msg()
    ));
    exit();
}

// Validasi input
if (empty($data->nama_brand)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Nama brand tidak boleh kosong"
    ));
    exit();
}

// Database connection
try {
    $database = new Database();
    $db = $database->getConnection();
    
    if (!$db) {
        throw new Exception("Koneksi database gagal");
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection error: " . $e->getMessage()
    ));
    exit();
}

try {
    // Cek duplikasi (case-insensitive)
    $query = "SELECT id FROM brand WHERE LOWER(nama_brand) = LOWER(:nama_brand) LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":nama_brand", $data->nama_brand);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        http_response_code(400);
        echo json_encode(array(
            "success" => false,
            "message" => "Brand sudah ada!"
        ));
        exit();
    }

    // Insert brand baru
    $query = "INSERT INTO brand (nama_brand) VALUES (:nama_brand)";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":nama_brand", $data->nama_brand);

    if ($stmt->execute()) {
        $brand_id = $db->lastInsertId();
        
        // Ambil data lengkap termasuk created_at dari database
        $query = "SELECT id, nama_brand, created_at FROM brand WHERE id = :id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":id", $brand_id);
        $stmt->execute();
        
        $brand_data = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($brand_data) {
            http_response_code(201);
            echo json_encode(array(
                "success" => true,
                "message" => "Brand berhasil ditambahkan",
                "data" => array(
                    "id" => (int)$brand_data['id'],
                    "nama_brand" => $brand_data['nama_brand'],
                    "created_at" => $brand_data['created_at']
                )
            ));
        } else {
            http_response_code(500);
            echo json_encode(array(
                "success" => false,
                "message" => "Brand tersimpan tapi gagal mengambil data"
            ));
        }
    } else {
        http_response_code(500);
        echo json_encode(array(
            "success" => false,
            "message" => "Gagal menambahkan brand"
        ));
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ));
}