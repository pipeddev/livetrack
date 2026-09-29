package main

import (
	"context"
	"log"

	"github.com/pipeddev/location-ingest/application"
	"github.com/pipeddev/location-ingest/infrastructure"
	"github.com/twmb/franz-go/pkg/kgo"
)

func main() {
	seeds := []string{"localhost:19092"}

	consumerClient, err := kgo.NewClient(
		kgo.SeedBrokers(seeds...),
		kgo.ConsumeTopics("shipment-events"),
		kgo.ConsumerGroup("location-ingest-group"),
	)
	if err != nil {
		log.Fatalf("error creando consumer client: %v", err)
	}
	defer consumerClient.Close()

	producerClient, err := kgo.NewClient(kgo.SeedBrokers(seeds...))
	if err != nil {
		log.Fatalf("error creando producer client: %v", err)
	}
	defer producerClient.Close()

	publisher := infrastructure.NewKafkaEventPublisher(producerClient, "driver-events")
	useCase := application.NewReserveDriverUseCase(publisher)
	consumer := infrastructure.NewKafkaConsumer(consumerClient, useCase)

	log.Println("location-ingest escuchando shipment-events...")
	consumer.Start(context.Background())
}