package net.fosforito.partido.api;

import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillDTO;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.split.SplitDTO;
import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.*;

@RestController
public class BillsApi {

  private final GroupRepository groupRepository;
  private final BillRepository billRepository;
  private final UserRepository userRepository;
  private final CurrentUserContext currentUserContext;

  @Inject
  public BillsApi(GroupRepository groupRepository,
                  BillRepository billRepository,
                  UserRepository userRepository,
                  CurrentUserContext currentUserContext) {
    this.groupRepository = groupRepository;
    this.billRepository = billRepository;
    this.userRepository = userRepository;
    this.currentUserContext = currentUserContext;
  }

  @GetMapping(value = "/groups/{groupId}/bills", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public List<Bill> getAllBillsForGroup(@PathVariable Long groupId) {
    List<Bill> bills = billRepository.findAllByGroupId(groupId);
    bills.sort(Collections.reverseOrder(null));
    return bills;
  }

  @PostMapping(value = "/groups/{groupId}/bills", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public ResponseEntity<Bill> createBillForGroup(@RequestBody BillDTO billDTO, @PathVariable Long groupId) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    if (groupOptional.isPresent()) {
      Bill bill = new Bill(
              billDTO.getDescription(),
              billDTO.getCategory(),
              billDTO.getTotalAmount(),
              billDTO.getParts(),
              billDTO.getBillingDate(),
              new Date(),
              groupOptional.get(),
              currentUserContext.getCurrentUser(),
              convertToSplits(billDTO.getSplits())
      );
      return new ResponseEntity<>(billRepository.save(bill), HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PutMapping(value = "/groups/{groupId}/bills/{billId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public ResponseEntity<Bill> updateBill(@PathVariable Long groupId, @PathVariable Long billId, @RequestBody BillDTO billDTO) throws Exception {
    Optional<Bill> billOptional = billRepository.findById(billId);
    if (billOptional.isPresent()) {
      return new ResponseEntity<>(billOptional.map(bill -> {
        bill.setDescription(billDTO.getDescription());
        bill.setCategory(billDTO.getCategory());
        bill.setTotalAmount(billDTO.getTotalAmount());
        bill.setParts(billDTO.getParts());
        bill.setBillingDate(billDTO.getBillingDate());
        bill.setSplits(convertToSplits(billDTO.getSplits()));
        return billRepository.save(bill);
      }).get(), HttpStatus.OK);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/bills/{billId}", produces = MediaType.APPLICATION_JSON)
  @PostAuthorize("@securityService.userCanReadGroup(principal, returnObject.body.group.id)")
  public ResponseEntity<Bill> getBill(@PathVariable Long billId) {
    Optional<Bill> billOptional = billRepository.findById(billId);
    return billOptional.map(bill -> new ResponseEntity<>(bill, HttpStatus.OK))
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/bills/{billId}")
  @PreAuthorize("@securityService.userCanDeleteBill(principal, #billId)")
  public ResponseEntity<?> deleteBill(@PathVariable Long billId) throws Exception {
    return billRepository.findById(billId)
            .map(bill -> {
              billRepository.delete(bill);
              return ResponseEntity.ok().build();
            }).orElseThrow(() -> new Exception("Bill not found with id " + billId));
  }

  private List<Split> convertToSplits(List<SplitDTO> splitDTOs) {
    List<Split> splits = new ArrayList<>();
    for (SplitDTO splitDTO : splitDTOs) {
      Optional<User> userOptional = userRepository.findById(splitDTO.getDebtor());
      userOptional.ifPresent(user -> splits.add(new Split(
              user,
              splitDTO.getPaid(),
              splitDTO.getPartsOfBill()
      )));
    }
    return splits;
  }
}
