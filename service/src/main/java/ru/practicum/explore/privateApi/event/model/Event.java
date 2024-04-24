package ru.practicum.explore.privateApi.event.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;
    private String annotation;
    private String description;

    @Column(name = "created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Column(name = "published")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean paid;
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private Integer participantLimit;
    private Long views;
    private Integer confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
