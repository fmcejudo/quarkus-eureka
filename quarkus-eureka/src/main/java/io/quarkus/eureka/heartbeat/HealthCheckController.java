package io.quarkus.eureka.heartbeat;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class HealthCheckController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (PrintWriter writer = response.getWriter()) {

            String status = """
                    {
                      "status" : "UP"
                    }""";

            writer.write(status);
            writer.flush();
        }

    }
}