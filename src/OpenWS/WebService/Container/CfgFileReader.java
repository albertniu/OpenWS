package OpenWS.WebService.Container;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CfgFileReader
{
	Properties pts = null;
	FileInputStream fs = null;
	InputStreamReader is = null;
	Logger log = Logger.getLogger ( CfgFileReader.class );

	public CfgFileReader(String filename)
	{
		try
		{
			fs = new FileInputStream ( filename );
			is = new InputStreamReader ( fs, "utf-8" );
			pts = new Properties ();
			pts.load ( is );
		}
		catch ( Exception ex )
		{
			fs = null;
			pts = null;
			log.error ( ex.toString (), ex );
		}
	}

	public String getString(String key, String def)
	{
		if ( pts == null )
			return def;

		return pts.getProperty ( key, def );
	}

	public int getInteger(String key, int def)
	{
		if ( pts == null )
			return def;

		String tmp = pts.getProperty ( key );
		if ( tmp == null )
			return def;

		return Integer.parseInt ( tmp );
	}

	public void close()
	{
		try
		{
			if ( is != null )
				is.close ();
		}
		catch ( Exception ex )
		{
			log.error ( ex.toString (), ex );
		}

		try
		{
			if ( fs != null )
				fs.close ();
		}
		catch ( Exception ex )
		{
			log.error ( ex.toString (), ex );
		}
	}
}
