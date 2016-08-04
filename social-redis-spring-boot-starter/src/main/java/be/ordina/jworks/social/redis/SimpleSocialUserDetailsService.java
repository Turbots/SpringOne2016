package be.ordina.jworks.social.redis;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import java.util.Collections;

public class SimpleSocialUserDetailsService implements SocialUserDetailsService {

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return new SocialUser(userId, "", Collections.emptyList());
    }
}
