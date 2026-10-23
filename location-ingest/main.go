package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/pipeddev/location-ingest/application"
	"github.com/pipeddev/location-ingest/infrastructure"
	"github.com/twmb/franz-go/pkg/kgo"
)

func main() {
	// Se cancela con Ctrl+C o con SIGTERM (lo que envía Docker/K8s al detener).
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

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

	driverPublisher := infrastructure.NewKafkaEventPublisher(producerClient, "driver-events")
	locationPublisher := infrastructure.NewKafkaLocationPublisher(producerClient, "location-events")

	simulator := application.NewRouteSimulator(locationPublisher, 2*time.Second, 30) // ~1 min por viaje
	useCase := application.NewReserveDriverUseCase(driverPublisher, simulator, ctx)
	consumer := infrastructure.NewKafkaConsumer(consumerClient, useCase)

	log.Println("location-ingest escuchando shipment-events...")
	consumer.Start(ctx) // retorna cuando ctx se cancela

	log.Println("apagando: esperando simulaciones en curso...")
	simulator.Wait()
	log.Println("location-ingest detenido")
}