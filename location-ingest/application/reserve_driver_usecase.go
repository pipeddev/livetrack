package application

import (
	"math/rand"
	"time"

	"github.com/google/uuid"
	"github.com/pipeddev/location-ingest/domain"
)

type ReserveDriverUseCase struct {
	publisher domain.EventPublisher 
}

func NewReserveDriverUseCase(publisher domain.EventPublisher) *ReserveDriverUseCase {
	return &ReserveDriverUseCase{publisher: publisher}
}

func (uc *ReserveDriverUseCase) Execute(shipmentCreated domain.ShipmentCreatedEvent) error {
	hasAvailableDriver := hasAvailableDriver()
	println("hasAvailableDriver:", hasAvailableDriver)
	if hasAvailableDriver {
		event := domain.DriverReservedEvent{
			EventID:    uuid.NewString(),
			EventType:  "driver.reserved",
			ShipmentID: shipmentCreated.ShipmentID,
		}
		event.Payload.DriverID = uuid.NewString()
		event.Payload.EtaMinutes = rand.Intn(20) + 5
		return uc.publisher.PublishDriverReserved(event)
	}

	event := domain.DriverReservationFailedEvent{
		EventID:    uuid.NewString(),
		EventType:  "driver.reservation_failed",
		OccurredAt: time.Now().UTC(),
		ShipmentID: shipmentCreated.ShipmentID,
	}
	event.Payload.Reason = "no_drivers_in_radius"
	return uc.publisher.PublishDriverReservationFailed(event)
}

// hasAvailableDriver simula la búsqueda real de un conductor.
// 30% de éxito para poder probar ambas ramas del saga sin depender de datos reales.
func hasAvailableDriver() bool {
	return rand.Intn(100) < 50
}