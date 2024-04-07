package com.bus.services;

import com.bus.dao.TravelCostRepository;
import com.bus.entities.TravelCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoaderService implements CommandLineRunner {

	@Autowired
	private TravelCostRepository travelCostRepository;

	@Override
	public void run(String... args) {
		List<TravelCost> travelCosts = new ArrayList<>();
		List<String> cities = List.of("Pune", "Mumbai", "Delhi", "Bangalore", "Chennai");
		long id = 1;

		for (String origin : cities) {
			for (String destination : cities) {
				if (!origin.equals(destination)) {
					double cost = Math.random() * 5000 + 3000; // Random cost between 3000 and 8000
					travelCosts.add(new TravelCost(id++, origin, destination, cost));
				}
			}
		}

		travelCostRepository.saveAll(travelCosts);
	}
}