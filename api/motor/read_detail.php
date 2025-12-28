<?php
/**
 * API Read Detail Motor
 * Endpoint: GET /api/mobil/read_detail.php?id=1
 * Header: Authorization: Bearer {token}
 */

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../../config/database.php';
include_once '../../utils/auth_middleware.php';

// Validate authentication
validateAuth();

// Hanya terima GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// Get motor_id dari query parameter
$motor_id = isset($_GET['id']) ? $_GET['id'] : '';

// Validasi input
if (empty($motor_id)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Motor ID tidak boleh kosong"
    ));
    exit();
}

// Database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Query detail motor dengan JOIN ke brand dan stok
    $query = "SELECT 
                m.id, 
                m.nama_motor, 
                m.brand_id,
                b.nama_brand,
                m.tipe, 
                m.tahun, 
                m.harga, 
                m.warna,
                s.jumlah_stok,
                m.created_at
              FROM motor m
              INNER JOIN brand b ON m.brand_id = b.id
              LEFT JOIN stok s ON m.id = s.motor_id
              WHERE m.id = :id
              LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":id", $motor_id);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        
        http_response_code(200);
        echo json_encode(array(
            "success" => true,
            "data" => array(
                "id" => $row['id'],
                "nama_motor" => $row['nama_motor'],
                "brand_id" => $row['brand_id'],
                "nama_brand" => $row['nama_brand'],
                "tipe" => $row['tipe'],
                "tahun" => $row['tahun'],
                "harga" => $row['harga'],
                "warna" => $row['warna'],
                "jumlah_stok" => $row['jumlah_stok'] ? intval($row['jumlah_stok']) : 0,
                "created_at" => $row['created_at']
            )
        ));
    } else {
        http_response_code(404);
        echo json_encode(array(
            "success" => false,
            "message" => "Motor tidak ditemukan"
        ));
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ));
}
?>