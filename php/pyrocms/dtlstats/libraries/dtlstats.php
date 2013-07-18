<?php if (!defined('BASEPATH')) exit('No direct script access allowed');
/**
 * dtlStats
 * dtlStats PHP Api for PyroCMS.
 *
 * @package 	addons/Libraries
 * @author      boozaa
 */
class dtlstats {

    private $plugin;


    /**
     * Constructor method
     * 
     * @return void
     */
    public function __construct()
    {
        $ci =& get_instance();
        $ci->load->library('dtlserver');
        $ci->plugin = null;
        $ci->dtlserver->init();
        $ci->dtlserver->connect();
    }


    public function plugin($plugin)
    {
        $this->plugin = $plugin;
    }
    
    public function get($stat)
    {
        $ci =& get_instance();
        $ci->dtlserver->send("GET\n");
        $plugin = $ci->dtlstats->plugin;
        $ci->dtlserver->send("$plugin:$stat:get\n");
        $buffer = $ci->dtlserver->read(255);

        return str_replace("\n", "", $buffer);
    }

    public function disconnect()
    {
        $ci =& get_instance();
        $ci->dtlserver->disconnect();
    }
    
    public function update($stat, $value)
    {
        $ci =& get_instance();
        $ci->dtlserver->send("UPDATE\n");
        $plugin = $ci->dtlstats->plugin;
        $ci->dtlserver->send("$plugin:$stat:$value\n");
    }

    public static function connect()
    {
        $ci =& get_instance();
        $ci->dtlserver = new dtlstats();
    }

    
}
/* End of file dtlstats.php */