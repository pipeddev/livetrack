package application

import (
	"context"
	"log"
	"math/rand"
	"time"

	"github.com/google/uuid"
	"github.com/pipeddev/location-ingest/domain"
)

type ReserveDriverUseCase struct {
	publisher domain.EventPublisher
	simulator *RouteSimulator
	rootCtx   context.Context
}

func NewReserveDriverUseCase(p domain.EventPublisher, sim *RouteSimulator, rootCtx context.Context) *ReserveDriverUseCase {
	return &ReserveDriverUseCase{publisher: p, simulator: sim, rootCtx: rootCtx}
}

func (uc *ReserveDriverUseCase) Execute(shipmentCreated domain.ShipmentCreatedEvent) error {
	var hasAvailableDriver = hasAvailableDriver() 
	println("hasAvailableDriver", hasAvailableDriver)
	if !hasAvailableDriver {
		return uc.publishReservationFailed(shipmentCreated.ShipmentID)
	}

	driverID := uuid.NewString()

	event := domain.DriverReservedEvent{
		EventID:    uuid.NewString(),
		EventType:  "driver.reserved",
		OccurredAt: time.Now().UTC(),
		ShipmentID: shipmentCreated.ShipmentID,
	}
	event.Payload.DriverID = driverID
	event.Payload.EtaMinutes = rand.Intn(20) + 5

	if err := uc.publisher.PublishDriverReserved(event); err != nil {
		return err // si no se publicó la reserva, no simulamos el viaje
	}

	log.Printf("conductor %s asignado a shipment=%s", driverID, shipmentCreated.ShipmentID)
	uc.simulator.Start(uc.rootCtx, shipmentCreated.ShipmentID, driverID,
		shipmentCreated.Payload.Origin, shipmentCreated.Payload.Destination)
	return nil
}

func (uc *ReserveDriverUseCase) publishReservationFailed(shipmentID string) error {
	event := domain.DriverReservationFailedEvent{
		EventID:    uuid.NewString(),
		EventType:  "driver.reservation_failed",
		OccurredAt: time.Now().UTC(),
		ShipmentID: shipmentID,
	}
	event.Payload.Reason = "no_drivers_in_radius"
	log.Printf("sin conductor para shipment=%s", shipmentID)
	return uc.publisher.PublishDriverReservationFailed(event)
}

// hasAvailableDriver simula la búsqueda real de un conductor.
// 70% de éxito para poder probar ambas ramas del saga sin depender de datos reales.
func hasAvailableDriver() bool {
	return rand.Intn(100) < 70
}