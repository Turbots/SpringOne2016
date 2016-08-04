package be.ordina.jworks.rpsls.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/play")
    public String play() {
        return "play";
    }
}
