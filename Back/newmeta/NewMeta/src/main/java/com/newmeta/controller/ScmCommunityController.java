package com.newmeta.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.newmeta.domain.Admin;
import com.newmeta.domain.ScmCommunity;
import com.newmeta.domain.dto.ScmCommunityDTO;
import com.newmeta.persistence.AdminRepository;
import com.newmeta.service.ScmCommunityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class ScmCommunityController {

    private final ScmCommunityService scmCommunityService;
    private final AdminRepository adminRepository;  // 🔥 AdminRepository 추가

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<ScmCommunityDTO> createPost(@RequestBody ScmCommunity scmCommunity,
                                                      @AuthenticationPrincipal User currentUser) {
        // 🔥 User에서 username 가져오기
        String username = currentUser.getUsername();
        Admin admin = adminRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        scmCommunityService.createPost(scmCommunity, admin);
        return ResponseEntity.ok(ScmCommunityDTO.fromEntity(scmCommunity));
    }
    
    @GetMapping("/all-posts")
    public ResponseEntity<List<ScmCommunityDTO>> getAllPosts() {
        List<ScmCommunity> allPosts = scmCommunityService.getAllPosts();
        List<ScmCommunityDTO> response = allPosts.stream()
                                                 .map(ScmCommunityDTO::fromEntity)
                                                 .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 내 게시글 조회
    @GetMapping("/my-posts")
    public ResponseEntity<List<ScmCommunityDTO>> getMyPosts(@AuthenticationPrincipal User currentUser) {
        String username = currentUser.getUsername();
        Admin admin = adminRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<ScmCommunity> myPosts = scmCommunityService.getMyPosts(admin);
        List<ScmCommunityDTO> response = myPosts.stream()
                                               .map(ScmCommunityDTO::fromEntity)
                                               .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<ScmCommunityDTO> updatePost(@PathVariable Long id, 
                                                      @RequestBody ScmCommunity updatedPost, 
                                                      @AuthenticationPrincipal User currentUser) {
        String username = currentUser.getUsername();
        Admin admin = adminRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ScmCommunity updated = scmCommunityService.updatePost(id, updatedPost, admin);
        return ResponseEntity.ok(ScmCommunityDTO.fromEntity(updated));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        String username = currentUser.getUsername();
        Admin admin = adminRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        scmCommunityService.deletePost(id, admin);
        return ResponseEntity.noContent().build();
    }
}
