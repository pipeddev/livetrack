package domain

type EventPublisher interface {
	PublishDriverReserved(event DriverReservedEvent) error
	PublishDriverReservationFailed(event DriverReservationFailedEvent) error
}