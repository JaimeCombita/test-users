package com.company.users.especifications;

import com.company.users.crosscutting.Roles;
import com.company.users.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecifications {
    public static Specification<User> hasNameLike(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasEmailLike(String email) {
        return (root, query, cb) -> email == null ? null :
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> isActive(Boolean isActive) {
        return (root, query, cb) -> isActive == null ? null :
                cb.equal(root.get("isActive"), isActive);
    }

    public static Specification<User> hasRole(Roles role) {
        return (root, query, cb) -> role == null ? null :
                cb.equal(root.get("rol"), role);
    }

    public static Specification<User> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("created"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("created"), from);
            return cb.lessThanOrEqualTo(root.get("created"), to);
        };
    }

    public static Specification<User> modifiedBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("modified"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("modified"), from);
            return cb.lessThanOrEqualTo(root.get("modified"), to);
        };
    }
}
