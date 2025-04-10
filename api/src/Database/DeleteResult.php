<?php

namespace EI\Database;

enum DeleteResult {
    case Deleted;
    case Missing;
    case Failed;
}