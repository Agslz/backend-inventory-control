package com.company.inventory.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;

@Service
public interface IProductService {
	
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId);
	
	public ResponseEntity<ProductResponseRest> searchById(Long id);
	
	public ResponseEntity<ProductResponseRest> searchByName(String name);
	
	

	
	
}
