package com.livetrack.shipment_tracker.infrastructure.web;

import com.livetrack.shipment_tracker.application.command.CreateShipmentCommand;
import com.livetrack.shipment_tracker.application.usecase.CreateShipmentUseCase;
import com.livetrack.shipment_tracker.application.usecase.GetShipmentUseCase;
import com.livetrack.shipment_tracker.domain.Coordinates;
import com.livetrack.shipment_tracker.domain.Shipment;
import com.livetrack.shipment_tracker.infrastructure.common.JSendResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final CreateShipmentUseCase createShipmentUseCase;
    private final GetShipmentUseCase getShipmentUseCase;

    public ShipmentController(CreateShipmentUseCase createShipmentUseCase, GetShipmentUseCase getShipmentUseCase) {
        this.createShipmentUseCase = createShipmentUseCase;
        this.getShipmentUseCase = getShipmentUseCase;
    }

    @PostMapping
    public ResponseEntity<JSendResponse<ShipmentResponse>> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateShipmentRequest request) {

        var command = new CreateShipmentCommand(
                new Coordinates(request.origin().lat(), request.origin().lng()),
                new Coordinates(request.destination().lat(), request.destination().lng()),
                request.customerId()
        );
        Shipment shipment = createShipmentUseCase.execute(command, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JSendResponse.success(ShipmentResponse.from(shipment)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JSendResponse<ShipmentResponse>> getById(@PathVariable UUID id) {
        Shipment shipment = getShipmentUseCase.execute(id);
        return ResponseEntity.ok(JSendResponse.success(ShipmentResponse.from(shipment)));
    }
}
