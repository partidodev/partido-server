package net.fosforito.partido.api;

import net.fosforito.partido.model.group.*;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GroupsApi {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupService groupService;
  private final CurrentUserContext currentUserContext;

  @Inject
  public GroupsApi(GroupRepository groupRepository,
                   UserRepository userRepository,
                   GroupService groupService,
                   CurrentUserContext currentUserContext) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.groupService = groupService;
    this.currentUserContext = currentUserContext;
  }

  // Groups

  @GetMapping(
      value = "/groups",
      produces = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Group> getGroups(Pageable pageable) {
    return groupRepository.findAll(pageable);
  }

  @GetMapping(
      value = "/currentusergroups",
      produces = MediaType.APPLICATION_JSON
  )
  public List<Group> getCurrentUsersGroups() {
    return groupRepository.findAllByUsersIsContaining(currentUserContext.getCurrentUser());
  }

  @PostMapping(
      value = "/groups",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON
  )
  public Group createGroup(@RequestBody GroupDTO groupDTO) {
    User founder = currentUserContext.getCurrentUser();
    List<User> userList = new ArrayList<>();
    userList.add(founder);
    Group group = new Group();
    group.setName(groupDTO.getName());
    group.setStatus(groupDTO.getStatus());
    group.setCurrency(groupDTO.getCurrency());
    group.setJoinModeActive(groupDTO.isJoinModeActive());
    group.setJoinKey(groupDTO.getJoinKey());
    group.setFounder(founder);
    group.setUsers(userList);
    return groupRepository.save(group);
  }

  @PutMapping(
      value = "/groups/{groupId}",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("@securityService.userCanUpdateGroup(principal, #groupId)")
  public ResponseEntity<Group> updateGroup(@PathVariable Long groupId, @RequestBody GroupDTO groupDTO) throws Exception {
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

  @DeleteMapping(value = "/groups/{groupId}")
  @PreAuthorize("@securityService.userIsFounderOfGroup(principal, #groupId)")
  public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) throws Exception {
    //TODO: make sure to delete all bills and user-group relations but no user accounts
    return groupRepository.findById(groupId)
        .map(group -> {
          groupRepository.delete(group);
          return ResponseEntity.ok().build();
        }).orElseThrow(() -> new Exception("Group not found with id " + groupId));
  }

  /**
   * Try to join a group.
   * - Returns status code 404 (not found) if group or user does not exist.
   * - Returns status code 304 (not modified) if user is already in the group, returns the group object.
   * - Returns status code 200 (ok) if user has been added to group successfully, returns the updated group object.
   * - Returns status code 403 (forbidden) if group's join mode is inactive or invalid join key was provided.
   * @param groupId ID of group to jin
   * @param groupJoinBodyDTO conatins the id of user who wants to join group and the group key
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
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * ADMIN only
   *
   * Not used by group members. Every user who wants to join a group, must join it explicitly with a joinKey.
   *
   * @param groupId ID of group where a user wants to join
   * @param userId ID of user to join in group
   * @return Group object with updated user list or old user list if user already is in group
   * @throws Exception if user cannot be added to group
   */
  @PostMapping(value = "/groups/{groupId}/users/{userId}", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("hasRole('ADMIN')")
  public Group addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) throws Exception {
    return groupRepository.findById(groupId).map(group -> {
      List<User> groupUsers = group.getUsers();
      for (User user : groupUsers) {
        if (user.getId().equals(userId)) {
          return group; // User already in group, just return the group as it is
        }
      }
      groupUsers.add(userRepository.findById(userId).get());
      group.setUsers(groupUsers);
      return groupRepository.save(group);
    }).orElseThrow(() -> new Exception("Cannot add user with id " + userId + " to group with id " + groupId));
  }

  /**
   * ADMIN only
   */
  @DeleteMapping(value = "/groups/{groupId}/users/{userId}", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userIsSameUserAndFromGroup(principal, #userId, #groupId) " +
      "AND @securityService.userIsNotFounderOfGroup(#userId, #groupId)")
  public Group removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) throws Exception {
    return groupRepository.findById(groupId).map(group -> {
      List<User> groupUsers = group.getUsers();
      groupUsers.remove(userRepository.findById(userId).get());
      group.setUsers(groupUsers);
      return groupRepository.save(group);
    }).orElseThrow(() -> new Exception("Cannot remove user with id " + userId + " from group with id " + groupId));
  }

  /**
   * Calculate current group balances
   * @param groupId ID of the group, the report should be generated for
   * @return Report with List of current balances
   */
  @GetMapping(value = "/groups/{groupId}/report")
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Report getGroupReport(@PathVariable Long groupId) {
    return groupService.createActualGroupReport(groupId);
  }
}
