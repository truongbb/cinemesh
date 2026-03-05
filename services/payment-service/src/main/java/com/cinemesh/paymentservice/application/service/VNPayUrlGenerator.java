package com.cinemesh.paymentservice.application.service;

import com.cinemesh.common.utils.DataUtils;
import com.cinemesh.paymentservice.statics.PaymentCurrency;
import com.cinemesh.paymentservice.statics.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayUrlGenerator {

    @Value("${application.vn-pay.tmn-code}")
    private String vnpTmnCode;

    @Value("${application.vn-pay.hash-secret}")
    private String vnpHashSecret;

    @Value("${application.vn-pay.pay-url}")
    private String vnpPayUrl;

    @Value("${application.vn-pay.return-url}")
    private String vnpReturnUrl;

    /**
     * Generates the secure VNPay checkout URL.
     */
    public String generatePaymentUrl(UUID paymentId, BigDecimal amount, String ipAddress, PaymentMethod paymentMethod) {

        // VNPay requires amount in basic units (multiplied by 100)
        long vnpAmount = amount.longValue() * 100;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", PaymentCurrency.VND.name());

        // Map your frontend payment method to VNPay's bank code
        if (PaymentMethod.VNPAY_QR.equals(paymentMethod)) {
            vnpParams.put("vnp_BankCode", PaymentMethod.VNPAY_QR.getVnPayValue());
        } else if (PaymentMethod.VNPAY_ATM.equals(paymentMethod)) {
            vnpParams.put("vnp_BankCode", PaymentMethod.VNPAY_ATM.getVnPayValue());
        }

        vnpParams.put("vnp_TxnRef", paymentId.toString());
        vnpParams.put("vnp_OrderInfo", "Thanh toan ve xem phim Cinemesh: " + paymentId);
        vnpParams.put("vnp_OrderType", "other"); // Or "190000" for entertainment/movies
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", ipAddress);

        // Date processing exactly as VNPay requires (GMT+7)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        // Set Expiration Time (e.g., 10 minutes to match your Redis lock)
        cld.add(Calendar.MINUTE, 10);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Note: I removed the Billing/Invoice fields from the VNPay docs
        // because they are optional and usually unnecessary for simple B2C movie tickets.

        // Sort parameters alphabetically
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnpSecureHash = DataUtils.hmacSHA512(vnpHashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        return vnpPayUrl + "?" + queryUrl;
    }

}
