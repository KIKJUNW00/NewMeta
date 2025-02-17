package com.newmeta.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.newmeta.domain.Admin;
import com.newmeta.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 관리자 등록
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin created = adminService.createAdmin(admin);
        return ResponseEntity.ok(created);
    }

    // 전체 관리자 조회
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    // 단일 관리자 조회
    @GetMapping("/{username}")
    public ResponseEntity<Admin> getAdminByUsername(@PathVariable String username) {
        Optional<Admin> adminOpt = adminService.getAdminByUsername(username);
        return adminOpt.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

//    // 관리자 수정
//    @PutMapping("/{username}")
//    public ResponseEntity<Admin> updateAdmin(@PathVariable String username, @RequestBody Admin admin) {
//        Admin updated = adminService.updateAdmin(username, admin);
//        return ResponseEntity.ok(updated);
//    }
    
    // 🔥 관리자 정보 업데이트 API
    @PutMapping("/{username}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String username, @RequestBody Admin admin) {
        Admin updated = adminService.updateAdmin(username, admin);
        return ResponseEntity.ok(updated);
    }

    // 🔥 프로필 사진 업로드 API (파일 저장 후 URL 반환)
    @PostMapping("/{username}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(@PathVariable String username, 
                                                                  @RequestParam("file") MultipartFile file) {
        String photoUrl = adminService.uploadProfilePhoto(username, file);

        // 사진 URL을 응답으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("photo", photoUrl);

        return ResponseEntity.ok(response);
    }



    // 관리자 삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String username) {
        adminService.deleteAdmin(username);
        return ResponseEntity.noContent().build();
    }
}
