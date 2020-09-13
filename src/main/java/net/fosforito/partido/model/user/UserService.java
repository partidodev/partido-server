package net.fosforito.partido.model.user;

import net.fosforito.partido.util.Pair;


public interface UserService {

    Pair<String, User> createUser(UserDTO userDTO);

    Pair<String, User> deleteUser(Long userId);

    Pair<String, User> updateUser(Long userId, UserDTO userDTO);
}
