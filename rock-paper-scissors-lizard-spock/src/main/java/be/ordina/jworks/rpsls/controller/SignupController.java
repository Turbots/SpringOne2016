package be.ordina.jworks.rpsls.controller;

import be.ordina.jworks.rpsls.configuration.AuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@Controller
public class SignupController {

    private final ProviderSignInUtils providerSignInUtils;

    @Autowired
    public SignupController(final ConnectionFactoryLocator connectionFactoryLocator, final UsersConnectionRepository usersConnectionRepository) {
        this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }

    @RequestMapping("/signup")
    public String signup(final WebRequest webRequest) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);

        if (connection != null) {
            AuthenticationUtil.authenticate(connection);
            providerSignInUtils.doPostSignUp(connection.getDisplayName(), webRequest);
        }

        return "redirect:/";
    }
}
