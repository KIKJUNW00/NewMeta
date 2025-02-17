package com.newmeta.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
//    private final String UPLOAD_DIR = "uploads/"; // 이미지 저장 폴더
//    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

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

//    // 관리자 수정
//    public Admin updateAdmin(String username, Admin updatedAdmin) {
//        return adminRepository.findById(username).map(admin -> {
//            if (updatedAdmin.getPassword() != null) {
//                admin.setPassword(updatedAdmin.getPassword());
//            }
//            if (updatedAdmin.getRole() != null) {
//                admin.setRole(updatedAdmin.getRole());
//            }
//            if (updatedAdmin.getPhoto() != null) {
//                admin.setPhoto(updatedAdmin.getPhoto());
//            }
//            Admin saved = adminRepository.save(admin);
//            log.info("관리자 수정 완료: {}", saved);
//            return saved;
//        }).orElseThrow(() -> new RuntimeException("관리자 미존재: " + username));
//    }

    // 관리자 삭제
    public void deleteAdmin(String username) {
        adminRepository.deleteById(username);
        log.info("관리자 삭제 완료, username: {}", username);
    }
    
    
 // 🔥 관리자 정보 수정 (이미지 URL 저장)
    public Admin updateAdmin(String username, Admin updatedAdmin) {
        return adminRepository.findById(username).map(admin -> {
            if (updatedAdmin.getPassword() != null) {
                admin.setPassword(updatedAdmin.getPassword());
            }
            if (updatedAdmin.getRole() != null) {
                admin.setRole(updatedAdmin.getRole());
            }
            if (updatedAdmin.getPhoto() != null) {
                admin.setPhoto(updatedAdmin.getPhoto()); // 절대 경로로 저장
            }
            Admin saved = adminRepository.save(admin);
            log.info("관리자 수정 완료: {}", saved);
            return saved;
        }).orElseThrow(() -> new RuntimeException("관리자 미존재: " + username));
    }


    // 🔥 관리자 프로필 사진 업로드 (파일 저장 후 URL 반환)
    public String uploadProfilePhoto(String username, MultipartFile file) {
        try {
            // 디렉토리 생성
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // 파일 저장
            String fileName = username + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());

            // **절대 경로 반환**
            String fileUrl = "/uploads/" + fileName;

            log.info("파일 저장 완료: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }


}
