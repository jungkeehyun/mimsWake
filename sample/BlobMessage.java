
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.JMSException;


public abstract interface BlobMessage extends Message {
	public abstract InputStream getInputStream() throws IOException,
			JMSException;

	public abstract URL getURL() throws MalformedURLException, JMSException;

	public abstract String getMimeType();

	public abstract void setMimeType(String paramString);

	public abstract String getName();

	public abstract void setName(String paramString);
}