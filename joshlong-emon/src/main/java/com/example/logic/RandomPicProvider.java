package com.example.logic;

import java.util.List;
import java.util.Random;

public class RandomPicProvider implements PictureProvider {

    private final static Random RAND = new Random();

    private final List<String> pics;

    public RandomPicProvider(final List<String> pics) {
        this.pics = pics;
    }

    @Override
    public String getNextPicture() {
        return pics.get(RAND.nextInt(pics.size()));
    }
}
