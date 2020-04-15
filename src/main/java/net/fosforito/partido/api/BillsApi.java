package net.fosforito.partido.api;

import net.fosforito.partido.model.bill.Bill;
import net.fosforito.partido.model.bill.BillDTO;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.split.Split;
import net.fosforito.partido.model.split.SplitDTO;
import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
  public Bill createBillForGroup(@RequestBody BillDTO billDTO, @PathVariable Long groupId) {
    Bill bill = new Bill(
        billDTO.getDescription(),
        billDTO.getTotalAmount(),
        billDTO.getParts(),
        billDTO.getBillingDate(),
        new Date(),
        groupRepository.findById(groupId).get(),
        currentUserContext.getCurrentUser(),
        convertToSplits(billDTO.getSplits())
    );
    return billRepository.save(bill);
  }

  @PutMapping(value = "/groups/{groupId}/bills/{billId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userCanReadGroup(principal, #groupId)")
  public Bill updateBill(@PathVariable Long groupId, @PathVariable Long billId, @RequestBody BillDTO billDTO) throws Exception {
    return billRepository.findById(billId)
            .map(bill -> {
              bill.setDescription(billDTO.getDescription());
              bill.setTotalAmount(billDTO.getTotalAmount());
              bill.setParts(billDTO.getParts());
              bill.setBillingDate(billDTO.getBillingDate());
              bill.setSplits(convertToSplits(billDTO.getSplits()));
              return billRepository.save(bill);
            }).orElseThrow(() -> new Exception("Bill not found with id " + billId));
  }

  @GetMapping(value = "/bills/{billId}", produces = MediaType.APPLICATION_JSON)
  @PostAuthorize("@securityService.userCanReadGroup(principal, returnObject.group.id)")
  public Bill getBill(@PathVariable Long billId) {
    return billRepository.findById(billId).get();
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
      splits.add(new Split(
          userRepository.findById(splitDTO.getDebtor()).get(),
          splitDTO.getPaid(),
          splitDTO.getPartsOfBill()
      ));
    }
    return splits;
  }
}
