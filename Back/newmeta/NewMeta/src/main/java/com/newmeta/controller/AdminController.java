package com.newmeta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 관리자 수정
    @PutMapping("/{username}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String username, @RequestBody Admin admin) {
        Admin updated = adminService.updateAdmin(username, admin);
        return ResponseEntity.ok(updated);
    }

    // 관리자 삭제
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String username) {
        adminService.deleteAdmin(username);
        return ResponseEntity.noContent().build();
    }
}
