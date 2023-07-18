package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;

@Service
public class ProductoServiceImpl implements IProductService{
	
	private ICategoryDao categoryDao;
	private IProductDao productDao;

	public ProductoServiceImpl(ICategoryDao categoryDao,IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}



	@Override
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
		
		ProductResponseRest response = new ProductResponseRest();
		
		List<Product> list = new ArrayList<>();
		
		try {
			
			//Search category to set in the product object
			
			Optional<Category> category = categoryDao.findById(categoryId);
			
			if(category.isPresent()) {
				product.setCategory(category.get());
			}else {
				response.setMetadata("Response Not Ok", "-1", "Category associated with the product not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			
			//Saves the product
			
			Product productSaved = productDao.save(product);
			
			if(productSaved != null) {
				list.add(productSaved);
				response.getProduct().setProduct(list);
				response.setMetadata("Response OK", "00", "Product saved");
			}else {
				response.setMetadata("Response Not Ok", "-1", "Product not saved");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
			}
			
		}catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error saving product");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}
	

}
