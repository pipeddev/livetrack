package application

import (
	"context"
	"log"
	"math/rand/v2"
	"sync"
	"time"

	"github.com/google/uuid"
	"github.com/pipeddev/location-ingest/domain"
)

type RouteSimulator struct {
	publisher domain.LocationPublisher
	tick      time.Duration
	steps     int
	active    sync.Map // shipmentID -> struct{}
	wg        sync.WaitGroup
}

func NewRouteSimulator(p domain.LocationPublisher, tick time.Duration, steps int) *RouteSimulator {
	return &RouteSimulator{publisher: p, tick: tick, steps: steps}
}

// Start lanza una simulación por envío. Es idempotente: si ya hay una
// corriendo para ese shipment (p.ej. el evento llegó duplicado), no hace nada.
func (s *RouteSimulator) Start(ctx context.Context, shipmentID, driverID string, from, to domain.Coordinates) {
	if _, running := s.active.LoadOrStore(shipmentID, struct{}{}); running {
		return
	}
	s.wg.Add(1)
	go func() {
		defer s.wg.Done()
		defer s.active.Delete(shipmentID)
		s.run(ctx, shipmentID, driverID, from, to)
	}()
}

// Wait bloquea hasta que terminen todas las simulaciones (graceful shutdown).
func (s *RouteSimulator) Wait() { s.wg.Wait() }

func (s *RouteSimulator) run(ctx context.Context, shipmentID, driverID string, from, to domain.Coordinates) {
	ticker := time.NewTicker(s.tick)
	defer ticker.Stop()

	heading := domain.BearingDeg(from, to)
	log.Printf("simulación iniciada shipment=%s distancia=%.1fkm", shipmentID, domain.HaversineKm(from, to))

	for seq := int64(1); seq <= int64(s.steps); seq++ {
		select {
		case <-ctx.Done():
			log.Printf("simulación interrumpida shipment=%s seq=%d", shipmentID, seq)
			return
		case <-ticker.C:
		}

		pos := domain.Interpolate(from, to, float64(seq)/float64(s.steps))
		now := time.Now().UTC()

		event := domain.DriverLocationUpdatedEvent{
			EventID:    uuid.NewString(),
			EventType:  domain.EventTypeDriverLocationUpdated,
			OccurredAt: now,
			ShipmentID: shipmentID,
			Payload: domain.LocationPayload{
				DriverID:   driverID,
				Sequence:   seq,
				Lat:        pos.Lat,
				Lng:        pos.Lng,
				SpeedKmh:   30 + rand.Float64()*20, // 30–50 km/h
				HeadingDeg: heading,
				RecordedAt: now,
			},
		}

		// Si falla un punto, lo perdemos y seguimos: el siguiente lo reemplaza.
		if err := s.publisher.PublishLocationUpdated(ctx, event); err != nil {
			log.Printf("error publicando ubicación shipment=%s seq=%d: %v", shipmentID, seq, err)
		}
	}
	log.Printf("simulación completada shipment=%s", shipmentID)
}