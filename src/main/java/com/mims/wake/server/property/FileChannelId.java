// [YPK]
package com.mims.wake.server.property;

import io.netty.channel.ChannelId;

public class FileChannelId implements ChannelId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String serviceId;

	public FileChannelId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * Returns the short but globally non-unique string representation of the
	 * {@link ChannelId}.
	 */
	@Override
	public String asShortText() {
		return this.serviceId;
	}

	/**
	 * Returns the long yet globally unique string representation of the
	 * {@link ChannelId}.
	 */
	@Override
	public String asLongText() {
		return this.serviceId;
	}

	@Override
	public int compareTo(ChannelId o) {
		// TODO Auto-generated method stub
		return 0;
	}
}