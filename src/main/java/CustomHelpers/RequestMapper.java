package CustomHelpers;

import Interfaces.Model;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RequestMapper {
	public static Model toModel(HttpServletRequest request, Class<? extends Model> className) {
		String data;
		Model object = null;
		try {
			data = RequestMapper.getRequest(request);
			if (!data.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				object = mapper.readValue(data, className);
			} else {
				System.out.println("Data from request was empty");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Object toObject(HttpServletRequest request, Class<? extends Object> className) {
		String data;
		Object object = null;
		try {
			data = RequestMapper.getRequest(request);
			if (!data.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				object = mapper.readValue(data, className);
			} else {
				System.out.println("Data from request was empty");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}



	public static HashMap<String, String> toMap(HttpServletRequest request) {
		HashMap<String, String> jsonMap = null;
		String data = null;
		try {
			data = RequestMapper.getRequest(request);
			if (!data.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMap = mapper.readValue(data, HashMap.class);
			} else {
				System.out.println("Data from request was empty");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonMap;
	}

	private static String getRequest(HttpServletRequest request) throws IOException {
		return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
	}
}
