package server.WebProtocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class WebCodecFactory implements ProtocolCodecFactory {
	// private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ProtocolEncoder encoder;
	private final ProtocolDecoder decoder;

	public WebCodecFactory() {
		encoder = new WebPacketEncoder();
		decoder = new WebPacketDecoder();
	}

	public ProtocolDecoder getDecoder(IoSession client) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession client) throws Exception {
		return encoder;
	}

}
