package net.fosforito.partido.api;

import net.fosforito.partido.model.group.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class GroupsApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupsApi.class);

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupService groupService;
  private final CurrentUserContext currentUserContext;
  private final HttpServletRequest request;

  @Inject
  public GroupsApi(GroupRepository groupRepository,
                   UserRepository userRepository,
                   GroupService groupService,
                   CurrentUserContext currentUserContext,
                   HttpServletRequest request) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.groupService = groupService;
    this.currentUserContext = currentUserContext;
    this.request = request;
  }

  // Groups

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
    group.setCreationDate(new Date());
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
   * Calculate current group balances
   * @param groupId ID of the group, the report should be generated for
   * @return Report with List of current balances
   */
  @GetMapping(value = "/groups/{groupId}/report")
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Report getGroupReport(@PathVariable Long groupId) {
    return groupService.createActualGroupReport(groupId);
  }

  public String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null){
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
