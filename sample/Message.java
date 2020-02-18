import java.util.Enumeration;

import javax.print.attribute.standard.Destination;


public abstract interface Message {
	public static final int DEFAULT_DELIVERY_MODE = 2;
	public static final int DEFAULT_PRIORITY = 4;
	public static final long DEFAULT_TIME_TO_LIVE = 0L;

	public abstract String getMessageID() throws Exception;

	public abstract void setMessageID(String paramString)
			throws Exception;

	public abstract long getTimestamp() throws Exception;

	public abstract void setTimestamp(long paramLong) throws Exception;

	public abstract byte[] getCorrelationIDAsBytes() throws Exception;

	public abstract void setCorrelationIDAsBytes(byte[] paramArrayOfByte)
			throws Exception;

	public abstract void setCorrelationID(String paramString)
			throws Exception;

	public abstract String getCorrelationID() throws Exception;

	public abstract Destination getReplyTo() throws Exception;

	public abstract void setReplyTo(Destination paramDestination)
			throws Exception;

	public abstract Destination getDestination() throws Exception;

	public abstract void setDestination(Destination paramDestination)
			throws Exception;

	public abstract int getDeliveryMode() throws Exception;

	public abstract void setDeliveryMode(int paramInt) throws Exception;

	public abstract boolean getRedelivered() throws Exception;

	public abstract void setRedelivered(boolean paramBoolean)
			throws Exception;

	public abstract String getType() throws Exception;

	public abstract void setType(String paramString) throws Exception;

	public abstract long getExpiration() throws Exception;

	public abstract void setExpiration(long paramLong) throws Exception;

	public abstract int getPriority() throws Exception;

	public abstract void setPriority(int paramInt) throws Exception;

	public abstract void clearProperties() throws Exception;

	public abstract boolean propertyExists(String paramString)
			throws Exception;

	public abstract boolean getBooleanProperty(String paramString)
			throws Exception;

	public abstract byte getByteProperty(String paramString)
			throws Exception;

	public abstract short getShortProperty(String paramString)
			throws Exception;

	public abstract int getIntProperty(String paramString) throws Exception;

	public abstract long getLongProperty(String paramString)
			throws Exception;

	public abstract float getFloatProperty(String paramString)
			throws Exception;

	public abstract double getDoubleProperty(String paramString)
			throws Exception;

	public abstract String getStringProperty(String paramString)
			throws Exception;

	public abstract Object getObjectProperty(String paramString)
			throws Exception;

	public abstract Enumeration getPropertyNames() throws Exception;

	public abstract void setBooleanProperty(String paramString,
			boolean paramBoolean) throws Exception;

	public abstract void setByteProperty(String paramString, byte paramByte)
			throws Exception;

	public abstract void setShortProperty(String paramString, short paramShort)
			throws Exception;

	public abstract void setIntProperty(String paramString, int paramInt)
			throws Exception;

	public abstract void setLongProperty(String paramString, long paramLong)
			throws Exception;

	public abstract void setFloatProperty(String paramString, float paramFloat)
			throws Exception;

	public abstract void setDoubleProperty(String paramString,
			double paramDouble) throws Exception;

	public abstract void setStringProperty(String paramString1,
			String paramString2) throws Exception;

	public abstract void setObjectProperty(String paramString,
			Object paramObject) throws Exception;

	public abstract void acknowledge() throws Exception;

	public abstract void clearBody() throws Exception;
}
