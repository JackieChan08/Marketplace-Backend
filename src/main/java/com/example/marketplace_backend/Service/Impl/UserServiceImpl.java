package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Repositories.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> {
    private final Long USER_ROLE_ID = 1L;
    private final Long ADMIN_ROLE_ID = 2L;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
//    private final PasswordEncoder passwordEncoder;

    // не забыть добавить в контроллер при добавлении security: PasswordEncoder passwordEncoder
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, OrderRepository orderRepository) {
        super(userRepository);
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }
    @Override
    public User save(User myUser) {
        if (!userRepository.findByLogin(myUser.getLogin()).isPresent()) {
//            myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
            myUser.setRoles(List.of(roleRepository.findById(USER_ROLE_ID).get()));
            return userRepository.save(myUser);
        }else {
            return null;
        }
    }
    public List<Order> ordersByUser(User user) {
        return userRepository.ordersByUser(user);
    }

}
