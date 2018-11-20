package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class RequestMapper {
    public HashMap<String, Object> payloadToMap(HttpServletRequest request) throws IOException {
        // Read the request payload into a String
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        System.out.println(data);

        // If the String is not empty, parses the payload into a map
        HashMap<String, Object> jsonMap = null;
        if (!data.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            jsonMap = mapper.readValue(data, HashMap.class);
            System.out.println(jsonMap.toString());
        } else {
            System.out.println("Data from request was empty");
        }
        return jsonMap;
    }
}
