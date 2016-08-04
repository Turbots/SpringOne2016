package com.example;

import com.example.logic.OrderedPicProvider;
import com.example.logic.PictureProvider;
import com.example.logic.RandomPicProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("com.example.controller")
@ConditionalOnWebApplication
public class JoshLongEmonAutoConfiguration {

    private static final List<String> wildPokemon;

    static {
        wildPokemon = new ArrayList<>();

        wildPokemon.add("https://pbs.twimg.com/media/CpAEg2WUkAA46Xk.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/CpAFCCgVIAAZbGB.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co_5W8rVYAEcW5g.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co_m3YiUEAAUCXw.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co_EWBgVYAAnPME.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co-tpclUIAI7sFC.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co-j-c2UIAALN-o.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co-YP1ZUsAA0y8q.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co-SqeGUsAEgADj.jpg");
        wildPokemon.add("https://pbs.twimg.com/media/Co-WR0mVMAAxv4y.jpg");
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "josh.long", name = "pics", havingValue = "random")
    public PictureProvider randomizedJoshLongPicProvider() {
        return new RandomPicProvider(wildPokemon);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "josh.long", name = "pics", havingValue = "ordered")
    public PictureProvider orderedJoshLongPicProvider() {
        return new OrderedPicProvider(wildPokemon);
    }

    @Bean
    @ConditionalOnMissingBean
    public PictureProvider defaultJoshLongPicProvider() {
        return new OrderedPicProvider(wildPokemon);
    }
}
