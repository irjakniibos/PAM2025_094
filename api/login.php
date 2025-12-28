<?php
/**
 * API Login
 * Endpoint: POST /api/login.php
 * Body: { "email": "...", "password": "..." }
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

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Validasi input
if (empty($data->email) || empty($data->password)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Email dan password tidak boleh kosong"
    ));
    exit();
}

// Database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Query user berdasarkan email
    $query = "SELECT id, nama_lengkap, email, password FROM user WHERE email = :email LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Verify password
        if (password_verify($data->password, $row['password'])) {
            // Generate JWT token
            $token = JWTHelper::generateToken($row['id'], $row['email']);
            
            // Simpan token ke database
            $query = "INSERT INTO token (user_id, token) VALUES (:user_id, :token)";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":user_id", $row['id']);
            $stmt->bindParam(":token", $token);
            $stmt->execute();

            // Response success
            http_response_code(200);
            echo json_encode(array(
                "success" => true,
                "message" => "Login berhasil",
                "token" => $token,
                "user" => array(
                    "id" => $row['id'],
                    "nama_lengkap" => $row['nama_lengkap'],
                    "email" => $row['email']
                )
            ));
        } else {
            // Password salah
            http_response_code(401);
            echo json_encode(array(
                "success" => false,
                "message" => "Email atau password salah"
            ));
        }
    } else {
        // User tidak ditemukan
        http_response_code(401);
        echo json_encode(array(
            "success" => false,
            "message" => "Email atau password salah"
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