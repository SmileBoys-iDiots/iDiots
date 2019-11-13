package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_Elixir extends ServerBasePacket {
	public static final int Elixir = 0xe9;

	public S_Elixir(int type, int stat) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(type);
		writeC(0x01);

		switch (type) {
		case Elixir:
			writeC(0x08);
			writeC(stat);
			writeH(0);
			break;

		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

}