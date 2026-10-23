package domain

import "context"

type EventPublisher interface {
	PublishDriverReserved(event DriverReservedEvent) error
	PublishDriverReservationFailed(event DriverReservationFailedEvent) error
}

type LocationPublisher interface {
	PublishLocationUpdated(ctx context.Context, event DriverLocationUpdatedEvent) error
}