package net.fosforito.partido.security;

import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillRepository;
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

  @Inject
  private BillRepository billRepository;

  public SecurityService() {
  }

  /**
   * Determine if a user can view a given image.
   */
  public boolean userCanUpdateGroup(UserDetails userDetails, Long groupId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Group group = groupRepository.findById(groupId).get();
    return userIsFounderOfGroup(user, group)
        || userListContainsUser(group.getUsers(), user);
  }

  public boolean userCanReadGroup(UserDetails userDetails, Long groupId) {
    return userCanUpdateGroup(userDetails, groupId);
  }

  public boolean userIsFounderOfGroup(UserDetails userDetails, Long groupId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Group group = groupRepository.findById(groupId).get();
    return userIsFounderOfGroup(user, group);
  }

  public boolean userIsFounderOfGroup(User user, Group group) {
    return group.getFounder().getId().equals(user.getId());
  }

  public boolean userIsNotFounderOfGroup(Long userId, Long groupId) {
    User user = userRepository.findById(userId).get();
    Group group = groupRepository.findById(groupId).get();
    return !userIsFounderOfGroup(user, group);
  }

  public boolean userIsSameUserAndFromGroup(UserDetails userDetails, Long userId, Long groupId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Group group = groupRepository.findById(groupId).get();
    return (userListContainsUser(group.getUsers(), user) || userIsFounderOfGroup(user, group))
        && user.getId().equals(userId);
  }

  public boolean userIsSameUser(UserDetails userDetails, Long userId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    return user.getId().equals(userId);
  }

  public  boolean userCanDeleteBill(UserDetails userDetails, Long billId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Bill bill = billRepository.findById(billId).get();
    return userIsFounderOfGroup(user, bill.getGroup())
        || bill.getCreator().getId().equals(user.getId());
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

