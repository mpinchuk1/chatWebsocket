package com.company.config;

import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.entities.UserRole;
import com.company.repo.RoomRepository;
import com.company.repo.RoomService;
import com.company.repo.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration {

    public static final String ADMIN = "admin";


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final RoomRepository roomRepository,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                userService.addUser(ADMIN,
                        encoder.encode("!assword"),
                        UserRole.ADMIN, "kek", "77777777");
                userService.addUser("user",
                        encoder.encode("password"),
                        UserRole.USER, "", "");
//                ChatRoom test = new ChatRoom("PublicTestRoom");
//                test.setOwner(userService.findByLogin(ADMIN));
//                test.addUserToRoom(userService.findByLogin(ADMIN));
//                test.addUserToRoom(userService.findByLogin("user"));
//                roomRepository.save(test);
            }
        };
    }
}
