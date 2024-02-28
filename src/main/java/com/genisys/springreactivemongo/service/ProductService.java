package com.genisys.springreactivemongo.service;

import com.genisys.springreactivemongo.dto.ProductDto;
import com.genisys.springreactivemongo.entity.Product;
import com.genisys.springreactivemongo.repository.ProductRepository;
import com.genisys.springreactivemongo.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Flux<ProductDto> getProducts(){
        return repository.findAll().map(AppUtils::mapToProductDto);
    }

    public Mono<ProductDto> getProduct(String id){
        return repository.findById(id).map(AppUtils::mapToProductDto);
    }

    public Flux<ProductDto> getProductInRange(double min, double max){
        return repository.findByPriceBetween(Range.closed(min, max))
                .map(AppUtils::mapToProductDto);
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> productDtoMono){
        return  productDtoMono
                    .map(AppUtils::mapToProductEntity)
                    .flatMap(repository::save)
                    .map(AppUtils::mapToProductDto);
    }

    public Mono<ProductDto> updateProduct(Mono<ProductDto> productDtoMono, String id){
        return repository.findById(id)
                    .flatMap(p -> productDtoMono.map(AppUtils::mapToProductEntity))
                    .doOnNext(e -> e.setId(id))
                    .flatMap(repository::save)
                    .map(AppUtils::mapToProductDto);
    }


    public Mono<Void> deleteProduct(Publisher<String> id){
        return repository.deleteById(id);
    }
}
