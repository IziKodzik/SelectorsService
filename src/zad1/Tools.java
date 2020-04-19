/**
 *
 *  @author Adarczyn Piotr S19092
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

public class Tools {
	public static Options createOptionsFromYaml(String fileName) {

		Yaml data = new Yaml();

		try{

			FileReader input = new FileReader(fileName);

			Map<String,Object> values = data.load(input);
			String host = ((String)values.get("host"));
			int port = ((int) values.get("port"));
			boolean cMode = ((boolean) values.get("concurMode"));
			boolean showRes = ((boolean)values.get("showSendRes"));
			Map<String, List<String>> clMap = ((Map<String,List<String>>) values.get("clientsMap"));

			return new Options(host,port,cMode,showRes,clMap);

		}catch (IOException e){
			e.printStackTrace();
		}

		return null;

	}
}
