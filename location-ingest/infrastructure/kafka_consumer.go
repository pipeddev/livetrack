package infrastructure

import (
	"context"
	"encoding/json"
	"log"

	"github.com/pipeddev/location-ingest/application"
	"github.com/pipeddev/location-ingest/domain"
	"github.com/twmb/franz-go/pkg/kgo"
)

type KafkaConsumer struct {
	client  *kgo.Client
	useCase *application.ReserveDriverUseCase
}

func NewKafkaConsumer(client *kgo.Client, useCase *application.ReserveDriverUseCase) *KafkaConsumer {
	return &KafkaConsumer{client: client, useCase: useCase}
}

func (c *KafkaConsumer) Start(ctx context.Context) {
	for {
		fetches := c.client.PollFetches(ctx)
		if ctx.Err() != nil {
			return
		}
		fetches.EachError(func(_ string, _ int32, err error) {
			log.Printf("error de fetch: %v", err)
		})
		fetches.EachRecord(func(record *kgo.Record) {
			var event domain.ShipmentCreatedEvent
			if err := json.Unmarshal(record.Value, &event); err != nil {
				log.Printf("no se pudo deserializar el evento: %v", err)
				return
			}
			if event.EventType != "shipment.created" {
				return
			}
			if err := c.useCase.Execute(event); err != nil {
				log.Printf("error procesando shipment %s: %v", event.ShipmentID, err)
			}
		})
	}
}