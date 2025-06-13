package com.vagtplan.vagtplan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedTo;

    @ManyToOne
        @JoinColumn(name = "requested_transfer_to_id")
    private User requestedTransferTo;

    private boolean transferPending = false;

    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public User getRequestedTransferTo() {
    return requestedTransferTo;
}

public void setRequestedTransferTo(User requestedTransferTo) {
    this.requestedTransferTo = requestedTransferTo;
}

public boolean isTransferPending() {
    return transferPending;
}

public void setTransferPending(boolean transferPending) {
    this.transferPending = transferPending;
}


}
