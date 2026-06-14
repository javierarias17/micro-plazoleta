package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.api.ILinkEmployeeServicePort;
import com.pragma.powerup.domain.api.IListDishesServicePort;
import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.api.IListRestaurantsServicePort;
import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import com.pragma.powerup.domain.usecase.CreateDishUseCase;
import com.pragma.powerup.domain.usecase.CreateOrderUseCase;
import com.pragma.powerup.domain.usecase.CreateRestaurantUseCase;
import com.pragma.powerup.domain.usecase.LinkEmployeeUseCase;
import com.pragma.powerup.domain.usecase.ListDishesUseCase;
import com.pragma.powerup.domain.usecase.ListOrdersUseCase;
import com.pragma.powerup.domain.usecase.ListRestaurantsUseCase;
import com.pragma.powerup.domain.usecase.UpdateDishUseCase;
import com.pragma.powerup.infrastructure.out.http.adapter.UserValidationAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantEmployeeJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
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
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;
    private final IRestaurantEmployeeRepository restaurantEmployeeRepository;
    private final IRestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

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

    @Bean
    public IListRestaurantsServicePort listRestaurantsServicePort() {
        return new ListRestaurantsUseCase(restaurantPersistencePort());
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, categoryEntityMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public ICreateDishServicePort createDishServicePort() {
        return new CreateDishUseCase(dishPersistencePort(), restaurantPersistencePort(), categoryPersistencePort(), authenticatedUserPort);
    }

    @Bean
    public IUpdateDishServicePort updateDishServicePort() {
        return new UpdateDishUseCase(dishPersistencePort(), restaurantPersistencePort(), authenticatedUserPort);
    }

    @Bean
    public IListDishesServicePort listDishesServicePort() {
        return new ListDishesUseCase(dishPersistencePort(), restaurantPersistencePort());
    }

    @Bean
    public IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort() {
        return new RestaurantEmployeeJpaAdapter(restaurantEmployeeRepository, restaurantEmployeeEntityMapper);
    }

    @Bean
    public ILinkEmployeeServicePort linkEmployeeServicePort() {
        return new LinkEmployeeUseCase(restaurantPersistencePort(), restaurantEmployeePersistencePort(), userValidationPort(), authenticatedUserPort);
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public ICreateOrderServicePort createOrderServicePort() {
        return new CreateOrderUseCase(orderPersistencePort(), restaurantPersistencePort(), dishPersistencePort(), authenticatedUserPort);
    }

    @Bean
    public IListOrdersServicePort listOrdersServicePort() {
        return new ListOrdersUseCase(orderPersistencePort(), restaurantEmployeePersistencePort(), authenticatedUserPort);
    }
}
