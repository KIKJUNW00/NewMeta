package com.newmeta.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.Meta;
import com.newmeta.service.MetaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
public class MetaController {

    @Autowired
    private MetaService metaService;

    // 모든 게시글 조회 (GET /Meta)
    @GetMapping
    public List<Meta> getAllMetas() {
    	System.out.println("모든 일련번호 조회");
        return metaService.getAllMetas();
    }

    // 특정 게시글 조회 (GET /Meta/{id})
    @GetMapping("/{seq}")
    public Meta getMetaById(@PathVariable Long seq) {
    	System.out.println(seq + "조회 성공");
    	return metaService.getMetaById(seq);
    }

    // 게시글 생성 (POST /Meta)
    @PostMapping
    public Meta saveMeta(@RequestBody Meta meta) {
    	System.out.println(meta + "ETC 생성");
        return metaService.saveMeta(meta);
    }

    
 // 게시글 수정 (PUT /Meta/{seq})
    @PutMapping("/{seq}")
    public Meta updateMeta(@PathVariable Long seq, @RequestBody Meta updatedMeta) {
        System.out.println(seq + " 일련번호 수정");
        return metaService.updateMeta(seq, updatedMeta);
    }

    // 게시글 삭제 (DELETE /Meta/{seq})
    @DeleteMapping("/{seq}")
    public String deleteMeta(@PathVariable Long seq) {
    	metaService.deleteMeta(seq);
        return seq + "번 일련번호 삭제";
    }
}