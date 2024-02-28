package com.genisys.springreactivemongo;

import com.genisys.springreactivemongo.dto.ProductDto;
import com.genisys.springreactivemongo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest
class SpringReactiveMongoApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductService productService;

    @Test
    void addProductTest() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 1000));
        when(productService.saveProduct(productDtoMono)).thenReturn(productDtoMono);

        webTestClient.post().uri("/products")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void getProductsTest(){
        Flux<ProductDto> productDtoFlux = Flux.just(
                new ProductDto("102", "mobile", 1, 1000),
                new ProductDto("103", "TV", 1, 50000)
        );

        when(productService.getProducts()).thenReturn(productDtoFlux);

         Flux<ProductDto> listResult =  webTestClient.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(listResult)
                .expectSubscription()
                .expectNext( new ProductDto("102", "mobile", 1, 1000))
                .expectNext( new ProductDto("103", "TV", 1, 50000))
                .verifyComplete();
    }

    public void getProduct(){
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 1000));

        when(productService.getProduct(any())).thenReturn(productDtoMono);

         Flux<ProductDto> resProductDto = webTestClient.get().uri("/products/102")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

         StepVerifier.create(resProductDto)
                 .expectSubscription()
                 .expectNextMatches(p -> p.name().equals("mobile"))
                 .verifyComplete();
    }

    @Test
    public void deleteProductTest(){
        given(productService.deleteProduct(any())).willReturn(Mono.empty());

        webTestClient.delete().uri("/products/120")
                .exchange()
                .expectStatus().isOk();
    }

}
