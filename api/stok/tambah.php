<?php
/**
 * API Tambah Stok
 * Endpoint: PUT /api/stok/tambah.php
 * Header: Authorization: Bearer {token}
 * Body: { "motor_id": 1 }
 */

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../../config/database.php';
include_once '../../utils/auth_middleware.php';

// Validate authentication
validateAuth();

// Hanya terima PUT request
if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Validasi input
if (empty($data->motor_id)) {
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
    // Update stok: tambah 1
    $query = "UPDATE stok SET jumlah_stok = jumlah_stok + 1, updated_at = NOW() WHERE motor_id = :motor_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":motor_id", $data->motor_id);
    
    if ($stmt->execute()) {
        if ($stmt->rowCount() > 0) {
            // Get stok terbaru
            $query = "SELECT jumlah_stok FROM stok WHERE motor_id = :motor_id LIMIT 1";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":motor_id", $data->motor_id);
            $stmt->execute();
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            
            http_response_code(200);
            echo json_encode(array(
                "success" => true,
                "message" => "Stok berhasil ditambah",
                "data" => array(
                    "motor_id" => $data->motor_id,
                    "jumlah_stok" => intval($row['jumlah_stok'])
                )
            ));
        } else {
            http_response_code(404);
            echo json_encode(array(
                "success" => false,
                "message" => "Stok tidak ditemukan"
            ));
        }
    } else {
        http_response_code(500);
        echo json_encode(array(
            "success" => false,
            "message" => "Gagal menambah stok"
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