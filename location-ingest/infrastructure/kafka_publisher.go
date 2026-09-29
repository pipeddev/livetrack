package infrastructure

import (
	"context"
	"encoding/json"

	"github.com/pipeddev/location-ingest/domain"
	"github.com/twmb/franz-go/pkg/kgo"
)

type KafkaEventPublisher struct {
	client *kgo.Client
	topic  string
}

func NewKafkaEventPublisher(client *kgo.Client, topic string) *KafkaEventPublisher {
	return &KafkaEventPublisher{client: client, topic: topic}
}

func (p *KafkaEventPublisher) PublishDriverReserved(event domain.DriverReservedEvent) error {
	return p.publish(event.ShipmentID, event)
}

func (p *KafkaEventPublisher) PublishDriverReservationFailed(event domain.DriverReservationFailedEvent) error {
	return p.publish(event.ShipmentID, event)
}

func (p *KafkaEventPublisher) publish(key string, value any) error {
	data, err := json.Marshal(value)
	if err != nil {
		return err
	}
	record := &kgo.Record{Topic: p.topic, Key: []byte(key), Value: data}
	res := p.client.ProduceSync(context.Background(), record)
	return res.FirstErr()
}