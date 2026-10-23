package infrastructure

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/pipeddev/location-ingest/domain"
	"github.com/twmb/franz-go/pkg/kgo"
)

type KafkaLocationPublisher struct {
	client *kgo.Client
	topic  string
}

func NewKafkaLocationPublisher(client *kgo.Client, topic string) *KafkaLocationPublisher {
	return &KafkaLocationPublisher{client: client, topic: topic}
}

func (p *KafkaLocationPublisher) PublishLocationUpdated(ctx context.Context, e domain.DriverLocationUpdatedEvent) error {
	value, err := json.Marshal(e)
	println("Publishing location updated event:", string(value))
	println("Publishing location updated event to topic:", p.topic)
	println("err", err)
	if err != nil {
		return fmt.Errorf("serializando evento: %w", err)
	}
	record := &kgo.Record{
		Topic: p.topic,
		Key:   []byte(e.ShipmentID), // misma key → misma partición → orden garantizado
		Value: value,
	}
	return p.client.ProduceSync(ctx, record).FirstErr()
}