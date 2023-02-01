package com.example.springboot.controller;

import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productList = productRepository.findAll();
        if(!productList.isEmpty()){
            for(ProductModel produtc : productList){
                UUID id = produtc.getIdProduct();
                produtc.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return new ResponseEntity<List<ProductModel>>(productList, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productO = productRepository.findById(id);
        if(productO.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product List"));
                return new ResponseEntity<ProductModel>(productO.get(), HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductModel>saveProduct(@RequestBody @Validated ProductModel product){
        return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?>deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productO = productRepository.findById(id);
            if (productO.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            productRepository.delete(productO.get());
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductModel>updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Validated ProductModel product){
        Optional<ProductModel> productO = productRepository.findById(id);
            if (productO.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            product.setIdProduct(productO.get().getIdProduct());
            return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.OK);
    }

}
