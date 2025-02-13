package com.newmeta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.ScmCommunity;
import com.newmeta.service.ScmCommunityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class ScmCommunityController {
	
	 private final ScmCommunityService scmCommunityService;

	    /**
	     * 게시글 생성 API
	     * POST /community/posts
	     */
	    @PostMapping("/posts")
	    public ResponseEntity<ScmCommunity> createPost(@RequestBody ScmCommunity scmCommunity) {
	    	ScmCommunity created = scmCommunityService.createPost(scmCommunity);
	        return ResponseEntity.ok(created);
	    }
	    
	    @GetMapping("/my-posts")
	    public List<ScmCommunity> getMyPosts() {
	        return scmCommunityService.getMyPosts();  // 현재 사용자 게시글 반환
	    }

	    /**
	     * 전체 게시글 조회 API
	     * GET /community/posts
	     */
	    @GetMapping("/posts")
	    public ResponseEntity<List<ScmCommunity>> getAllPosts() {
	        List<ScmCommunity> posts = scmCommunityService.getAllPosts();
	        return ResponseEntity.ok(posts);
	    }

	    /**
	     * 게시글 단건 조회 API
	     * GET /community/posts/{id}
	     */
	    @GetMapping("/posts/{id}")
	    public ResponseEntity<ScmCommunity> getPostById(@PathVariable Long id) {
	        Optional<ScmCommunity> postOpt = scmCommunityService.getPostById(id);
	        return postOpt.map(ResponseEntity::ok)
	                      .orElse(ResponseEntity.notFound().build());
	    }

	    /**
	     * 게시글 수정 API
	     * PUT /community/posts/{id}
	     */
	    @PutMapping("/posts/{id}")
	    public ResponseEntity<ScmCommunity> updatePost(@PathVariable Long id, @RequestBody ScmCommunity updatedPost) {
	    	ScmCommunity updated = scmCommunityService.updatePost(id, updatedPost);
	        if (updated == null) {
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.ok(updated);
	    }

	    /**
	     * 게시글 삭제 API
	     * DELETE /community/posts/{id}
	     */
	    @DeleteMapping("/posts/{id}")
	    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
	    	scmCommunityService.deletePost(id);
	        return ResponseEntity.noContent().build();
	    }

}
