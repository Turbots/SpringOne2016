package be.ordina.jworks.rpsls.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class Player implements Serializable {

    private String displayName;
    private String imageUrl;
    private String profileUrl;
}
