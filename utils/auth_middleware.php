<?php
/**
 * Auth Middleware
 * Validasi JWT token untuk protected endpoints
 */

function validateAuth() {
    include_once __DIR__ . '/../utils/jwt_helper.php';
    
    // Get authorization header
    $headers = getallheaders();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    
    if (empty($authHeader)) {
        http_response_code(401);
        echo json_encode(array("success" => false, "message" => "Unauthorized - Token tidak ditemukan"));
        exit();
    }
    
    // Extract token
    $token = str_replace('Bearer ', '', $authHeader);
    
    // Validate token
    $decoded = JWTHelper::validateToken($token);
    if (!$decoded) {
        http_response_code(401);
        echo json_encode(array("success" => false, "message" => "Unauthorized - Token tidak valid"));
        exit();
    }
    
    // Check token exists in database
    include_once __DIR__ . '/../config/database.php';
    $database = new Database();
    $db = $database->getConnection();
    
    $query = "SELECT id FROM token WHERE token = :token LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":token", $token);
    $stmt->execute();
    
    if ($stmt->rowCount() === 0) {
        http_response_code(401);
        echo json_encode(array("success" => false, "message" => "Unauthorized - Token tidak valid"));
        exit();
    }
    
    return $decoded;
}
?>