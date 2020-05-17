package com.company.repo;

import com.company.config.AppConfig;
import com.company.entities.ChatRoom;
import com.company.entities.CustomUser;
import com.company.entities.UserRole;
import com.company.entities.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public UserService(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CustomUser findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Transactional(readOnly = true)
    public CustomUser findByID(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean setUserStatus(Long userId, int stateNumber) {
        CustomUser user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return false;
        switch (stateNumber){
            case 0:
                user.setState(UserState.OFFLINE);
                break;
            case 1:
                user.setState(UserState.ONLINE);
                break;
            case 2:
                user.setState(UserState.AWAY);
                break;
        }

        userRepository.save(user);
        return true;
    }

    @Transactional
    public void deleteUsers(List<Long> ids) {
        ids.forEach(id -> {
            Optional<CustomUser> user = userRepository.findById(id);
            user.ifPresent(u -> {
                if ( ! AppConfig.ADMIN.equals(u.getLogin())) {
                    userRepository.deleteById(u.getId());
                }
            });
        });
    }

    @Transactional
    public boolean addUser(String login, String passHash, UserRole role,
                           String email, String phone) {
        if (userRepository.existsByLogin(login))
            return false;
        ChatRoom userRoom = new ChatRoom(login);
        CustomUser user = new CustomUser(login, passHash, role, email, phone);
        userRoom.setOwner(user);
        userRoom.addUserToRoom(user);
        userRepository.save(user);
        roomRepository.save(userRoom);
        return true;
    }

    @Transactional
    public boolean updateUser(String login, String email, String phone) {
        CustomUser user = userRepository.findByLogin(login);
        if (user == null)
            return false;

        user.setEmail(email);
        user.setPhone(phone);
        //user.setImg(img);

        userRepository.save(user);
        return true;
    }
}
