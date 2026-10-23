package domain

import "time"

const EventTypeDriverLocationUpdated = "driver.location_updated"

type DriverLocationUpdatedEvent struct {
	EventID    string          `json:"eventId"`
	EventType  string          `json:"eventType"`
	OccurredAt time.Time       `json:"occurredAt"`
	ShipmentID string          `json:"shipmentId"`
	Payload    LocationPayload `json:"payload"`
}

type LocationPayload struct {
	DriverID   string    `json:"driverId"`
	Sequence   int64     `json:"sequence"`
	Lat        float64   `json:"lat"`
	Lng        float64   `json:"lng"`
	SpeedKmh   float64   `json:"speedKmh"`
	HeadingDeg float64   `json:"headingDeg"`
	RecordedAt time.Time `json:"recordedAt"`
}