package com.newmeta.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
