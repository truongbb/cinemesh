sequenceDiagram
autonumber
actor Customer
participant Gateway as API Gateway (9999)
participant Booking as Booking Service
participant Redis as Redis (Seat Lock)
participant DB_Booking as Booking DB (order_logs)
participant Debezium as Kafka Connect (Debezium)
participant Kafka as Kafka Broker
participant Payment as Payment Service
participant VNPay as VNPay (Sandbox)
participant Notify as Notification Service

    Customer->>Gateway: POST /api/v1/bookings
    Gateway->>Booking: createBooking(payload)
    
    rect rgb(240, 240, 240)
        Note over Booking, Redis: [Hexagonal Application Layer]
        Booking->>Redis: Lock seats (600s TTL)
        Booking->>Booking: Calculate Total Price & Validate
        Booking->>DB_Booking: Save Order (Status: PENDING)
        Booking->>DB_Booking: Save OrderLogEntity (Outbox Log)
    end

    DB_Booking-->>Booking: Commit Transaction
    Booking-->>Gateway: 201 Created (Order ID)
    Gateway-->>Customer: Redirect to Payment Confirmation Page

    Note over DB_Booking, Debezium: Asynchronous Capture of DB Changes
    Debezium->>DB_Booking: Stream 'order_logs' table
    Debezium->>Kafka: Publish to 'cinemesh.booking-service.order-logs'

    Kafka->>Payment: Consume order_created event
    Payment->>Payment: Generate VNPay Payment URL
    Payment-->>Customer: User pay on VNPay Page

    Customer->>VNPay: Authorize Payment
    VNPay->>Payment: GET /api/v1/payments/vnpay-ipn (IPN Webhook)
    
    Payment->>Payment: Update Payment Status (SUCCESS)
    Payment->>Kafka: Publish to 'cinemesh.payment-service.payment-logs'

    par Finalize Order & Notify
        Kafka->>Booking: Consume payment_success
        Booking->>DB_Booking: Update Order Status (SUCCESS)
        Booking->>Redis: Confirm permanent seat occupancy
    and
        Kafka->>Notify: Consume payment_success
        Notify->>Customer: Send Confirmation Email (Gmail SMTP)
    end
