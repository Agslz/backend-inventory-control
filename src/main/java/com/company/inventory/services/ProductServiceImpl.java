package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServiceImpl implements IProductService {

	private ICategoryDao categoryDao;
	private IProductDao productDao;

	public ProductServiceImpl(ICategoryDao categoryDao, IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {

		ProductResponseRest response = new ProductResponseRest();

		List<Product> list = new ArrayList<>();

		try {

			// Search category to set in the product object

			Optional<Category> category = categoryDao.findById(categoryId);

			if (category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("Response Not Ok", "-1", "Category associated with the product not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

			// Saves the product

			Product productSaved = productDao.save(product);

			if (productSaved != null) {
				list.add(productSaved);
				response.getProduct().setProducts(list);
				response.setMetadata("Response OK", "00", "Product saved");
			} else {
				response.setMetadata("Response Not Ok", "-1", "Product not saved");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {

			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error saving product");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchById(Long id) {

		ProductResponseRest response = new ProductResponseRest();

		List<Product> list = new ArrayList<>();

		try {

			// Search product by id

			Optional<Product> product = productDao.findById(id);

			if (product.isPresent()) {

				byte[] imageDescompressed = Util.decompressZLib(product.get().getPicture());
				product.get().setPicture(imageDescompressed);
				list.add(product.get());
				response.getProduct().setProducts(list);
				response.setMetadata("Response Ok", "00", "Product found");

			} else {
				response.setMetadata("Response Not Ok", "-1", "Product not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error saving product");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchByName(String name) {

		ProductResponseRest response = new ProductResponseRest();

		List<Product> list = new ArrayList<>();

		List<Product> listAux = new ArrayList<>();

		try {

			// Search product by name

			listAux = productDao.findByNameContainingIgnoreCase(name);

			if (listAux.size() > 0) {

				listAux.stream().forEach((p) -> {

					byte[] imageDescompressed = Util.decompressZLib(p.getPicture());
					p.setPicture(imageDescompressed);
					list.add(p);

				});

				response.getProduct().setProducts(list);
				response.setMetadata("Response Ok", "00", "Products found");

			} else {
				response.setMetadata("Response Not Ok", "-1", "Products not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error searching product by name");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> deleteById(Long id) {

		ProductResponseRest response = new ProductResponseRest();

		try {

			// Delete product by ID
			productDao.deleteById(id);
			response.setMetadata("Response Ok", "00", "Product deleted");

		} catch (Exception e) {

			response.setMetadata("Respuesta Not Ok", "-1", "Error deleting product");
			e.getStackTrace();
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> search() {

		ProductResponseRest response = new ProductResponseRest();

		List<Product> list = new ArrayList<>();

		List<Product> listAux = new ArrayList<>();

		try {

			// Search all products

			listAux = (List<Product>) productDao.findAll();

			if (listAux.size() > 0) {

				listAux.stream().forEach((p) -> {

					byte[] imageDescompressed = Util.decompressZLib(p.getPicture());
					p.setPicture(imageDescompressed);
					list.add(p);

				});

				response.getProduct().setProducts(list);
				response.setMetadata("Response Ok", "00", "Products found");

			} else {
				response.setMetadata("Response Not Ok", "-1", "Products not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error searching product");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> update(Product product, Long categoryId, Long id) {

		ProductResponseRest response = new ProductResponseRest();

		List<Product> list = new ArrayList<>();

		try {

			// Search category to set in the product object

			Optional<Category> category = categoryDao.findById(categoryId);

			if (category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("Response Not Ok", "-1", "Category associated with the product not found");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

			// Search product to update

			Optional<Product> productSearch = productDao.findById(id);

			if (productSearch.isPresent()) {

				// We update the product
				productSearch.get().setAccount(product.getAccount());
				productSearch.get().setCategory(product.getCategory());
				productSearch.get().setName(product.getName());
				productSearch.get().setPicture(product.getPicture());
				productSearch.get().setPrice(product.getPrice());

				// Save the product in db
				Product productToUpdate = productDao.save(productSearch.get());

				if (productToUpdate != null) {
					list.add(productToUpdate);
					response.getProduct().setProducts(list);
					response.setMetadata("Response OK", "00", "Product updated");
				}

			} else {
				response.setMetadata("Response Not Ok", "-1", "Product not updated");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {

			e.getStackTrace();
			response.setMetadata("Response Not Ok", "-1", "Error updating product");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}

}
