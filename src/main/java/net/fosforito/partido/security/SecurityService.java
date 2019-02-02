package net.fosforito.partido.security;

import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class SecurityService {

  @Inject
  private UserRepository userRepository;

  @Inject
  private GroupRepository groupRepository;

  public SecurityService() {
  }

  /**
   * Determine if a user can view a given image.
   */
  public boolean userCanUpdateGroup(UserDetails userDetails, Long groupId) {
/*    User user = userRepository.findByUsername(userDetails.getUsername());
    Group group = groupRepository.findById(groupId).get();
    if (userIsFounderOfGroup(user, group)
        || userListContainsUser(group.getUsers(), user)) {
      return true;
    }*/
    return false;
  }

  public boolean userIsFounderOfGroup(User user, Group group) {
    return group.getFounder().getId().equals(user.getId());
  }

  public boolean userListContainsUser(List<User> userList, User user) {
    for (User listUser : userList) {
      if (listUser.getId().equals(user.getId())) {
        return true;
      }
    }
    return false;
  }
}

