package com.newmeta.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.service.ProductEventLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/producteventLog")
@RequiredArgsConstructor
public class ProductEventLogController {

    private final ProductEventLogService productEventLogService;

//    // 페이지네이션 적용 - 제품명과 EPC 코드만 반환
//    @GetMapping("/paged")
//    public ResponseEntity<Page<ProductEventLogDTO>> getPagedProductNamesAndEpcCodes(
//            @RequestParam(defaultValue = "0") int page) { // 기본값으로 페이지 번호는 0
//        Page<ProductEventLogDTO> result = productEventLogService.getPagedProductNamesAndEpcCodes(page);
//        return ResponseEntity.ok(result);
//    }

    
 // 페이징 처리된 데이터를 반환
    @GetMapping("/paged")
    public ResponseEntity<Page<ProductEventLogDTO>> getPagedLogs(
            @RequestParam(defaultValue = "0") int page, // 기본 페이지 번호는 0
            @RequestParam(defaultValue = "30") int size // 기본 페이지 크기는 30
    ) {
        Pageable pageable = PageRequest.of(page, size); // 페이지와 크기 설정
        Page<ProductEventLogDTO> pagedLogs = productEventLogService.getPagedLogs(pageable);
        return ResponseEntity.ok(pagedLogs);
    }
    
    
    // 모든 로그 조회
    @GetMapping
    public ResponseEntity<List<ProductEventLog>> getAllLogs() {
        return ResponseEntity.ok(productEventLogService.findAllLogs());
    }

    // ID로 특정 로그 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductEventLog> getLogById(@PathVariable Long id) {
        return productEventLogService.findLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/epc/{epcCode}")
    public ResponseEntity<List<ProductEventLog>> getLogsByEpcCode(@PathVariable String epcCode) {
        List<ProductEventLog> logs = productEventLogService.findLogsByEpcCode(epcCode);
        if (logs.isEmpty()) {
            return ResponseEntity.notFound().build(); // 데이터가 없는 경우 404 반환
        }
        return ResponseEntity.ok(logs);
    }

    // 새로운 로그 생성
    @PostMapping
    public ResponseEntity<ProductEventLog> createLog(@RequestBody ProductEventLog productEventLog) {
        return ResponseEntity.ok(productEventLogService.saveLog(productEventLog));
    }

    // ID로 로그 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        productEventLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
