package com.newmeta.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Hub;
import com.newmeta.domain.Product;

public interface HubRepository extends JpaRepository<Hub, String> {
	
	Optional<Hub> findByHubTypeAndLatitudeAndLongitude(String HubType, Double latitude, Double longitude);

	Optional<Product> findByHubType(String hubType);

	// 허브 이름으로 조회
//    Optional<Hub> findByHubName(String hubName);
    

}
