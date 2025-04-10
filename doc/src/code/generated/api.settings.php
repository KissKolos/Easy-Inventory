class Settings {
    public readonly DBConnection $db_connection;
    public readonly LogSettings $success_log;
    public readonly LogSettings $bad_log;
    public readonly LogSettings $rejected_log;
    public readonly LogSettings $error_log;
    public readonly string|null $test_token;
    public readonly int $token_length;
        string|null $test_token,int $token_length) { ... }
    public static function getSettings():Settings { ... }
}