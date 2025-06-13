package com.vagtplan.vagtplan.repository;

import com.vagtplan.vagtplan.model.Shift;
import com.vagtplan.vagtplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByAssignedTo(User user);
    List<Shift> findByTransferPendingTrue();

}
