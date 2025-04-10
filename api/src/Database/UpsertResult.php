<?php

namespace EI\Database;

enum UpsertResult {
    case Created;
    case Updated;
    case Failed;

    public static function fromModified(int $modified):self {
        switch($modified) {
            case 0:
                return self::Failed;
            case 1:
                return self::Created;
            default:
                return self::Updated;
        }
    }
}