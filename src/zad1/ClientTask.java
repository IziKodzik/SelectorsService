/**
 *
 *  @author Adarczyn Piotr S19092
 *
 */

package zad1;


import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ClientTask
	extends FutureTask<String> {




	public ClientTask(Callable<String> callable) {
		super(callable);
	}

	public static ClientTask create(Client c, List<String> reqList, boolean showRes) {

		return new ClientTask(()->{

			c.connect();
			c.send("login " + c.id);
			reqList.forEach((string)->{

				String response = c.send(string);

				if(showRes)
					System.out.println(response);


			});

			c.log.append(c.send("bye and log transfer"));

			return c.log.toString();

		});

	}

}
