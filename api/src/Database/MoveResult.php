<?php

namespace EI\Database;

enum MoveResult {
    case Moved;
    case Missing;
    case Failed;
}