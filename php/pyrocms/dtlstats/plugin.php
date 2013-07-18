<?php

defined('BASEPATH') OR exit('No direct script access allowed');


class Plugin_dtlstats extends Plugin {


	/**
	 * Get the IP or domain setup
	 *
	 * Usage:
	 * {{ dtlstats:server }}
	 *
	 * @return string
	 */
	function server()
	{		
		$this->load->model('settings_m');
		$setting = $this->settings_m->get_by(array('slug' => 'dtlstats_server'));
		return $setting->value;
	}

	/**
	 * Get the port setup
	 *
	 * Usage:
	 * {{ dtlstats:port }}
	 *
	 * @return string
	 */
	function port()
	{		
		$this->load->model('settings_m');
		$setting = $this->settings_m->get_by(array('slug' => 'dtlstats_port'));
		return $setting->value;
	}


	/**
	 * Try the sample listenerCount
	 *
	 * Usage:
	 * {{ dtlstats:listenerCount }}
	 *
	 * @return string
	 */
	function listenerCount()
	{		
		$this->dtlstats->plugin("dtlStats");
		$nbListener = $this->dtlstats->get("listenerCount");
		$this->dtlstats->disconnect();
		return $nbListener;
	}


	/**
	 * Try the sample broadcast message to your bukkit server
	 *
	 * Usage:
	 * {{ dtlstats:message }}
	 *
	 * @return string
	 */
	function message()
	{		
		$this->dtlstats->plugin("dtlStats");
		$this->dtlstats->update("message", "Hello from PyroCMS using the great dtlStats!");
		$this->dtlstats->disconnect();
		return "";
	}
    
}

/* End of file Plugin_dtlTests.php */
