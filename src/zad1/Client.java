/**
 *
 *  @author Adarczyn Piotr S19092
 *
 */

package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public
	class Client {

	String host,id;
	SocketChannel channel;

	private static Charset charset = StandardCharsets.UTF_8;

	int port;
	StringBuffer log;

	public Client(String host, int port, String id)  {

		this.host = host;
		this.port = port;
		this.id = id;
		this.log = new StringBuffer();

	}

	public void connect() {

		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(host,port));
			while ((!channel.finishConnect()));

		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public String send(String request) {

		ByteBuffer outBuf = ByteBuffer.allocateDirect(request.getBytes().length);
		ByteBuffer inBuf = ByteBuffer.allocateDirect(2137);
		StringBuilder response = new StringBuilder("");
		try {

			outBuf.put(charset.encode(request));
			outBuf.flip();
			channel.write(outBuf);

		} catch (IOException e) {
			e.printStackTrace();
		}
		inBuf.clear();

		try {
			int readBytes;

			while((readBytes = channel.read(inBuf)) < 1);

			for( ; readBytes > 0 ; readBytes = channel.read(inBuf) ){
				inBuf.flip();
				CharBuffer cbuf = charset.decode(inBuf);
				response.append(cbuf);
			}

		}catch (IOException e){
			e.printStackTrace();
		}

		return response.toString();

	}
}
