package server.WebProtocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class WebPacketEncoder implements ProtocolEncoder {

	public synchronized void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		try {
		} catch (Exception e) {
		}
	}

	public void dispose(IoSession client) throws Exception {

	}
}
