class Authentication {
    public static function getToken():string|null { ... }
    public static function isTestUser():bool { ... }
    public static function getCurrentUserId():string|null { ... }
    public static function isValidPassword(string $password):bool { ... }
    public static function verifyPassword(string $user_id,string $password):bool { ... }
    public static function authenticate(string $user_id,string $password):string|null { ... }
    public static function createHash(string $password): string { ... }
}