package me.parkhuijun.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Builder
    public UserRole(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserRole={id: "+id+", name: " + name + "}";
    }
}
