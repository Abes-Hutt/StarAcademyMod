package abeshutt.staracademy.data.serializable;

import io.netty.buffer.ByteBuf;

public interface IByteSerializable {

	void writeBytes(ByteBuf buffer);

	void readBytes(ByteBuf buffer);

}
