// [YPK]
package com.mims.wake.server.outbound;

import io.netty.channel.ChannelId;

public class SendChannelId implements ChannelId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String serviceId;

	public SendChannelId(String serviceId) {
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