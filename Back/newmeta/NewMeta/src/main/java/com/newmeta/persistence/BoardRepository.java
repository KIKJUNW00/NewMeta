package com.newmeta.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
