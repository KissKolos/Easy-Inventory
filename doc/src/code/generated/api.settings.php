class Settings {
    public readonly DBConnection $db_connection;
    public readonly LogSettings $success_log;
    public readonly LogSettings $bad_log;
    public readonly LogSettings $rejected_log;
    public readonly LogSettings $error_log;
    public readonly string|null $test_token;
    public readonly int $token_length;
    public readonly int $token_expiration;
        string|null $test_token,int $token_length,int $token_expiration) { ... }
    public static function getSettings():Settings { ... }
}