<?php

namespace EI\Database;

enum UpdateResult {
    case Updated;
    case NotFound;
    case Failed;
}