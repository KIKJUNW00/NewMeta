package com.newmeta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newmeta.domain.Board;
import com.newmeta.persistence.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardrepo;

	// 모든 게시글 조회
	public List<Board> getAllboards() {
		return boardrepo.findAll();
	}

	// 게시글 저장
	public Board saveboard(Board board) {
		return boardrepo.save(board);
	}

	// 특정 게시글 조회
	public Board getboardById(Long seq) {
		return boardrepo.findById(seq).orElseThrow(() -> new RuntimeException("Board not found"));
	}

	// 게시글 수정
	public Board updateboard(Long seq, Board updatedBoard) {
		// 기존 게시글 찾기
		Board board = boardrepo.findById(seq).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

		// 수정할 내용 적용
		if (updatedBoard.getName() != null) {
	        board.setName(updatedBoard.getName());
	    }

		// 저장 후 반환
		return boardrepo.save(board);
	}

	// 게시글 삭제
	public void deleteboard(Long seq) {
		boardrepo.deleteById(seq);
	}
}
