package com.company.controller;

import com.company.entities.*;
import com.company.repo.RoomService;
import com.company.repo.UserService;
import com.company.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/getAllUsers")
    public List<CustomUserDTO> getAllUsers() {

        List<CustomUserDTO> list = new ArrayList<>();
        userService.getAllUsers().forEach(u -> list.add(new CustomUserDTO(u.getId(), u.getLogin(), u.getRole())));

        return list;
    }

    @GetMapping("/fetchAllRooms")
    public List<ChatRoom> fetchAllRooms() {

        return roomService.getAllRooms();
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<Void> update(@RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) int stateNumber) {      //RequestParam type??? Need to convert to byte[]
        CustomUser user = getCurrentUser().getUser();

        String login = user.getLogin();
        if(userService.updateUser(login, email, phone) && userService.setUserStatus(user.getId(),stateNumber)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

    @RequestMapping(value = "/newuser")
    public ResponseEntity<Void> newuser(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone) {
        logger.info("handling register user request: " + login);
        String passHash = passwordEncoder.encode(password);

        if (userService.addUser(login, passHash, UserRole.USER, email, phone)) {

            return ResponseEntity.ok().build();

        }
        return ResponseEntity.badRequest().build();

    }

//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    public String delete(@RequestParam(name = "toDelete[]", required = false) List<Long> ids,
//                         Model model) {
//        userService.deleteUsers(ids);
//        model.addAttribute("users", userService.getAllUsers());
//
//        return "admin";
//    }

    @RequestMapping(value = "/setUserState", method = RequestMethod.POST)
    public ResponseEntity<Void> setOnlineState(@RequestParam int stateNumber){
        //logger.info("setUserState -------" + userId + " num " + stateNumber);
        CustomUser user = getCurrentUser().getUser();
        if(userService.setUserStatus(user.getId(), stateNumber))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();

    }

    @GetMapping("/getCurUser")
    public CustomUser getCurUserLogin(){
        CustomUserDetails tempUser = getCurrentUser();
        return tempUser.getUser();
    }

    @RequestMapping("/login")
    public ModelAndView loginPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // !!!
    public ModelAndView admin(Model model) {
        //model.addAttribute("users", userService.getAllUsers());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping("/unauthorized")
    public ModelAndView unauthorized(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //model.addAttribute("login", user.getUsername());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("unauthorized");
        return modelAndView;
    }

    // ----

    private CustomUserDetails getCurrentUser() {
        //logger.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));

        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority()))
                return true;
        }

        return false;
    }
}
