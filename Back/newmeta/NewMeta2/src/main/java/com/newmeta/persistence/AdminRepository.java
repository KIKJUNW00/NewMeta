package com.newmeta.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {

}
