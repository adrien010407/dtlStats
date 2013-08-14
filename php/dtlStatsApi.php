class Socket
{
    private static $instance;

    private $address;
    private $port;

    private $socket;
    private $connected = FALSE;

    private function __construct($address, $port)
    {
        $this->address = $address;
        $this->port = $port;
          
        $this->socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
    }

    private function __connect()
    {
        $this->connected = @socket_connect($this->socket, $this->address, $this->port);
    }

    private function __connected()
    {
    	return $this->connected;
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

    public static function init($address, $port)
    {
        if ( static::$instance == null )
        {
            static::$instance = new Socket($address, $port);
        }
    }

    public static function connected()
    {
    	return static::$instance->__connected();
    }

    public static function send($message)
    {
        return static::$instance->__send($message);
    }

    public static function read($len = 1024)
    {
        return static::$instance->__read($len);
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

        Socket::init($address, $port);
        Socket::connect();
    }

    public function __pass($pass) 
    {
        Socket::send("$pass\n");
    }

    public function __setPlugin($plugin)
    {
        $this->plugin = $plugin;
    }
    
    public function __getStat($stat)
    {
        Socket::send("GET\n");
        Socket::send("$this->plugin:$stat\n");

        $buffer = Socket::read(1024);

        return str_replace("\n", "", $buffer);
    }

    public function __disconnect()
    {
        Socket::stop();
    }
    
    public function __updateStat($stat, $value)
    {
        Socket::send("UPDATE\n");
        Socket::send("$this->plugin:$stat/$value\n");
    }

    public static function connect($address, $port)
    {
        static::$instance = new Stat($address, $port);
    }

    public static function connected()
    {
    	return Socket::connected();
    }

    public static function pass($pass)
    {
    	if ( !Stat::connected() ) return;
        static::$instance->__pass($pass);
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

class StatClass
{
    private static $instance;
    private static $staticMethods;
    public static $staticValues;

    //non static methods
  //  private $methods;
    public $values = array();

    final public function __construct($clazz, $args = array())
    {
        //get the plugin name from $plugin property
        $plugin = StatClass::__plugin($clazz, $args);

        //get the clazz name
        $clazzName = is_object($clazz) ? $clazz->name : $clazz;

        //get $stat property
        $stat = StatClass::__stat($clazz, $args);

        //send the basic response
        $this->__response($plugin, $stat, $clazzName);
    }

    private function __stat($clazz, $args)
    {
        //get the statistic path
        if ( empty($args) && $clazz->hasProperty("stat") )
        {
            //get the property
            $statProperty = $clazz->getProperty("stat");
            $statProperty->setAccessible(true);

            //get the property value
            $stat = $statProperty->getValue($this);

            //replace clazz value
            $stat = $this->replaceStat($stat, $clazz->name);
        } 
        else 
        {
            $stat = !isset($args["stat"]) ? $clazz->name : $args["stat"];
        }
        return $stat;
    }

    private static function __plugin($clazz, $args)
    {
        //get the plugin name from $plugin property
        if ( empty($args) && $clazz->hasProperty("plugin") )
        {
            //get the property
            $pluginProperty = $clazz->getProperty("plugin");
            $pluginProperty->setAccessible(true);

            //get the property value
            $plugin = $pluginProperty->getValue($this);
        } 
        else 
        {
            $plugin = !isset($args["plugin"]) ? "dtlStats" : $args["plugin"];
        }
        return $plugin;
    }

    private function __response($plugin, $stat, $clazzName)
    {
        //set the plugin for the response
        Stat::plugin($plugin);

        //send a response request
        $response = json_decode(Stat::get($stat), true);

        //add methods
        if ( is_array($response) )
        foreach ( $response as $name => $value )
        {
            //static
            if ( !isset(StatClass::$staticValues[$clazzName][$name]) )
            {
                StatClass::$staticValues[$clazzName][$name] = $this->arrayToObject($value);
                StatClass::$staticMethods[$clazzName][$name] = function($clazz, $fnc) { return StatClass::$staticValues[$clazz][$fnc]; };
            }

            //non-static
            $this->values[$name] = $value;
        }

        //Debug info
    //    echo "<br/><br/>[{ $clazzName | $stat }]<br/><br/>";
    }

    private function __responseMethod($plugin, $stat, $methodName, $clazzName)
    {
        //set the plugin for the response
        Stat::plugin($plugin);

        //send a response request
        $response = json_decode(Stat::get($stat), true);

        //add methods
        if ( !is_array($response) ) return;
        foreach ( $response as $name => $value )
        {
            //static
            if ( !isset(StatClass::$staticValues[$clazzName][$name]) )
            {
                StatClass::$staticValues[$clazzName][$name] = $this->arrayToObject($value);
                StatClass::$staticMethods[$clazzName][$name] = function($clazz, $fnc) { return StatClass::$staticValues[$clazz][$fnc]; };
            }

            //non-static
            $this->values[$name] = $value;
        }
            
        //non-static
        $this->values[$methodName] = $this->arrayToObject($response);

        //static
        if ( !isset(StatClass::$staticValues[$clazzName][$methodName]) )
        {
            StatClass::$staticValues[$clazzName][$methodName] =  $this->values[$methodName];
            StatClass::$staticMethods[$clazzName][$methodName] = function($clazz, $fnc) { return StatClass::$staticValues[$clazz][$fnc]; };
        }
            

        //Debug info
    //    echo "<br/><br/>[{ $clazzName | $stat }]<br/><br/>";
    }

    private static function arrayToObject($d) 
    {
        if (is_array($d)) 
        {
            /*
            * Return array converted to object
            * Using __FUNCTION__ (Magic constant)
            * for recursive call
            */
            return (object) array_map("StatClass::arrayToObject", $d);
        }
        else 
        {
            // Return object
            return $d;
        }
    }

    public static function __callStatic($name, $args)
    {
        $clazzName = get_called_class();
        if ( empty(StatClass::$staticMethods[$clazzName]) )
            self::load();

        if( is_callable(StatClass::$staticMethods[$clazzName][$name]) )
        {
            array_unshift($args, $clazzName, $name);
            return call_user_func_array(StatClass::$staticMethods[$clazzName][$name], $args);
        }
        else
        {
            //new clazz reflection
            $clazz = new ReflectionClass($clazzName);

            //get plugin
            $plugin = StatClass::__plugin($clazz, array());

            //get stat 
            $stat = StatClass::$instance[$clazzName]->__stat($clazz, array())."/$name";

            //send a new reponse
            StatClass::$instance[$clazzName]->__responseMethod($plugin, $stat, $name, $clazzName);

            //retry to call the method
            if( is_callable(StatClass::$staticMethods[$clazzName][$name]) )
            {
                array_unshift($args, $clazzName, $name);
                return call_user_func_array(StatClass::$staticMethods[$clazzName][$name], $args);
            }
            else
                return null;//"Fatal error: Static Method $name is undefined!";
        }
    }

    public function __get($name) 
    {
        if( array_key_exists($name, $this->values) )
        {
            return $this->values[$name];
        }
        else
        {
            //new clazz reflection
            $clazz = new ReflectionClass($this);

            //get plugin
            $plugin = StatClass::__plugin($clazz, array());

            //get stat 
            $stat = $this->__stat($clazz, array())."/$name";

            //send a new reponse
            $this->__responseMethod($plugin, $stat, $name, $clazz->name);

            //retry to call the method
            if( array_key_exists($name, $this->values) )
            {
                return $this->values[$name];
            }
            else
                return null;//"Fatal error: Static Method $name is undefined!";
        }
    }

    protected function replaceStat($stat, $clazz)
    {
        return $stat;
    }

    public static function load()
    {
        //get the class name
        $clazzName = get_called_class();

        //get the reflection class and apply all static methods
        $clazz = new ReflectionClass($clazzName);

        //create the default instance
        StatClass::$instance[$clazzName] = $clazz->newInstance($clazz);

        //return the instance
        return StatClass::$instance[$clazzName];
    }

}

class Players extends StatClass { }

class Player extends StatClass
{
    protected $stat = "players/player/{player}";
    private $player = null;

    public function replaceStat($stat, $clazz)
    {
        return str_replace("{player}", $this->player == null ? $clazz : $this->player, $stat);
    }

    public static function get($playerName)
    {
        $player = new Player("Player", array("stat"=>"players/player/$player"));

        //set the player
        $player->player = $playerName;

        //return a new Player instance
        return $player;
    }
}
 
class Bukkit extends StatClass
{
    protected $stat = "bukkit/info";

    //define the command method
    public static function command($command, $condition = null, $arg = null)
    {
        if ( $condition == null )
            Stat::update("bukkit/command", "$command");
        else
            Stat::update("bukkit/command/$command/$condition", "$arg");
    }

    //define the broadcast method
    public static function broadcast($message)
    {
        Stat::update("bukkit/broadcast", $message);
    }
}

class Worlds extends StatClass { }

class World extends StatClass
{
    protected $stat = "worlds/world/{world}";
    protected $world = null;

    public function replaceStat($stat, $clazz)
    {
        return str_replace("{world}", $this->world == null ? $clazz : $this->world, $stat);
    }
 
    public static function get($world)
    {
        $nw = new World("World", array( "stat"=>"worlds/world/$world" )); 

        $nw->world = $world;

        return $nw;
    }
}
