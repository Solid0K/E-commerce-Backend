package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.BadCredentials;
import com.krishu.ecommerce.CustomExceptions.EmailAlreadyExist;
import com.krishu.ecommerce.CustomExceptions.UserNotFound;
import com.krishu.ecommerce.DTO.LoginRequest;
import com.krishu.ecommerce.DTO.MeResponse;
import com.krishu.ecommerce.DTO.RegisterRequest;
import com.krishu.ecommerce.Model.User;
import com.krishu.ecommerce.Repository.UserRepo;
import com.krishu.ecommerce.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepo userRepo,BCryptPasswordEncoder encoder,JwtService jwtService){
        this.userRepo=userRepo;
        this.encoder=encoder;
        this.jwtService=jwtService;
    }

    public void registerUser(RegisterRequest request) {
        String emailToCheck=request.getEmail();
        Optional<User> existUser=userRepo.findByEmail(emailToCheck);
        if(existUser.isPresent()){
            throw new EmailAlreadyExist(existUser.get().getEmail());
        }
        User user=new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRoles(Role.User);
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public String verifyUser(LoginRequest loginRequest) {
        Optional<User> user=userRepo.findByEmail(loginRequest.getEmail());
        if(user.isEmpty()){
            throw new BadCredentials("Bad Credentials");
        }
        if(!encoder.matches(loginRequest.getPassword(),user.get().getPassword())){
            throw new BadCredentials("Bad Credentials");
        }
        return jwtService.generateToken(user.get().getEmail(),user.get().getRoles());
    }

    public MeResponse getCurrentUser(Authentication authentication){
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User "+email+" not found"));

        MeResponse me=new MeResponse();
        me.setEmail(user.getEmail());
        me.setUsername(user.getUsername());
        me.setRoles(user.getRoles());
        return me;
    }
}
