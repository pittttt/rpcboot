package cn.pitt.common;

import cn.pitt.util.SerializationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC编码器
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

	@Override
	public void encode(ChannelHandlerContext ctx, Object inObj, ByteBuf out) throws Exception {
		// 序列化
		byte[] data = SerializationUtils.serialize(inObj);
		out.writeInt(data.length);
		out.writeBytes(data);
	}
}