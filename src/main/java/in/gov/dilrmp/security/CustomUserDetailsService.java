package in.gov.dilrmp.security;

import in.gov.dilrmp.controllers.Login.HomePageController;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositry userRepository;
   



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
        User user = userRepository.getUserByUsername(username);

        String token = HomePageController.getOneTimeToken();
        if (user != null) {
            String combinedValue = user.getPassword();
            String hashedValue = SecurityUtils.hashWithSHA256(combinedValue+token);
            user.setPassword(hashedValue);
            return new UserService(user);
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

}


