package com.vagtplan.vagtplan.config;

import com.vagtplan.vagtplan.model.Role;
import com.vagtplan.vagtplan.model.Shift;
import com.vagtplan.vagtplan.model.User;
import com.vagtplan.vagtplan.repository.ShiftRepository;
import com.vagtplan.vagtplan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(UserRepository userRepo, ShiftRepository shiftRepo) {
        return args -> {
            // Opret brugere
            User admin = new User("admin", "admin123", Role.ADMIN);
            User user1 = new User("user1", "test123", Role.USER);
            User user2 = new User("user2", "test123", Role.USER);

            userRepo.save(admin);
            userRepo.save(user1);
            userRepo.save(user2);

            // Opret vagter
            Shift shift1 = new Shift();
            shift1.setStartTime(LocalDateTime.of(2025, 6, 12, 8, 0));
            shift1.setEndTime(LocalDateTime.of(2025, 6, 12, 16, 0));
            shift1.setAssignedTo(user1);

            Shift shift2 = new Shift();
            shift2.setStartTime(LocalDateTime.of(2025, 6, 13, 10, 0));
            shift2.setEndTime(LocalDateTime.of(2025, 6, 13, 18, 0));
            shift2.setAssignedTo(user2);
            shift2.setRequestedTransferTo(user1);  // Simuler pending transfer
            shift2.setTransferPending(true);

            shiftRepo.save(shift1);
            shiftRepo.save(shift2);

            System.out.println("🎉 Hardcoded brugere og vagter er oprettet.");
        };
    }
}
