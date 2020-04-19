/**
 *
 *  @author Adarczyn Piotr S19092
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public
	class Server {

	String host;
	ServerSocketChannel serverChannel;
	int port;
	Selector selector;
	volatile boolean isServerRunning;
	SelectionKey sscKey;
	Map<SocketChannel,Connection> connections;
	StringBuilder log;


	public Server(String host, int port) throws IOException {

		this.host = host;
		this.port = port;
		serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(host,port));
		serverChannel.configureBlocking(false);
		selector = Selector.open();
		sscKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		log = new StringBuilder();
		connections = new HashMap<>();


	}

	public void startServer(){

		new Thread(()-> {

				isServerRunning = true;

				for (; isServerRunning; ) {
					try {

						selector.select();

						Set<SelectionKey> keys = selector.selectedKeys();
						Iterator<SelectionKey> iterator = keys.iterator();

						while (iterator.hasNext()){

							SelectionKey key = iterator.next();
							iterator.remove();

							if(key.isAcceptable()){

								SocketChannel clientChannel = serverChannel.accept();

								clientChannel.configureBlocking(false);

								clientChannel.register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
								continue;
							}
							if(key.isReadable()){

								SocketChannel clientChannel = (SocketChannel) key.channel();
								serviceRequest(clientChannel);


							}

						}


					}catch (IOException e){}

				}

		}).start();

	}

	private ByteBuffer buf = ByteBuffer.allocateDirect(1024);
	private static Charset charset = StandardCharsets.UTF_8;


	private StringBuffer reqString = new StringBuffer();

	private void serviceRequest(SocketChannel clientChannel) {

		if (!clientChannel.isOpen()) return;
		reqString.setLength(0);

		buf.clear();

		try {
			for (int bytesRead = clientChannel.read(buf); bytesRead > 0; bytesRead = clientChannel.read(buf)) {
				buf.flip();

				CharBuffer cbuf = charset.decode(buf);
				reqString.append(cbuf);

			}

			handleRequest(clientChannel, reqString.toString());


		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void handleRequest(SocketChannel client,String request) throws IOException {

		StringBuilder response = new StringBuilder();
		String[] array = new String[4];
		array[3] = "";

		if(request.contains("login")){

			String str = "logged in";
			response.append(str);
			connections.put(client,new Connection(request.split(" ")[1]));
			doLogClient(client,str);
			array[1] = str;

		}else if(request.equals("bye")){

			String str = "logged out";
			response.append(str);
			connections.get(client).close();
			doLogClient(client,str);
			array[1] = str;

		}else if(request.equals("bye and log transfer")){

			String str = "logged out";
			doLogClient(client,str);

			connections.get(client).close();
			response.append(connections.get(client));
			array[1] = str;

		}else{

			doLogClient(client,"Request: " + request );
			String[] split = request.split(" ");
			String result = Time.passed(split[0],split[1]);
			doLogClient(client,"Result:");
			doLogClient(client,result);
			response.append(result);

			array[1] = "request";
			array[3] = ": \"" + request + "\"";
		}

		array[0] = connections.get(client).id;

		array[2] = "at " + LocalTime.now();

		doLog(String.format("%s %s %s%s",array[0],array[1],array[2],array[3]));

		ByteBuffer out = ByteBuffer.allocateDirect(response.toString().getBytes().length);

		out.put(charset.encode(response.toString()));
		out.flip();
		client.write(out);

	}

	private void doLog(String log){
		this.log.append(log).append("\n");

	}
	private void doLogClient(SocketChannel client,String log){

		if(!connections.containsKey(client))
			connections.put(client, new Connection(log));
		else
			connections.get(client).log.append(log).append("\n");

	}


	public void stopServer() {

		isServerRunning = false;

	}


	public String getServerLog() {

		return log.toString();
	}

	private static class Connection{

		StringBuilder log;
		String id;

		public Connection(String id){

			this.id = id;
			log = new StringBuilder("\n=== " + id + " log start ===\n");
		}
		public void close(){
			log.append("=== ").append(id).append(" log end ===\n");
		}

		@Override
		public String toString() {
			return log.toString();
		}
	}

}
