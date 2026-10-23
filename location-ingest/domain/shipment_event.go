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
	Lat float64 `json:"lat"`
	Lng float64 `json:"lng"`
}