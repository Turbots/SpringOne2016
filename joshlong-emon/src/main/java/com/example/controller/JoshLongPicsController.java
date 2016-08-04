package com.example.controller;

import com.example.logic.PictureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoshLongPicsController {

    private final PictureProvider pictureProvider;

    @Autowired
    public JoshLongPicsController(final PictureProvider pictureProvider) {
        this.pictureProvider = pictureProvider;
    }

    @RequestMapping("/joshLong")
    public String joshLongPics() {
        return pictureProvider.getNextPicture();
    }
}
