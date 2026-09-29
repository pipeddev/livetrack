package domain

import "time"

type ShipmentCreatedEvent struct {
	EventID string	`json:"eventId"`
	EventType string	`json:"eventType"`
	OccuredAt time.Time	`json:"occurredAt"`
	ShipmentID string	`json:"shipmentId"`
	Payload Payload	`json:"payload"`
}

type Payload struct {
	Origin Coordinates	`json:"origin"`
	Destination Coordinates	`json:"destination"`
	CustomerId string	`json:"customerId"`
}

type Coordinates struct {
	Latitude float64	`json:"latitude"`
	Longitude float64	`json:"longitude"`
}