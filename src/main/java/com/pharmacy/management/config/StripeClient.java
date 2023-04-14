
package com.pharmacy.management.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacy.management.model.StripePaymentHistory;
import com.pharmacy.management.repository.StripePaymentHistoryRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeClient {
    private final StripePaymentHistoryRepository stripePaymentHistoryRepository;

    @Value("${STRIPE_SECRET_KEY}")
    String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    public Customer createCustomer(String token, String email) throws StripeException {
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", email);
        customerParams.put("source", token);
        return Customer.create(customerParams);
    }

    private Customer getCustomer(String id) throws Exception {
        return Customer.retrieve(id);
    }


    public StripePaymentHistory chargeNewCard(String token, double amount, Long orderId)
            throws StripeException {

        String currency = "USD";
        String source = token;
        int amountCent  = (int) amount * 100;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountCent);
        chargeParams.put("currency", currency);
        chargeParams.put("source", source);
        Charge charge = Charge.create(chargeParams);
        Account account = Account.retrieve();
        StripePaymentHistory stripePaymentHistory = new StripePaymentHistory();

        String accountId = account.getId();
        String status = charge.getStatus();
        Boolean isPaid = charge.getPaid();
        Double payAmount = amount;
        String email = account.getEmail();

        stripePaymentHistory.setAccountId(accountId);
        stripePaymentHistory.setStatus(status);
        stripePaymentHistory.setIsPaid(isPaid);
        stripePaymentHistory.setPayAmount(payAmount);
        stripePaymentHistory.setEmail(email);
        stripePaymentHistory.setSource(source);
        stripePaymentHistory.setCurrency(currency);
        stripePaymentHistory.setAddedDateTime(LocalDateTime.now());
        stripePaymentHistory.setOrderId(orderId);
        stripePaymentHistoryRepository.save(stripePaymentHistory);



//        stripePaymentHistory.setAccountId(charge.g);
        return stripePaymentHistory;
    }


//    public Charge chargeNewCard(String token, double amount) throws StripeException {
//        Map<String, Object> chargeParams = new HashMap<String, Object>();
//        chargeParams.put("amount", (int) (amount * 100));
//        chargeParams.put("currency", "USD");
//        chargeParams.put("source", token);
//        Charge charge = Charge.create(chargeParams);
//        return charge;
//    }

    public Charge chargeCustomerCard(String customerId, int amount) throws Exception {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "USD");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        Charge charge = Charge.create(chargeParams);
        return charge;
    }
}
