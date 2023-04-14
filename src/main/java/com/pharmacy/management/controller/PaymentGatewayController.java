package com.pharmacy.management.controller;

import com.pharmacy.management.config.StripeClient;
import com.pharmacy.management.model.StripePaymentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentGatewayController {

    private StripeClient stripeClient;
    @Autowired
    PaymentGatewayController(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }
    @PostMapping("/charge")
    public StripePaymentHistory chargeCard(@RequestParam(value="token") String token, @RequestParam(value="amount") Double amount, @RequestParam(value="orderId", required = false) Long orderId) throws Exception {
//        StripePaymentHistory stripePaymentHistory = this.stripeClient.chargeNewCard(token, amount, orderId);

        System.out.println("Token----------" + token);

        StripePaymentHistory stripePaymentHistory = new StripePaymentHistory();

        return stripePaymentHistory;
    }

}
