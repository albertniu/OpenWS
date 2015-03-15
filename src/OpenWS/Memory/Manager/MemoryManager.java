package OpenWS.Memory.Manager;

import java.util.Calendar;

import OpenWS.WebService.Container.OpenWS;

class MemoryCleanThread extends Thread
{
	private long m_tmLastActive = Calendar.getInstance ().getTimeInMillis ();
	private long m_nLastStatus = 0;

	public long GetLastActiveTime()
	{
		return (Calendar.getInstance ().getTimeInMillis () - m_tmLastActive);
	}

	public long GetLastStatus()
	{
		return m_nLastStatus;
	}

	public void run()
	{
		while ( true )
		{
			try
			{
				OpenWS.m_logger.debug ( "totalMemory = " + Runtime.getRuntime ().totalMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "freeMemory  = " + Runtime.getRuntime ().freeMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "maxMemory   = " + Runtime.getRuntime ().maxMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "myMemory    = " + (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) / 1048576 + "MB" );

				System.gc ();

				OpenWS.m_logger.debug ( "totalMemory = " + Runtime.getRuntime ().totalMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "freeMemory  = " + Runtime.getRuntime ().freeMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "maxMemory   = " + Runtime.getRuntime ().maxMemory () / 1048576 + "MB" );
				OpenWS.m_logger.debug ( "myMemory    = " + (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) / 1048576 + "MB" );

				m_nLastStatus = 1;
				m_tmLastActive = Calendar.getInstance ().getTimeInMillis ();

				sleep ( OpenWS.INTERVAL_GC * 1000 );
			}
			catch ( Exception ex )
			{
				OpenWS.m_logger.error ( ex.toString (), ex );

				m_nLastStatus = 0;
				m_tmLastActive = Calendar.getInstance ().getTimeInMillis ();
			}
		}
	}
}

public class MemoryManager
{
	static private MemoryCleanThread m_MemoryCleanThread;

	static public void Start()
	{
		try
		{
			m_MemoryCleanThread = new MemoryCleanThread ();
			m_MemoryCleanThread.setPriority ( Thread.MIN_PRIORITY );
			m_MemoryCleanThread.start ();
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}

	static public String GetStatusInfo()
	{
		String info = "";
		info += "AlbertWebService.TotalMemory=" + Runtime.getRuntime ().totalMemory () / 1048576 + "MB\r\n";
		info += "AlbertWebService.FreeMemory=" + Runtime.getRuntime ().freeMemory () / 1048576 + "MB\r\n";
		info += "AlbertWebService.MyMemory=" + (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) / 1048576 + "MB\r\n";
		info += "AlbertWebService.MaxMemory=" + Runtime.getRuntime ().maxMemory () / 1048576 + "MB\r\n";

		if ( m_MemoryCleanThread != null )
		{
			info += "AlbertWebService.MemoryManager.MemoryCleanThread.LastStatus=" + m_MemoryCleanThread.GetLastStatus () + "\r\n";
			info += "AlbertWebService.MemoryManager.MemoryCleanThread.LastActiveTime=" + m_MemoryCleanThread.GetLastActiveTime () + "\r\n";
		}

		return info;
	}
}
