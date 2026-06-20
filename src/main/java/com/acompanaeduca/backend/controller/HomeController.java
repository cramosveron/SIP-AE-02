package com.acompanaeduca.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head><meta charset=\"UTF-8\"><title>AcompanaEduca Backend</title></head>"
                + "<body style=\"font-family:Arial,sans-serif;margin:40px;line-height:1.6;\">"
                + "<h1>AcompanaEduca Backend</h1>"
                + "<p>La aplicación está en funcionamiento.</p>"
                + "<ul>"
                + "<li><a href=\"/users\">/users</a></li>"
                + "<li><a href=\"/courses\">/courses</a></li>"
                + "<li><a href=\"/activities\">/activities</a></li>"
                + "<li><a href=\"/evaluations\">/evaluations</a></li>"
                + "</ul>"
                + "<p>Consola H2: <a href=\"/h2-console\">/h2-console</a></p>"
                + "</body></html>";
    }
}
