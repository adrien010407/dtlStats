<?php

class Serv
{
    private static $instance;

    private $address;
    private $port;

    private $socket;

    private function __construct($address, $port)
    {
        $this->address = $address;
        $this->port = $port;
          
        $this->socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
    }

    private function __connect()
    {
        socket_connect($this->socket, $this->address, $this->port);
    }

    private function __send($message)
    {
        return socket_sendto($this->socket, $message, strlen($message), MSG_EOF, $this->address, $this->port);
    }

    private function __read($len)
    {
        return socket_read($this->socket, $len, PHP_NORMAL_READ);//, );
    }

    private function __disconnect()
    {
        socket_close($this->socket);
    }

    public static function send($message)
    {
        return static::$instance->__send($message);
    }

    public static function read($len = 1024)
    {
        return static::$instance->__read($len);
    }

    public static function init($address, $port)
    {
        if ( static::$instance == null )
        {
            static::$instance = new Serv($address, $port);
        }
    }

    public static function connect()
    {
        static::$instance->__connect();
    }

    public static function disconnect()
    {
        static::$instance->__disconnect();
    }

    public static function stop()
    {
        static::$instance->__disconnect();
        static::$instance = null;
    }
}

class Stat
{
    private static $instance;
    private $plugin;

    private function __construct($address, $port)
    {
        $this->plugin = null;

        Serv::init($address, $port);
        Serv::connect();
    }

    public function __setPlugin($plugin)
    {
        $this->plugin = $plugin;
    }
    
    public function __getStat($stat)
    {
        Serv::send("GET\n");
        Serv::send("$this->plugin:$stat:get\n");

        $buffer = Serv::read(255);

        return str_replace("\n", "", $buffer);
    }

    public function __disconnect()
    {
        Serv::stop();
    }
    
    public function __updateStat($stat, $value)
    {
        Serv::send("UPDATE\n");
        Serv::send("$this->plugin:$stat:$value\n");
    }

    public static function connect($address, $port)
    {
        static::$instance = new Stat($address, $port);
    }

    public static function plugin($plugin)
    {
        static::$instance->__setPlugin($plugin);
    }

    public static function get($stat)
    {
        return static::$instance->__getStat($stat);
    }

    public static function disconnect()
    {
        static::$instance->__disconnect();
    }

    public static function update($stat, $value)
    {
        return static::$instance->__updateStat($stat, $value);
    }
}
