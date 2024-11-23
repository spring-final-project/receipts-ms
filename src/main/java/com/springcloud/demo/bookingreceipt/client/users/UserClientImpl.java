package com.springcloud.demo.bookingreceipt.client.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientImpl implements UserClient {

    private final UserClient userClient;

    @Override
    @CircuitBreaker(name = "users-service", fallbackMethod = "findUserByIdFallback")
    public UserDTO findById(String id) {
        return userClient.findById(id);
    }

    UserDTO findUserByIdFallback(String id, Throwable e) throws Exception {
        if(!(e instanceof FeignException.FeignClientException feignClientException)){
            log.error("Users service not available. Try later");
        } else {

            Map body = new ObjectMapper().readValue(feignClientException.contentUTF8(), Map.class);

            if(feignClientException.status() == 404) {
                log.error((String) body.get("message"));
            }
        }

        log.error(e.getMessage());
        throw new Exception(e);
    }
}
