package com.newmeta.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Hub;

public interface HubRepository extends JpaRepository<Hub, Long> {

}
