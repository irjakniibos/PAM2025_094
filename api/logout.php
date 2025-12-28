<?php
/**
 * API Logout
 * Endpoint: POST /api/logout.php
 * Header: Authorization: Bearer {token}
 */

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../utils/jwt_helper.php';

// Hanya terima POST request
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// Get authorization header
$headers = getallheaders();
$authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

if (empty($authHeader)) {
    http_response_code(401);
    echo json_encode(array("success" => false, "message" => "Token tidak ditemukan"));
    exit();
}

// Extract token dari "Bearer {token}"
$token = str_replace('Bearer ', '', $authHeader);

// Validate token
$decoded = JWTHelper::validateToken($token);
if (!$decoded) {
    http_response_code(401);
    echo json_encode(array("success" => false, "message" => "Token tidak valid"));
    exit();
}

// Database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Hapus token dari database
    $query = "DELETE FROM token WHERE token = :token";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":token", $token);
    $stmt->execute();

    // Response success
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "message" => "Logout berhasil"
    ));
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ));
}
?>