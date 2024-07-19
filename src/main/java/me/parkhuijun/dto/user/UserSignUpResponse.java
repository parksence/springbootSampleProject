package me.parkhuijun.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpResponse {
    private String userId;
    private String name;
    private LocalDateTime regDt;
}
