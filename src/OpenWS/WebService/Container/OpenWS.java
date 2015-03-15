package OpenWS.WebService.Container;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import Albert.CfgFileReader.CfgFileReader;
import DemoWebService.DemoWebService;
import OpenWS.Inbound.Processor.InboundProcessor;
import OpenWS.Memory.Manager.MemoryManager;

public class OpenWS
{
	// [General]
	static public Logger m_logger;
	static public int HTTP_PORT_WEBSERVICE;
	static public int INTERVAL_GC;
	static public int MEMORY_MANAGER;
	static public int INBOUND_PROCESSOR;

	// [WebService]
	static public String SERVICE_SELECT = "";
	static public String SERVICE_INSERT = "";
	static public String SERVICE_UPDATE = "";
	static public String SERVICE_WSDL = "";
	static public String SERVICE_NAME = "";
	static public String USER_NAME = "";
	static public String USER_PASSWORD = "";

	static public DemoWebService m_demoWebService = new DemoWebService ();
	static public String VERSION = "2014-06-20 10:30 Build(V0.0.0.1), Albert Newton";

	static public void LoadConfig()
	{
		CfgFileReader cfg = null;

		try
		{
			cfg = new CfgFileReader ( "openws.ini" );

			// [General]
			HTTP_PORT_WEBSERVICE = cfg.getInteger ( "HTTP_PORT_WEBSERVICE", 8080 );
			INTERVAL_GC = cfg.getInteger ( "INTERVAL_GC", 60 );
			MEMORY_MANAGER = cfg.getInteger ( "MEMORY_MANAGER", 1 );
			INBOUND_PROCESSOR = cfg.getInteger ( "INBOUND_PROCESSOR", 1 );

			// [WebService]
			SERVICE_NAME = cfg.getString ( "SERVICE_NAME", "" );
			USER_NAME = cfg.getString ( "USER_NAME", "" );
			USER_PASSWORD = cfg.getString ( "USER_PASSWORD", "" );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace ();
		}
		finally
		{
			if ( cfg != null )
			{
				cfg.close ();
			}
		}
	}

	static private void LoadWSDL()
	{
		try
		{
			FileInputStream fis = null;
			InputStreamReader is = null;
			BufferedReader reader = null;
			String line = null;

			try
			{
				fis = new FileInputStream ( "wsdl.xml" );

				if ( fis != null )
				{
					is = new InputStreamReader ( fis );
					reader = new BufferedReader ( is );

					while ( (line = reader.readLine ()) != null )
					{
						line = line.trim ();
						SERVICE_WSDL += line;
					}
				}
			}
			catch ( Exception ex )
			{
				OpenWS.m_logger.error ( ex.toString (), ex );
			}
			finally
			{
				line = null;

				try
				{
					if ( fis != null )
					{
						fis.close ();
						fis = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}

				try
				{
					if ( is != null )
					{
						is.close ();
						is = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}

				try
				{
					if ( reader != null )
					{
						reader.close ();
						reader = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}

	static private void LoadDemoOperation()
	{
		try
		{
			FileInputStream fis = null;
			InputStreamReader is = null;
			BufferedReader reader = null;
			String line = null;

			try
			{
				fis = new FileInputStream ( "insert.xml" );

				if ( fis != null )
				{
					is = new InputStreamReader ( fis );
					reader = new BufferedReader ( is );

					while ( (line = reader.readLine ()) != null )
					{
						line = line.trim ();
						SERVICE_INSERT += line;
					}
				}
			}
			catch ( Exception ex )
			{
				OpenWS.m_logger.error ( ex.toString (), ex );
			}
			finally
			{
				line = null;

				try
				{
					if ( fis != null )
					{
						fis.close ();
						fis = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}

				try
				{
					if ( is != null )
					{
						is.close ();
						is = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}

				try
				{
					if ( reader != null )
					{
						reader.close ();
						reader = null;
					}
				}
				catch ( IOException ex )
				{
					OpenWS.m_logger.error ( ex.toString (), ex );
				}
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}

	static private void InitLog()
	{
		try
		{
			m_logger = Logger.getLogger ( OpenWS.class.getName () );
			PropertyConfigurator.configure ( "log4j.properties" );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace ();
		}
	}

	public static void main(String[] args)
	{
		try
		{
			InitLog ();
			LoadConfig ();
			LoadWSDL ();
			LoadDemoOperation ();
			
			m_logger.debug ( "OpenWS Startup [....]" );
			m_logger.debug ( "Version=" + VERSION );

			if ( MEMORY_MANAGER == 1 )
			{
				m_logger.debug ( "MemoryManager Startup [....]" );
				MemoryManager.Start ();
				m_logger.debug ( "MemoryManager Startup [ OK ]" );
			}
			if ( INBOUND_PROCESSOR == 1 )
			{
				m_logger.debug ( "InboundProcessor Startup [....]" );
				InboundProcessor.Start ();
				m_logger.debug ( "InboundProcessor Startup [ OK ]" );
			}

			m_logger.debug ( "OpenWS Startup [ OK ]" );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace ();
		}
	}
}
