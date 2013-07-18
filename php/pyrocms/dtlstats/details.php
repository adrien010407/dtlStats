<?php defined('BASEPATH') or exit('No direct script access allowed');
/**
 * PyroCMS Module dtlStats
 *
 * @author 		boozaa - boozaa@shortrip.org
 * @package 	PyroCMS
 * @subpackage 	dtlStats Module
 */
class Module_dtlstats extends Module {

	public $version = '0.1';
	public $name = 'dtlStats';
        

	
	public function info()
	{
		return array(
			'name' => array(
				'fr' => 'dtlStats',
				'en' => 'dtlStats'
			),
			'description' => array(
				'fr' => 'Paramétrage pour dtlStats.',
				'en' => 'Setup for dtlStats.'
			),
			'frontend' => FALSE,
			'backend' => TRUE
		);
	}

	
    private $custom_settings = array(
		'dtlstats_server' => array(
			'slug' => 'dtlstats_server',
			'title' => 'Server',
			'description' => 'Server IP or domain',
			'type' => 'text',
			'default' => 'locahost',
			'value' => 'locahost',
			'options' => '',
			'is_required' => true,
			'is_gui' => true,
			'module' => 'dtlstats',
			'order' => 10),
		'dtlstats_port' => array(
			'slug' => 'dtlstats_port',
			'title' => 'Port',
			'description' => 'Same port as your plugins/dtlstats/config.yml',
			'type' => 'text',
			'default' => '4447',
			'value' => '4447',
			'options' => '',
			'is_required' => true,
			'is_gui' => true,
			'module' => 'dtlstats',
			'order' => 9)
	);
	
	
	
	

	public function install()
	{
            
        // Nettoyage
        $this->uninstall();
        
        // Stockage de $custom_settings
        $this->load->library('settings');
	    foreach($this->custom_settings as $setting)
		    $this->settings->add($setting);

	    return TRUE;
            
	}

		

	public function uninstall()
	{
				
		// Suppression des options admin éventuelles
		$this->db->delete('settings', array('module' => $this->name));		
		return true;
		
	}


	public function upgrade($old_version)
	{
		// Your Upgrade Logic
		return TRUE;
	}


	public function help()
	{
		return "";
	}
     
        
}
/* End of file details.php */
