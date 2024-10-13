package com.springcloud.demo.bookingreceipt.client.users;

import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms", url = "${client.users-ms.url}")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDTO findById(@PathVariable String id);
}
