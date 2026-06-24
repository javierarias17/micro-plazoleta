package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.api.ICancelOrderServicePort;
import com.pragma.powerup.domain.api.IDeliverOrderServicePort;
import com.pragma.powerup.domain.api.INotifyOrderReadyServicePort;
import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.api.IListDishesServicePort;
import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.api.IListRestaurantsServicePort;
import com.pragma.powerup.domain.api.IToggleDishStatusServicePort;
import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import com.pragma.powerup.domain.api.IValidateRestaurantOwnerServicePort;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.INotifyClientPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.domain.usecase.AssignOrderUseCase;
import com.pragma.powerup.domain.usecase.CancelOrderUseCase;
import com.pragma.powerup.domain.usecase.DeliverOrderUseCase;
import com.pragma.powerup.domain.usecase.NotifyOrderReadyUseCase;
import com.pragma.powerup.domain.usecase.CreateDishUseCase;
import com.pragma.powerup.domain.usecase.CreateOrderUseCase;
import com.pragma.powerup.domain.usecase.CreateRestaurantUseCase;
import com.pragma.powerup.domain.usecase.ListDishesUseCase;
import com.pragma.powerup.domain.usecase.ListOrdersUseCase;
import com.pragma.powerup.domain.usecase.ListRestaurantsUseCase;
import com.pragma.powerup.domain.usecase.ToggleDishStatusUseCase;
import com.pragma.powerup.domain.usecase.UpdateDishUseCase;
import com.pragma.powerup.domain.usecase.ValidateRestaurantOwnerUseCase;
import com.pragma.powerup.infrastructure.out.http.adapter.MessagingAdapter;
import com.pragma.powerup.infrastructure.out.http.adapter.TraceabilityAdapter;
import com.pragma.powerup.infrastructure.out.http.adapter.UserServiceAdapter;
import com.pragma.powerup.infrastructure.out.http.client.IMessagingFeignClient;
import com.pragma.powerup.infrastructure.out.http.client.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.http.client.IUserServiceFeignClient;
import com.pragma.powerup.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IUserServiceFeignClient userServiceFeignClient;
    private final IMessagingFeignClient messagingFeignClient;
    private final ITraceabilityFeignClient traceabilityFeignClient;

    @Bean
    public IUserServicePort userServicePort() {
        return new UserServiceAdapter(userServiceFeignClient);
    }

    @Bean
    public INotifyClientPort notifyClientPort() {
        return new MessagingAdapter(messagingFeignClient);
    }

    @Bean
    public ITraceabilityPort traceabilityPort() {
        return new TraceabilityAdapter(traceabilityFeignClient);
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public ICreateRestaurantServicePort createRestaurantServicePort() {
        return new CreateRestaurantUseCase(restaurantPersistencePort(), userServicePort());
    }

    @Bean
    public IValidateRestaurantOwnerServicePort validateRestaurantOwnerServicePort() {
        return new ValidateRestaurantOwnerUseCase(restaurantPersistencePort(), authenticatedUserPort);
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
        return new CreateDishUseCase(dishPersistencePort(), restaurantPersistencePort(), categoryPersistencePort(),
                authenticatedUserPort);
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
    public IToggleDishStatusServicePort toggleDishStatusServicePorts() {
        return new ToggleDishStatusUseCase(dishPersistencePort(), restaurantPersistencePort(), authenticatedUserPort);
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public ICreateOrderServicePort createOrderServicePort() {
        return new CreateOrderUseCase(orderPersistencePort(), restaurantPersistencePort(), dishPersistencePort(),
                authenticatedUserPort, traceabilityPort());
    }

    @Bean
    public IListOrdersServicePort listOrdersServicePort() {
        return new ListOrdersUseCase(orderPersistencePort(), userServicePort(), authenticatedUserPort);
    }

    @Bean
    public IAssignOrderServicePort assignOrderServicePort() {
        return new AssignOrderUseCase(orderPersistencePort(), userServicePort(), authenticatedUserPort,
                traceabilityPort());
    }

    @Bean
    public INotifyOrderReadyServicePort notifyOrderReadyServicePort() {
        return new NotifyOrderReadyUseCase(orderPersistencePort(), userServicePort(),
                authenticatedUserPort, notifyClientPort(), traceabilityPort());
    }

    @Bean
    public IDeliverOrderServicePort deliverOrderServicePort() {
        return new DeliverOrderUseCase(orderPersistencePort(), userServicePort(), authenticatedUserPort,
                traceabilityPort());
    }

    @Bean
    public ICancelOrderServicePort cancelOrderServicePort() {
        return new CancelOrderUseCase(orderPersistencePort(), authenticatedUserPort, traceabilityPort());
    }
}
