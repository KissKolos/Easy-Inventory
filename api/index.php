<?php
session_start();
include './vendor/autoload.php';

use EI\Routing\ApiRouter;

ApiRouter::handle();