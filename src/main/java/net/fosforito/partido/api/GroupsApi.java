package net.fosforito.partido.api;

import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.group.GroupDTO;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.group.GroupService;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupsApi {

  @Inject
  private GroupRepository groupRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GroupService groupService;

  // Groups

  @GetMapping(
      value = "/groups",
      produces = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Group> getGroups(Pageable pageable) {
    return groupRepository.findAll(pageable);
  }

  @PostMapping(
      value = "/groups",
      produces = MediaType.APPLICATION_JSON,
      consumes = MediaType.APPLICATION_JSON
  )
  public Group createGroup(@RequestBody GroupDTO groupDTO) {
    User founder = userRepository.findById(groupDTO.getFounder()).get();
    List<User> userList = new ArrayList<>();
    userList.add(founder);
    Group group = new Group();
    group.setName(groupDTO.getName());
    group.setStatus(groupDTO.getStatus());
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
  public Group updateGroup(@PathVariable Long groupId, @RequestBody GroupDTO groupDTO) throws Exception {
    return groupRepository.findById(groupId)
        .map(group -> {
          group.setName(groupDTO.getName());
          group.setStatus(groupDTO.getStatus());
          return groupRepository.save(group);
        }).orElseThrow(() -> new Exception("Group not found with id " + groupId));
  }

  @GetMapping(
      value = "/groups/{groupId}",
      produces = MediaType.APPLICATION_JSON
  )
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Group getGroup(@PathVariable Long groupId) {
    return groupRepository.findById(groupId).get();
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

  @PostMapping(value = "/groups/{groupId}/users/{userId}", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanUpdateGroup(principal, #groupId)")
  public Group addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) throws Exception {
    //TODO: check if user already exists in group
    return groupRepository.findById(groupId).map(group -> {
      List<User> groupUsers = group.getUsers();
      groupUsers.add(userRepository.findById(userId).get());
      group.setUsers(groupUsers);
      return groupRepository.save(group);
    }).orElseThrow(() -> new Exception("Cannot add user with id " + userId + " to group with id " + groupId));
  }

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

  @GetMapping(value = "/groups/{groupId}/report")
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Report getGroupReport(@PathVariable Long groupId) {
    return groupService.createActualGroupReport(groupId);
  }
}
