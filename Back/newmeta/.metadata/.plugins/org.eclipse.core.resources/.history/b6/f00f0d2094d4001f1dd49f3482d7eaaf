package com.newmeta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newmeta.domain.Meta;
import com.newmeta.persistence.MetaRepository;


@Service
public class MetaService {
	
	@Autowired
	private MetaRepository metarepo;

	// 모든 게시글 조회
	public List<Meta> getAllMetas() {
		return metarepo.findAll();
	}

	// 게시글 저장
	public Meta saveMeta(Meta meta) {
		return metarepo.save(meta);
	}

	// 특정 게시글 조회
	public Meta getMetaById(Long seq) {
		return metarepo.findById(seq).orElseThrow(() -> new RuntimeException("조회할 일련번호를 찾을 수 없습니다."));
	}

	// 게시글 수정
	public Meta updateMeta(Long seq, Meta updatedMeta) {
		// 기존 게시글 찾기
		Meta meta = metarepo.findById(seq).orElseThrow(() -> new RuntimeException("수정할 일련번호를 찾을 수 없습니다."));

		// 수정할 내용 적용
		meta.setProductSerial(updatedMeta.getProductSerial() != null ? updatedMeta.getProductSerial() : meta.getProductSerial());
		meta.setEpcCode(updatedMeta.getEpcCode() != null ? updatedMeta.getEpcCode() : meta.getEpcCode());
		meta.setProductName(updatedMeta.getProductName() != null ? updatedMeta.getProductName() : meta.getProductName());
		meta.setHubType(updatedMeta.getHubType() != null ? updatedMeta.getHubType() : meta.getHubType());
		meta.setEventType(updatedMeta.getEventType() != null ? updatedMeta.getEventType() : meta.getEventType());

		
		// 저장 후 반환
		return metarepo.save(meta);
		}

	// 게시글 삭제
	public void deleteMeta(Long seq) {
		metarepo.deleteById(seq);
	}
	
	

}

