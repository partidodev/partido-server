package net.fosforito.partido.api;

import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.checkout.CheckoutReport;
import net.fosforito.partido.model.group.*;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class GroupsApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupsApi.class);

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupService groupService;
  private final CurrentUserContext currentUserContext;
  private final HttpServletRequest request;
  private final EmailService emailService;

  @Inject
  public GroupsApi(GroupRepository groupRepository,
                   UserRepository userRepository,
                   GroupService groupService,
                   CurrentUserContext currentUserContext,
                   HttpServletRequest request,
                   EmailService emailService) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.groupService = groupService;
    this.currentUserContext = currentUserContext;
    this.request = request;
    this.emailService = emailService;
  }

  /**
   * This method returns a list of all groups, a user has access to.
   * @return List with group objects
   */
  @GetMapping(
      value = "/currentusergroups",
      produces = MediaType.APPLICATION_JSON
  )
  public List<Group> getCurrentUsersGroups() {
    return groupRepository.findAllByUsersIsContaining(currentUserContext.getCurrentUser());
  }

  /**
   * Create a new group.
   * @param groupDTO object with information about the new group
   * @return newly created group object as confirmation
   */
  @PostMapping(
      value = "/groups",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON
  )
  public Group createGroup(@RequestBody GroupDTO groupDTO) {
    User currentUser = currentUserContext.getCurrentUser();
    List<User> userList = new ArrayList<>();
    userList.add(currentUser);
    Group group = new Group();
    group.setName(groupDTO.getName());
    group.setStatus(groupDTO.getStatus());
    group.setCurrency(groupDTO.getCurrency());
    group.setJoinModeActive(groupDTO.isJoinModeActive());
    group.setJoinKey(groupDTO.getJoinKey());
    group.setCreationDate(new Date());
    group.setUsers(userList);
    return groupRepository.save(group);
  }

  /**
   * Change a single group's settings
   * @param groupId ID of the group to change settings of
   * @param groupDTO object with updated group settings
   * @return updated group object if succeeded
   */
  @PutMapping(
      value = "/groups/{groupId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("@securityService.userCanUpdateGroup(principal, #groupId)")
  public ResponseEntity<Group> updateGroup(@PathVariable Long groupId, @RequestBody GroupDTO groupDTO) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    if (groupOptional.isPresent()) {
      return new ResponseEntity<>(groupOptional.map(group -> {
        group.setName(groupDTO.getName());
        group.setStatus(groupDTO.getStatus());
        group.setCurrency(groupDTO.getCurrency());
        group.setJoinModeActive(groupDTO.isJoinModeActive());
        group.setJoinKey(groupDTO.getJoinKey());
        return groupRepository.save(group);
      }).get(), HttpStatus.OK);
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Get all information about a single group where the requesting user has access to.
   * Information includes a list of all other users in the same  group.
   * @param groupId ID of the group to get information for
   * @return group object with users list and other information
   */
  @GetMapping(
      value = "/groups/{groupId}",
      produces = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public ResponseEntity<Group> getGroup(@PathVariable Long groupId) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    return groupOptional.map(group -> new ResponseEntity<>(group, HttpStatus.OK))
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Try to join a group.
   * - Returns status code 404 (not found) if group or user does not exist.
   * - Returns status code 304 (not modified) if user is already in the group, returns the group object.
   * - Returns status code 200 (ok) if user has been added to group successfully, returns the updated group object.
   * - Returns status code 403 (forbidden) if group's join mode is inactive or invalid join key was provided.
   * @param groupId ID of group to join
   * @param groupJoinBodyDTO contains the id of user who wants to join group and the group key
   * @return Status code and group object as described above
   */
  @PostMapping(value = "/groups/{groupId}/join", produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<Group> joinGroup(@PathVariable Long groupId, @RequestBody GroupJoinBodyDTO groupJoinBodyDTO) {

    Optional<Group> optionalGroup = groupRepository.findById(groupId);
    Optional<User> optionalUser = userRepository.findById(groupJoinBodyDTO.getUserId());

    Group group;
    User user;

    if (optionalGroup.isPresent() && optionalUser.isPresent()) {
      group = optionalGroup.get();
      user = optionalUser.get();
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (group.isJoinModeActive() && group.getJoinKey().equals(groupJoinBodyDTO.getJoinKey())) {
      List<User> groupUsers = group.getUsers();
      for (User groupUser : groupUsers) {
        if (groupUser.getId().equals(groupJoinBodyDTO.getUserId())) {
          return new ResponseEntity<>(group, HttpStatus.NOT_MODIFIED);
        }
      }
      groupUsers.add(user);
      group.setUsers(groupUsers);
      groupRepository.save(group);
      return new ResponseEntity<>(group, HttpStatus.OK);
    } else {
      LOGGER.warn("Join group attempt for ip {} failed", getClientIP());
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * Allow users of a group to leave the group if group balances are all zero.
   * @param groupId Id of the group to leave
   * @param principal Contains current user's data
   * @return ResponseEntity with status code:
   *         - 200 if user was removed from the group
   *         - 404 if group was not found
   *         - 412 if the group has open (non zero) balances
   */
  @PostMapping(value = "/groups/{groupId}/leave", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanUpdateGroup(principal, #groupId)")
  public ResponseEntity<Group> leaveGroup(@PathVariable Long groupId, Principal principal) {

    User userToRemoveFromGroup = userRepository.findByEmail(principal.getName());
    Optional<Group> optionalGroup = groupRepository.findById(groupId);
    Group group;

    if (optionalGroup.isPresent()) {
      group = optionalGroup.get();
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Report report = getGroupReport(group.getId());
    for (Balance balance : report.getBalances()) {
      if (!(balance.getBalance().compareTo(BigDecimal.ZERO) == 0)) {
        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
      }
    }

    List<User> users = group.getUsers();
    List<User> updatedUsers = new ArrayList<>();
    for (User user: users) {
      if (!user.getId().equals(userToRemoveFromGroup.getId())) {
        updatedUsers.add(user);
      }
    }
    group.setUsers(updatedUsers);
    groupRepository.save(group);

    return new ResponseEntity<>(group, HttpStatus.OK);
  }

  /**
   * Calculate current group balances
   * @param groupId ID of the group, the report should be generated for
   * @return Report with List of current balances
   */
  @GetMapping(value = "/groups/{groupId}/report")
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Report getGroupReport(@PathVariable Long groupId) {
    return groupService.createActualGroupReport(groupId, false);
  }

  @PostMapping(value = "/groups/{groupId}/checkout")
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public ResponseEntity<CheckoutReport> checkoutGroup(@PathVariable Long groupId) {
    CheckoutReport checkoutReport = groupService.checkoutGroup(groupId);
    if (checkoutReport == null) {
      // If checkout report is null, checkout report cannot be made due to zero balances
      return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
    }
    Group group = groupRepository.findById(groupId).get();
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("report", checkoutReport);
    emailService.sendGroupCheckoutMail(group, templateModel);
    return new ResponseEntity<>(checkoutReport, HttpStatus.OK);
  }

  /**
   * Get the IP address of the current user.
   * If possible, use the X-Forwarded-For header set when server runs behind a proxy.
   * @return client's IP as String
   */
  public String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null){
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
