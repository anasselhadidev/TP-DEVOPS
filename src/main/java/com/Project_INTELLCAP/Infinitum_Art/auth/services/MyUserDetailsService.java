package com.Project_INTELLCAP.Infinitum_Art.auth.services;

import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.UserPrincipale;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.findByEmail(email);
        System.out.println("user :"+user);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipale(user);
    }
}
