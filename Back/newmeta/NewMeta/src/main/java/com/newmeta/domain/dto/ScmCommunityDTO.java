package com.newmeta.domain.dto;

import java.time.LocalDateTime;

import com.newmeta.domain.ScmCommunity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScmCommunityDTO {

    private Long id;
    private String title;
    private String content;
    private String username;  // 작성자 username
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScmCommunityDTO fromEntity(ScmCommunity scmCommunity) {
        return ScmCommunityDTO.builder()
                .id(scmCommunity.getId())
                .title(scmCommunity.getTitle())
                .content(scmCommunity.getContent())
                .username(scmCommunity.getAdmin().getUsername())  // Admin의 username 가져오기
                .createdAt(scmCommunity.getCreatedAt())
                .updatedAt(scmCommunity.getUpdatedAt())
                .build();
    }
}
