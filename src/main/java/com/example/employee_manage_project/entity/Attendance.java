package com.example.employee_manage_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long lateMinutes;
    @Version
    @Column(nullable = false)
    private Long version;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    public enum Status {
        ON_TIME,
        LATE
    }
}
