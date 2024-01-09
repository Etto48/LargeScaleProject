package it.unipi.gamecritic.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private static Map<Integer,String> errors = Map.ofEntries(
        Map.entry(100, "Continue"),
        Map.entry(101, "Switching Protocols"),
        Map.entry(102, "Processing"),
        Map.entry(103, "Checkpoint"),
        Map.entry(200, "OK"),
        Map.entry(201, "Created"),
        Map.entry(202, "Accepted"),
        Map.entry(203, "Non-Authoritative Information"),
        Map.entry(204, "No Content"),
        Map.entry(205, "Reset Content"),
        Map.entry(206, "Partial Content"),
        Map.entry(207, "Multi-Status"),
        Map.entry(300, "Multiple Choices"),
        Map.entry(301, "Moved Permanently"),
        Map.entry(302, "Found"),
        Map.entry(303, "See Other"),
        Map.entry(304, "Not Modified"),
        Map.entry(305, "Use Proxy"),
        Map.entry(306, "Switch Proxy"),
        Map.entry(307, "Temporary Redirect"),
        Map.entry(400, "Bad Request"),
        Map.entry(401, "Unauthorized"),
        Map.entry(402, "Payment Required"),
        Map.entry(403, "Forbidden"),
        Map.entry(404, "Not Found"),
        Map.entry(405, "Method Not Allowed"),
        Map.entry(406, "Not Acceptable"),
        Map.entry(407, "Proxy Authentication Required"),
        Map.entry(408, "Request Timeout"),
        Map.entry(409, "Conflict"),
        Map.entry(410, "Gone"),
        Map.entry(411, "Length Required"),
        Map.entry(412, "Precondition Failed"),
        Map.entry(413, "Request Entity Too Large"),
        Map.entry(414, "Request-URI Too Long"),
        Map.entry(415, "Unsupported Media Type"),
        Map.entry(416, "Requested Range Not Satisfiable"),
        Map.entry(417, "Expectation Failed"),
        Map.entry(418, "I\'m a teapot"),
        Map.entry(422, "Unprocessable Entity"),
        Map.entry(423, "Locked"),
        Map.entry(424, "Failed Dependency"),
        Map.entry(425, "Unordered Collection"),
        Map.entry(426, "Upgrade Required"),
        Map.entry(449, "Retry With"),
        Map.entry(450, "Blocked by Windows Parental Controls"),
        Map.entry(500, "Internal Server Error"),
        Map.entry(501, "Not Implemented"),
        Map.entry(502, "Bad Gateway"),
        Map.entry(503, "Service Unavailable"),
        Map.entry(504, "Gateway Timeout"),
        Map.entry(505, "HTTP Version Not Supported"),
        Map.entry(506, "Variant Also Negotiates"),
        Map.entry(507, "Insufficient Storage"),
        Map.entry(509, "Bandwidth Limit Exceeded"),
        Map.entry(510, "Not Extended")
    );

    @RequestMapping(value = "/error")
    public String handleError(Model model, HttpServletRequest request, HttpSession session) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer status_code = Integer.parseInt(status.toString());
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("status", status_code);
        model.addAttribute("status_string", errors.get(status_code));
        model.addAttribute("request", request);
        return "error";
    }
}
