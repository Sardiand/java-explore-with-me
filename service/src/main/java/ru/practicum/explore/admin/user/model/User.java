package ru.practicum.explore.admin.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String email;
}
