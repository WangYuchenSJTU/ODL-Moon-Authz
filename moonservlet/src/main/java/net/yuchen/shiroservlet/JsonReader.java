package net.yuchen.shiroservlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
public class JsonReader {
	public static JSONObject receivePost(HttpServletRequest request) throws IOException, UnsupportedEncodingException {

		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		JSONObject json=new JSONObject(sb.toString());
		return json;
	}
}
