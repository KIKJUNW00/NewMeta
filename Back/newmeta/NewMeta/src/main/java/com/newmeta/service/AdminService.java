package com.newmeta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.newmeta.domain.Admin;
import com.newmeta.persistence.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    // 관리자 등록
    public Admin createAdmin(Admin admin) {
        Admin savedAdmin = adminRepository.save(admin);
        log.info("관리자 등록 완료: {}", savedAdmin);
        return savedAdmin;
    }

    // 전체 관리자 조회
    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        log.info("전체 관리자 조회, 건수: {}", admins.size());
        return admins;
    }

    // 단일 관리자 조회
    public Optional<Admin> getAdminByUsername(String username) {
        Optional<Admin> admin = adminRepository.findById(username);
        admin.ifPresentOrElse(
            a -> log.info("관리자 조회 성공: {}", a),
            () -> log.warn("관리자 조회 실패, username: {}", username)
        );
        return admin;
    }

    // 관리자 수정
    public Admin updateAdmin(String username, Admin updatedAdmin) {
        return adminRepository.findById(username).map(admin -> {
            if (updatedAdmin.getPassword() != null) {
                admin.setPassword(updatedAdmin.getPassword());
            }
            if (updatedAdmin.getRole() != null) {
                admin.setRole(updatedAdmin.getRole());
            }
            if (updatedAdmin.getPhoto() != null) {
                admin.setPhoto(updatedAdmin.getPhoto());
            }
            Admin saved = adminRepository.save(admin);
            log.info("관리자 수정 완료: {}", saved);
            return saved;
        }).orElseThrow(() -> new RuntimeException("관리자 미존재: " + username));
    }

    // 관리자 삭제
    public void deleteAdmin(String username) {
        adminRepository.deleteById(username);
        log.info("관리자 삭제 완료, username: {}", username);
    }
}
