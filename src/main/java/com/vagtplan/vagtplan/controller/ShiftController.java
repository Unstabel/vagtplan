package com.vagtplan.vagtplan.controller;

import com.vagtplan.vagtplan.model.Shift;
import com.vagtplan.vagtplan.model.User;
import com.vagtplan.vagtplan.repository.ShiftRepository;
import com.vagtplan.vagtplan.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shifts")
@CrossOrigin(origins = "http://localhost:5173")
public class ShiftController {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public ShiftController(ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }


    // GET /users
@GetMapping("/users")
public List<User> getAllUsers() {
    return userRepository.findAll();
}

    // Endpoint: Opret en vagt
    @PostMapping("/create")
    public String createShift(@RequestBody Shift shift, @RequestParam String username) {
    User user = userRepository.findByUsernameIgnoreCase(username);
    if (user == null) {
        return "Brugeren findes ikke.";
    }

    System.out.println("Opretter vagt for bruger: " + user.getUsername());
    shift.setAssignedTo(user);
    shiftRepository.save(shift);
    return "Vagt oprettet for " + user.getUsername();
}


    // Endpoint: Se alle vagter
    @GetMapping("/all")
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }


    // Hent vagter for en bestemt bruger
@GetMapping("/user")
public ResponseEntity<?> getShiftsForLoggedInUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // hent principal som User objekt
    Object principal = auth.getPrincipal();

    String loggedInUsername;

    if (principal instanceof User) {
        loggedInUsername = ((User) principal).getUsername();
    } else if (principal instanceof String) {
        loggedInUsername = (String) principal; // fallback
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ugyldig bruger");
    }

    System.out.println("Logged in username: " + loggedInUsername);

    User user = userRepository.findByUsernameIgnoreCase(loggedInUsername);
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bruger ikke fundet");
    }

    List<Shift> shifts = shiftRepository.findByAssignedTo(user);
    return ResponseEntity.ok(shifts);
}



@DeleteMapping("/{id}")
public ResponseEntity<String> deleteShift(@PathVariable Long id) {
    Optional<Shift> optionalShift = shiftRepository.findById(id);
    if (optionalShift.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    shiftRepository.deleteById(id);
    return ResponseEntity.ok("Vagt slettet.");
}


@PutMapping("/{id}")
public ResponseEntity<Shift> updateShift(@PathVariable Long id, @RequestBody Shift updatedShift) {
    Optional<Shift> optionalShift = shiftRepository.findById(id);
    if (optionalShift.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Shift existingShift = optionalShift.get();
    existingShift.setStartTime(updatedShift.getStartTime());
    existingShift.setEndTime(updatedShift.getEndTime());
    existingShift.setAssignedTo(updatedShift.getAssignedTo());

    shiftRepository.save(existingShift);
    return ResponseEntity.ok(existingShift);
}





@PostMapping("/request-transfer")
public String requestShiftTransfer(@RequestParam Long shiftId,
                                   @RequestParam String fromUsername,
                                   @RequestParam String toUsername) {
    User fromUser = userRepository.findByUsernameIgnoreCase(fromUsername);
    User toUser = userRepository.findByUsernameIgnoreCase(toUsername);
    Optional<Shift> optionalShift = shiftRepository.findById(shiftId);

    if (fromUser == null || toUser == null || optionalShift.isEmpty()) {
        return "Ugyldige brugere eller vagt.";
    }

    Shift shift = optionalShift.get();
    if (!shift.getAssignedTo().getUsername().equals(fromUsername)) {
        return "Du kan kun anmode om at overdrage dine egne vagter.";
    }

    shift.setRequestedTransferTo(toUser);
    shift.setTransferPending(true);
    shiftRepository.save(shift);

    return "Overførselsanmodning sendt fra " + fromUsername + " til " + toUsername;
}







@PostMapping("/approve-transfer")
public String approveTransfer(@RequestParam Long shiftId) {
    Optional<Shift> optionalShift = shiftRepository.findById(shiftId);

    if (optionalShift.isEmpty()) {
        return "Vagt ikke fundet.";
    }

    Shift shift = optionalShift.get();

    if (!shift.isTransferPending() || shift.getRequestedTransferTo() == null) {
        return "Der er ingen overførselsanmodning for denne vagt.";
    }

    // Udfør overdragelse
    shift.setAssignedTo(shift.getRequestedTransferTo());
    shift.setRequestedTransferTo(null);
    shift.setTransferPending(false);
    shiftRepository.save(shift);

    return "Vagtoverførsel godkendt.";
}



@PostMapping("/reject-transfer")
public String rejectTransfer(@RequestParam Long shiftId) {
    Optional<Shift> optionalShift = shiftRepository.findById(shiftId);

    if (optionalShift.isEmpty()) {
        return "Vagt ikke fundet.";
    }

    Shift shift = optionalShift.get();

    if (!shift.isTransferPending() || shift.getRequestedTransferTo() == null) {
        return "Der er ingen overførselsanmodning for denne vagt.";
    }

    // Nulstil transfer
    shift.setRequestedTransferTo(null);
    shift.setTransferPending(false);
    shiftRepository.save(shift);

    return "Vagtoverførsel afvist.";
}

@PostMapping("/cancel-transfer")
public String cancelTransfer(@RequestParam Long shiftId, @RequestParam String username) {
    Optional<Shift> optionalShift = shiftRepository.findById(shiftId);
    User user = userRepository.findByUsernameIgnoreCase(username);

    if (optionalShift.isEmpty() || user == null) {
        return "Vagt eller bruger ikke fundet.";
    }

    Shift shift = optionalShift.get();

    if (!shift.getAssignedTo().getUsername().equals(username)) {
        return "Du kan kun annullere dine egne vagter.";
    }

    shift.setTransferPending(false);
    shift.setRequestedTransferTo(null);
    shiftRepository.save(shift);

    return "Bytteanmodning annulleret.";
}



@GetMapping("/transfer-requests")
public List<Shift> getPendingTransferRequests() {
    return shiftRepository.findByTransferPendingTrue();
}




}
