C4Container
title Container diagram for Cinemesh Movie Ticket Booking System

    Person(customer, "Customer", "A user who searches movies, books tickets, and pays.")
    Person(admin, "Admin", "A user who manages catalogs: movies, theaters, showtimes.")

    System_Boundary(cinemesh, "Cinemesh System") {
        Container(gateway, "API Gateway", "Spring Cloud Gateway", "Port: 9999. Routes requests, handles CORS, and propagates Request IDs.")
        
        Container(auth_service, "Auth Service", "Spring Boot", "Handles registration, login, JWT. DB: cinemesh_auth_db")
        Container(movie_service, "Movie Service", "Spring Boot", "Manages movie and genre catalogs. DB: cinemesh_movie_db")
        Container(theater_service, "Theater Service", "Spring Boot", "Manages rooms and showtime scheduling. DB: cinemesh_theater_db")
        Container(booking_service, "Booking Service", "Spring Boot", "Orchestrates reservations and seat locking. DB: cinemesh_booking_db")
        Container(payment_service, "Payment Service", "Spring Boot", "Integrates with VNPay for processing. DB: cinemesh_payment_db")
        Container(notification_service, "Notification Service", "Spring Boot", "Consumes events and sends emails via SMTP.")

        ContainerDb(kafka, "Message Broker", "Kafka / Confluent", "Handles async event propagation for SAGA choreography.")
        Container(debezium, "CDC (Debezium)", "Kafka Connect", "Monitors Outbox tables (_logs) to push events to Kafka.")
        ContainerDb(redis, "Distributed Cache/Lock", "Redis", "Distributed seat locking (600s TTL) in booking-service.")
        
        Container(config_server, "Config Server", "Spring Cloud Config", "Port: 8888. Centralized properties management.")
        Container(discovery_server, "Discovery Server", "Netflix Eureka", "Port: 8761. Service registration and lookup.")
    }

    System_Ext(vnpay, "VNPay Gateway", "External Payment Gateway")
    System_Ext(gmail, "Gmail SMTP", "External Mail Relay")

    Rel(customer, gateway, "HTTPS/JSON", "Browser/Postman")
    Rel(admin, gateway, "HTTPS/JSON", "Management Console")

    Rel(gateway, auth_service, "Forward Auth")
    Rel(gateway, movie_service, "Forward Catalog Query")
    Rel(gateway, theater_service, "Forward Showtime Query")
    Rel(gateway, booking_service, "Forward Booking Cmd")
    Rel(gateway, payment_service, "Forward Payment Cmd")

    Rel(booking_service, redis, "Seat Lock (TTL 600s)")
    Rel(booking_service, movie_service, "Feign: Fetch Movie Data")
    Rel(booking_service, theater_service, "Feign: Fetch Showtime Details")

    Rel(debezium, kafka, "Stream events from DB Outbox logs")
    Rel(kafka, booking_service, "Event: payment_success -> update status")
    Rel(kafka, payment_service, "Event: order_created -> init payment")
    Rel(kafka, notification_service, "Event: payment_success -> send email")

    Rel(payment_service, vnpay, "IPN/Webhook/Redirect", "HTTPS")
    Rel(notification_service, gmail, "Send Email", "SMTP/SSL")