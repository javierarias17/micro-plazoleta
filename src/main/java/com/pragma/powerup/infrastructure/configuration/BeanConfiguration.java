package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import com.pragma.powerup.domain.usecase.CreateRestaurantUseCase;
import com.pragma.powerup.infrastructure.out.http.adapter.UserValidationAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Value("${adapter.micro-users.url}")
    private String microUsersUrl;

    @Value("${adapter.micro-users.timeout}")
    private int microUsersTimeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(microUsersTimeout);
        factory.setReadTimeout(microUsersTimeout);
        return new RestTemplate(factory);
    }

    @Bean
    public IUserValidationPort userValidationPort() {
        return new UserValidationAdapter(restTemplate(), microUsersUrl);
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public ICreateRestaurantServicePort createRestaurantServicePort() {
        return new CreateRestaurantUseCase(restaurantPersistencePort(), userValidationPort());
    }
}
