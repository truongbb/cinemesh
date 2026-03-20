# VNPay Integration Guide (Sandbox Environment)

This guide systematizes the knowledge for integrating the VNPay Payment Gateway into a Spring Boot microservices project, specifically for local development and testing.

---

## 1. VNPay Sandbox Configuration
To test payments without real money, use the following Sandbox credentials provided by VNPay.

| Parameter | Value                                                                                     |
| :--- |:------------------------------------------------------------------------------------------|
| **Terminal ID (vnp_TmnCode)** | `<the Tmn code from email>`                                                               |
| **Secret Key (vnp_HashSecret)** | `<the hash secret from email>`                                                            |
| **Merchant Admin URL** | [https://sandbox.vnpayment.vn/merchantv2/](https://sandbox.vnpayment.vn/merchantv2/)      |
| **Payment SDK/Docs** | [VNPay API Documentation](https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html) |

### Test Card Information (NCB Bank) -> get from email
*   **Card Number:** `9704198526191432198`
*   **Account Name:** `NGUYEN VAN A`
*   **Issue Date:** `07/15`
*   **OTP:** `123456`

---

## 2. Core Architectural Concepts
VNPay uses two distinct callback mechanisms. Understanding the difference is critical for data integrity.

### A. vnp_ReturnUrl (Frontend Redirect)
*   **Flow:** Browser-based. After payment, VNPay redirects the **user** to this URL.
*   **Purpose:** Purely for UI/UX (e.g., showing a "Payment Successful" screen).
*   **Security:** **Unreliable.** Users can close their browser before the redirect happens, or malicious users can spoof the URL parameters.
*   **Action:** **NEVER** update your database or trigger business logic (like issuing tickets) here.

### B. IPN Webhook (Backend-to-Backend)
*   **Flow:** Server-to-Server. VNPay's backend makes a direct GET request to your backend.
*   **Purpose:** Finalizing the transaction.
*   **Security:** **Highly Secure.** Verified via checksums and direct IP-to-IP communication.
*   **Action:** This is where you update your database status to `PAID` and trigger downstream events (Kafka, Email).

---

## 3. Local Testing with Ngrok
Since VNPay's servers cannot "see" `localhost`, you must use a tunneling tool like **Ngrok** to expose your local backend to the public internet.

### Step 1: Install Ngrok
Using Homebrew on macOS:
```bash
brew install ngrok/ngrok/ngrok
```

### Step 2: Authenticate
Link your Ngrok account to avoid the "Security Warning" interstitial page, which breaks automated webhooks:
```bash
ngrok config add-authtoken <your_token_from_dashboard>
```

### Step 3: Start the Tunnel
Forward traffic to your Spring Boot port (default 8080):
```bash
ngrok http 8080
```
Copy the generated `Forwarding` URL (e.g., `https://a1b2-c3d4.ngrok-free.app`).

### Step 4: Configure VNPay Dashboard
1.  Log in to the [VNPay Sandbox Merchant Admin](https://sandbox.vnpayment.vn/vnpaygw-sit-testing/user/login).
2.  Navigate to **Configuration Management**.
3.  Set the **IPN URL** to your Ngrok public URL plus your API path:
    `https://your-subdomain.ngrok-free.app/api/v1/payments/vnpay-ipn`

---

## 4. End-to-End Testing Lifecycle
1.  **Start Infrastructure:** PostgreSQL, Kafka, and your Microservices.
2.  **Start Tunnel:** Ensure Ngrok is running and registered in VNPay Admin.
3.  **Generate Payment:** Call `POST /api/v1/payments/payment-requests` to get a `vnpayUrl`.
4.  **Complete Payment:** Open the URL, enter the NCB Test Card details, and confirm the OTP.
5.  **Verify IPN:** Observe your backend logs. You should see the IPN request hit your `PaymentWebhookController`, update the DB to `PAID`, and push a Kafka event to the `payment_logs` outbox.


## Reference
[1] https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html

[2] https://viblo.asia/p/tich-hop-cong-thanh-toan-vnpay-vao-du-an-laravel-trong-5-buoc-don-gian-pPLkNBeyJRZ

[3] https://zozo.vn/blog/huong-dan-tich-hop-cong-thanh-toan-vn-qr-vi-vnpay-vao-website-916