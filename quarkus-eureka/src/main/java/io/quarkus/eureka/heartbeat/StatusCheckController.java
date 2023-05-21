package io.quarkus.eureka.heartbeat;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;

@WebServlet
public class StatusCheckController extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (PrintWriter writer = response.getWriter()) {

            response.addHeader(CONTENT_TYPE, "application/json");
            String status = "{}";

            writer.write(status);
            writer.flush();
        }

    }
}
