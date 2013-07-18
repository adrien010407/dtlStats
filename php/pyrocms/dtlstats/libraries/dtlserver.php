<?php if (!defined('BASEPATH')) exit('No direct script access allowed');
/**
 * dtlServer
 * dtlStats PHP Api for PyroCMS.
 *
 * @package 	addons/Libraries
 * @author      boozaa
 */
class dtlserver {

    private $address;
    private $port;
    private $socket;



    public function __construct()
    {
        $this->address = Settings::get('dtlstats_server');
        $this->port = Settings::get('dtlstats_port');          
        $this->socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
    }

    public function connect()
    {
        socket_connect($this->socket, $this->address, $this->port);
    }

    public function send($message)
    {
        return socket_sendto($this->socket, $message, strlen($message), MSG_EOF, $this->address, $this->port);
    }

    public function read($len)
    {
        return socket_read($this->socket, $len, PHP_NORMAL_READ);//, );
    }

    public function disconnect()
    {
        socket_close($this->socket);
    }


    public static function init()
    {
        $ci =& get_instance();
        if ( $ci->dtlserver == null )
        {
            $ci->dtlserver = new dtlserver();
        }
    }

    public static function stop()
    {
        $ci =& get_instance();
        $ci->dtlserver->disconnect();
    }


}
/* End of file dtlserver.php */