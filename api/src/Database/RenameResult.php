<?php

namespace EI\Database;

enum RenameResult {
    case Renamed;
    case NotFound;
    case Failed;
}