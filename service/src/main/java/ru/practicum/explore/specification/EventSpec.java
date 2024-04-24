package ru.practicum.explore.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore.admin.category.model.Category;
import ru.practicum.explore.admin.event.parametrs.SearchingParams;
import ru.practicum.explore.admin.user.model.User;
import ru.practicum.explore.privateApi.event.model.Event;
import ru.practicum.explore.privateApi.event.model.Event_;
import ru.practicum.explore.privateApi.event.model.State;
import ru.practicum.explore.publicAPI.event.EventSearchingParams;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class EventSpec {
    public static Specification<Event> filterForPublic(EventSearchingParams params,
                                                       State state) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (params.getText() != null) {
                String search = "%" + params.getText().toUpperCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.upper(root.get(Event_.ANNOTATION)), search),
                        criteriaBuilder.like(criteriaBuilder.upper(root.get(Event_.DESCRIPTION)), search)
                ));
            }
            ;
            if (params.getCategories() != null) {
                Join<Event, Category> join = root.join(Event_.CATEGORY, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(params.getCategories()));
            }
            if (params.getPaid() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Event_.PAID), params.getPaid()));
            }
            if (state != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Event_.STATE), state));
            }
            LocalDateTime rangeStart = params.getRangeStart();
            LocalDateTime rangeEnd = params.getRangeEnd();
            if (rangeStart != null && rangeEnd == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), rangeStart));
            }
            if (rangeEnd != null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), LocalDateTime.now(), rangeEnd)
                );
            }
            if (rangeEnd != null && rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), rangeStart, rangeEnd)
                );
            }
            if (rangeEnd == null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), LocalDateTime.now()));
            }
            return predicate;
        });
    }

    public static Specification<Event> filterForAdmin(SearchingParams params) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (params.getCategories() != null) {
                Join<Event, Category> join = root.join(Event_.CATEGORY, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(params.getCategories()));
            }
            if (params.getUsers() != null) {
                Join<Event, User> join = root.join(Event_.INITIATOR, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(params.getUsers()));
            }
            if (params.getStates() != null) {
                predicate = criteriaBuilder.and(predicate, root.get(Event_.STATE).in(params.getStates()));
            }
            LocalDateTime rangeStart = params.getRangeStart();
            LocalDateTime rangeEnd = params.getRangeEnd();
            if (rangeStart != null && rangeEnd == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), rangeStart));
            }
            if (rangeEnd != null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), LocalDateTime.now(), rangeEnd)
                );
            }
            if (rangeEnd != null && rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), rangeStart, rangeEnd)
                );
            }
            if (rangeEnd == null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), LocalDateTime.now()));
            }
            return predicate;
        });
    }
}
