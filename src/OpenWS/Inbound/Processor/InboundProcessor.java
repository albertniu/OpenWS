package OpenWS.Inbound.Processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.concurrent.Executors;

import OpenWS.WebService.Container.OpenWS;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class httpServerDaemon
{
	void startHttpServer()
	{
		try
		{
			InetSocketAddress addr = new InetSocketAddress ( OpenWS.HTTP_PORT_WEBSERVICE );
			HttpServer server = HttpServer.create ( addr, 0 );

			server.createContext ( OpenWS.SERVICE_NAME, new httpHandler () );
			server.setExecutor ( Executors.newCachedThreadPool () );
			server.start ();

			OpenWS.m_logger.debug ( "AlbertWebService.InboundProcessor is listening on " + OpenWS.HTTP_PORT_WEBSERVICE );
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}
}

class httpHandler implements HttpHandler
{
	public void handle(HttpExchange exchange)
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		OutputStream responseBody = null;
		String line = "";
		String request = "";
		String response = "";
		String sql = "";
		String starttag = "";
		String endtag = "";
		
		try
		{
			if ( exchange.getRequestMethod ().equalsIgnoreCase ( "POST" ) )
			{
				isr = new InputStreamReader ( exchange.getRequestBody (), "UTF-8" );
				br = new BufferedReader ( isr );

				while ( (line = br.readLine ()) != null )
				{
					request += line;
				}

				if ( request.contains ( "</arg0>" ) )
				{
					starttag = "<arg0 xmlns=\"\">";
					endtag = "</arg0>";
				}
				else
				{
					starttag = "<sql>";
					endtag = "</sql>";
				}
				
				int start = request.indexOf ( starttag ) + starttag.length ();
				int end = request.indexOf ( endtag );
				if ( start < end )
				{
					sql = request.substring ( start, end );
				}
				
				if ( request.contains ( ":Body><insert xmlns=" ) )
				{
					response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns:insertResponse xmlns:ns=\"http://WebService\"><ns:return>";
					String temp = OpenWS.m_demoWebService.insert ( sql ).replace ( "<", "&lt;" );
					temp = temp.replace ( ">", "&gt;" );
					response += temp;
					response += "</ns:return></ns:insertResponse></soapenv:Body></soapenv:Envelope>";
				}

				OpenWS.m_logger.debug ( "request  = " + request );
				OpenWS.m_logger.debug ( "response = " + response );

				byte body[] = response.getBytes ( "UTF-8" );
				Headers responseHeaders = exchange.getResponseHeaders ();
				responseHeaders.set ( "Content-Type", "text/xml" );
				exchange.sendResponseHeaders ( 200, body.length );

				responseBody = exchange.getResponseBody ();
				responseBody.write ( body );
				responseBody.flush ();
			}
			else
			{
				String uri = exchange.getRequestURI ().toString ();
				response = OpenWS.SERVICE_WSDL;
				
				OpenWS.m_logger.debug ( "request  = " + uri );
				OpenWS.m_logger.debug ( "response  = " + response );

				byte body[] = response.getBytes ( "UTF-8" );
				Headers responseHeaders = exchange.getResponseHeaders ();
				responseHeaders.set ( "Content-Type", "text/xml" );
				exchange.sendResponseHeaders ( 200, body.length );

				responseBody = exchange.getResponseBody ();
				responseBody.write ( body );
				responseBody.flush ();
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
		finally
		{
			closeHandles ( br, isr, responseBody, exchange );
			line = null;
			request = null;
			response = null;
			sql = null;
		}
	}

	public String getParam(String params, String key)
	{
		try
		{
			String tokens[] = params.split ( "&" );

			for ( int i = 0; i < tokens.length; ++i )
			{
				String pair[] = tokens[i].split ( "=" );

				if ( pair.length == 2 && pair[0].equals ( key ) )
					return pair[1];
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}

		return "";
	}

	private void closeHandles(BufferedReader br, InputStreamReader isr, OutputStream out, HttpExchange exchange)
	{
		try
		{

			if ( br != null )
			{
				br.close ();
				br = null;
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}

		try
		{
			if ( isr != null )
			{
				isr.close ();
				isr = null;
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}

		try
		{
			if ( out != null )
			{
				out.close ();
				out = null;
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}

		try
		{
			if ( exchange != null )
			{
				exchange.close ();
				exchange = null;
			}
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}
}

public class InboundProcessor
{
	static public long m_tmLastActive = Calendar.getInstance ().getTimeInMillis ();
	static public long m_nLastStatus = 0;

	static public String GetStatusInfo()
	{
		String info = "";
		info += "AlbertWebService.InboundProcessor.LastStatus=" + m_nLastStatus + "\r\n";
		info += "AlbertWebService.InboundProcessor.LastActiveTime=" + (Calendar.getInstance ().getTimeInMillis () - m_tmLastActive) + "\r\n";
		return info;
	}

	static public void Start()
	{
		try
		{
			httpServerDaemon httpserver = new httpServerDaemon ();
			httpserver.startHttpServer ();
		}
		catch ( Exception ex )
		{
			OpenWS.m_logger.error ( ex.toString (), ex );
		}
	}
}
