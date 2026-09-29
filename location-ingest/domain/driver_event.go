package domain

import "time"

type DriverReservedEvent struct {
	EventID 	string		`json:"eventId"`
	EventType 	string 		`json:"eventType"`
	OcurredAt 	time.Time	`json:"occurredAt"`
	ShipmentID 	string		`json:"shipmentId"`
	Payload    	struct {
		DriverID   string 	`json:"driverId"`
		EtaMinutes int    	`json:"etaMinutes"`
	} `json:"payload"`
}

type DriverReservationFailedEvent struct {
	EventID    string    `json:"eventId"`
	EventType  string    `json:"eventType"`
	OccurredAt time.Time `json:"occurredAt"`
	ShipmentID string    `json:"shipmentId"`
	Payload    struct {
		Reason string `json:"reason"`
	} `json:"payload"`
}