package com.bus.dao;

import com.bus.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelCostRepository extends JpaRepository<TravelCost, Long> {
    TravelCost findByOriginCityIgnoreCaseAndDestinationCityIgnoreCase(String origin, String destination);
}
