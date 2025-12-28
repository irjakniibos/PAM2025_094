<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// DIPERBAIKI: Path ke config/database.php
require_once '../../config/database.php';

// Hanya terima GET
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Method not allowed"));
    exit();
}

// Database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Query semua brand, urutkan berdasarkan nama
    $query = "SELECT id, nama_brand, created_at FROM brand ORDER BY nama_brand ASC";
    $stmt = $db->prepare($query);
    $stmt->execute();

    $brands = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $brands[] = array(
            "id" => (int)$row['id'],
            "nama_brand" => $row['nama_brand'],
            "created_at" => $row['created_at']
        );
    }

    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "data" => $brands
    ));
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ));
}