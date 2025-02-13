package com.newmeta.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.newmeta.domain.ScmCommunity;
import com.newmeta.persistence.ScmCommunityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScmCommunityService {

    private final ScmCommunityRepository scmCommunityRepo;

    // 게시글 생성
    public ScmCommunity createPost(ScmCommunity scmCommunity) {
    	scmCommunity.setCreatedAt(LocalDateTime.now());
    	scmCommunity.setUpdatedAt(LocalDateTime.now());
        ScmCommunity savedPost = scmCommunityRepo.save(scmCommunity);
        log.info("게시글 생성 완료: {}", savedPost);
        return savedPost;
    }

    // 게시글 조회 (전체)
    public List<ScmCommunity> getAllPosts() {
        List<ScmCommunity> posts = scmCommunityRepo.findAll();
        log.info("전체 게시글 조회: {} 건", posts.size());
        return posts;
    }
    
    public List<ScmCommunity> getMyPosts() {
        // 현재 로그인된 사용자 이름 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();  // 현재 사용자 이름 (JWT에서 가져옴)

        // 자신이 작성한 게시글만 반환
        return scmCommunityRepo.findByAdminUsername(currentUsername);
    }

    // 게시글 조회 (ID 기준)
    public Optional<ScmCommunity> getPostById(Long id) {
        Optional<ScmCommunity> post = scmCommunityRepo.findById(id);
        post.ifPresentOrElse(
            p -> log.info("게시글 조회 성공: {}", p),
            () -> log.warn("게시글 조회 실패, id: {}", id)
        );
        return post;
    }

    // 게시글 수정
    public ScmCommunity updatePost(Long id, ScmCommunity updatedPost) {
        Optional<ScmCommunity> existingOpt = scmCommunityRepo.findById(id);
        if(existingOpt.isPresent()){
            ScmCommunity existing = existingOpt.get();
            existing.setTitle(updatedPost.getTitle());
            existing.setContent(updatedPost.getContent());
            existing.setUpdatedAt(LocalDateTime.now());
            ScmCommunity saved = scmCommunityRepo.save(existing);
            log.info("게시글 수정 완료: {}", saved);
            return saved;
        } else {
            log.warn("게시글 수정 실패, id: {}", id);
            return null; // 또는 예외 처리
        }
    }

    // 게시글 삭제
    public void deletePost(Long id) {
    	scmCommunityRepo.deleteById(id);
        log.info("게시글 삭제 완료, id: {}", id);
    }
}
