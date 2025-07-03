package org.example.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entities.User;
import org.example.repository.UserRepository;
import org.example.request.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
@Data
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()) {
            throw new UsernameNotFoundException("User Not Found ..!!");
        }
        return new CustomUserDetails(user.get());
    }

    public User checkIfUserAlreadyExist(UserDTO userDTO)
    {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        return user.orElse(null);
    }

    public Boolean signupUser(UserDTO userDTO)
    {
        //Define a utility function to check if userEmail, password is correct (like password should be greater than 5 characters etc.)
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if(Objects.nonNull(checkIfUserAlreadyExist(userDTO))) {
            return true;
        }
        userRepository.save(User.builder()
                        .username(userDTO.getUsername())
                        .password(userDTO.getPassword())
                        .userCreatedOn(Instant.now())
                .build());
        return false;
    }
}
