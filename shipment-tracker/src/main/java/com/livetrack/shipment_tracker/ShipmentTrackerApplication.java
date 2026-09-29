package com.livetrack.shipment_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShipmentTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentTrackerApplication.class, args);
	}

	/*@Bean
    CommandLineRunner testRun(CreateShipmentUseCase useCase) {
		return args -> {
			var command = new CreateShipmentCommand(
					new Coordinates(-33.45, -70.66),
					new Coordinates(-33.42, -70.61),
					UUID.randomUUID()
			);
			var shipment = useCase.execute(command);
			System.out.println("Shipment creado: " + shipment.getId());
		};
	}*/
}
