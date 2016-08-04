package com.example.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OrderedPicProvider implements PictureProvider {

    private final List<String> pics;
    private Iterator<String> iterator;

    public OrderedPicProvider(List<String> pics) {
        this.pics = sortPics(pics);
        this.iterator = pics.iterator();
    }

    private List<String> sortPics(final List<String> pics) {
        List<String> copy = new ArrayList<>(pics);
        Collections.sort(copy);

        return copy;
    }

    @Override
    public String getNextPicture() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            this.iterator = this.pics.iterator();
            return iterator.next();
        }
    }
}
