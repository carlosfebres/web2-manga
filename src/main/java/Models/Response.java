package Models;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response<T> {
    private String message;
    private Integer status;
    private T data;
    private HttpServletResponse res;

    public Response(HttpServletResponse res) {
        this.res = res;
    }

    public Object getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void print() {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            this.res.getWriter().print(objMapper.writeValueAsString(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
