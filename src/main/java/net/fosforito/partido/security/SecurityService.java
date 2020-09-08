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
import java.util.Optional;

/**
 * This class contains functions to determine the rights
 * of given users to perform specific actions in given groups.
 */
@Service
public class SecurityService {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final BillRepository billRepository;

  @Inject
  public SecurityService(UserRepository userRepository,
                         GroupRepository groupRepository,
                         BillRepository billRepository) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.billRepository = billRepository;
  }

  /**
   * Determine if a given user can update a given group
   * @param userDetails of the user who wants to perform the action
   * @param groupId of the group to be updated
   * @return true if user can update group
   */
  public boolean userCanUpdateGroup(UserDetails userDetails, Long groupId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    return groupOptional
        .filter(group -> userListContainsUser(group.getUsers(), user))
        .isPresent();
  }

  /**
   * Determine if a given user can read a given group
   * @param userDetails of the user who wants to perform the action
   * @param groupId of the group to be read
   * @return true if user can read group
   */
  public boolean userCanReadGroup(UserDetails userDetails, Long groupId) {
    return userCanUpdateGroup(userDetails, groupId);
  }

  /**
   * Determine if given user details are from the same user with
   * a given id and if the user is in a specific group.
   * @param userDetails details of teh given user
   * @param userId id  of the given user
   * @param groupId id of the given group
   * @return true if user details and id are from the same user and the user is in the given group
   */
  public boolean userIsSameUserAndFromGroup(UserDetails userDetails, Long userId, Long groupId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    return groupOptional
        .filter(group -> userListContainsUser(group.getUsers(), user) && user.getId().equals(userId))
        .isPresent();
  }

  /**
   * Determine if given user details are from the same user with a given id
   * @param userDetails details of the given user
   * @param userId id of the given user
   * @return true if details and id are from the same user
   */
  public boolean userIsSameUser(UserDetails userDetails, Long userId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    return user.getId().equals(userId);
  }

  /**
   * Determine if a given user can delete a given bill
   * @param userDetails details off the given user
   * @param billId id of the given bill
   * @return true if the user can delete the bill
   */
  public  boolean userCanDeleteBill(UserDetails userDetails, Long billId) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    Optional<Bill> billOptional = billRepository.findById(billId);
    return billOptional
        .filter(bill -> bill.getCreator().getId().equals(user.getId()))
        .isPresent();
  }

  /**
   * Determine if a given list of users contains a given user
   * @param userList list of users in which to search
   * @param user User to be searched in the list
   * @return true if the user is in the list
   */
  public boolean userListContainsUser(List<User> userList, User user) {
    for (User listUser : userList) {
      if (listUser.getId().equals(user.getId())) {
        return true;
      }
    }
    return false;
  }
}

