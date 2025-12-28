<?php
/**
 * API Delete Motor
 * Endpoint: DELETE /api/mobil/delete.php?id=1
 * Header: Authorization: Bearer {token}
 */

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: DELETE");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../../config/database.php';
include_once '../../utils/auth_middleware.php';

// Validate authentication
validateAuth();

// Hanya terima DELETE request
if ($_SERVER['REQUEST_METHOD'] !== 'DELETE') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// Get ID dari query parameter
$motor_id = isset($_GET['id']) ? $_GET['id'] : '';

// Validasi input
if (empty($motor_id)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "ID motor tidak boleh kosong"
    ));
    exit();
}

// Database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Hapus motor (CASCADE akan otomatis hapus stok terkait)
    $query = "DELETE FROM motor WHERE id = :id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":id", $motor_id);
    
    if ($stmt->execute()) {
        if ($stmt->rowCount() > 0) {
            http_response_code(200);
            echo json_encode(array(
                "success" => true,
                "message" => "Motor berhasil dihapus"
            ));
        } else {
            http_response_code(404);
            echo json_encode(array(
                "success" => false,
                "message" => "Motor tidak ditemukan"
            ));
        }
    } else {
        http_response_code(500);
        echo json_encode(array(
            "success" => false,
            "message" => "Gagal menghapus motor"
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