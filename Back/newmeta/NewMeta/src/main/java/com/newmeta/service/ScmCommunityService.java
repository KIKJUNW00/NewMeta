package com.newmeta.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.newmeta.domain.Admin;
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
    public ScmCommunity createPost(ScmCommunity scmCommunity, Admin currentUser) {
        scmCommunity.setAdmin(currentUser);  // Admin 정보 설정
        scmCommunity.setCreatedAt(LocalDateTime.now());
        scmCommunity.setUpdatedAt(LocalDateTime.now());
        ScmCommunity savedPost = scmCommunityRepo.save(scmCommunity);
        log.info("게시글 생성 완료: {}", savedPost);
        return savedPost;
    }
    
 // 전체 게시물 조회
    public List<ScmCommunity> getAllPosts() {
        List<ScmCommunity> posts = scmCommunityRepo.findAll();
        log.info("전체 게시물 조회 완료, 건수: {}", posts.size());
        return posts;
    }

    // 내 게시글 조회
    public List<ScmCommunity> getMyPosts(Admin currentUser) {
        List<ScmCommunity> posts = scmCommunityRepo.findByAdminUsername(currentUser.getUsername());
        log.info("현재 사용자의 게시글 조회 완료, 건수: {}", posts.size());
        return posts;
    }

    // 게시글 수정
    public ScmCommunity updatePost(Long id, ScmCommunity updatedPost, Admin currentUser) {
        return scmCommunityRepo.findById(id).map(existingPost -> {
            if (!existingPost.getAdmin().getUsername().equals(currentUser.getUsername())) {
                throw new RuntimeException("작성자만 수정할 수 있습니다.");
            }
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setUpdatedAt(LocalDateTime.now());
            ScmCommunity savedPost = scmCommunityRepo.save(existingPost);
            log.info("게시글 수정 완료: {}", savedPost);
            return savedPost;
        }).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 삭제
    public void deletePost(Long id, Admin currentUser) {
        ScmCommunity post = scmCommunityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!post.getAdmin().getUsername().equals(currentUser.getUsername())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }
        scmCommunityRepo.delete(post);
        log.info("게시글 삭제 완료, id: {}", id);
    }
}
