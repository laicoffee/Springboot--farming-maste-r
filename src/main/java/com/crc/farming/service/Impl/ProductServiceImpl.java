package com.crc.farming.service.Impl;

import com.crc.farming.dao.ProductDao;
import com.crc.farming.pojo.Product;
import com.crc.farming.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired(required =false)
    ProductDao productDao;

    @Override
    public List<Product> queryAllProduct() {
        return productDao.queryAllProduct();
    }

    @Override
    public Product queryProductById(Integer product_id) {
        return productDao.queryProductById(product_id);
    }

    @Override
    public boolean addProduct(Product product) {
        return productDao.addProduct(product);
    }

    @Override
    public boolean updateStock(Integer product_id, Integer buy_num) {
        return productDao.updateStock(product_id,buy_num);
    }

    @Override
    public boolean deleteProduct(Integer product) {
        return productDao.deleteProduct(product);
    }
}
