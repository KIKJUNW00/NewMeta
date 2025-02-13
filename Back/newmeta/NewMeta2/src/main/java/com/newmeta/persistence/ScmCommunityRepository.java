package com.newmeta.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newmeta.domain.ScmCommunity;


@Repository
public interface ScmCommunityRepository extends JpaRepository<ScmCommunity, Long> {
	@Query("SELECT s FROM ScmCommunity s WHERE s.admin.username = :username")
    List<ScmCommunity> findByAdminUsername(@Param("username") String username);

}
