package com.crc.farming.dao;

import com.crc.farming.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDao {
    List<Product> queryAllProduct();

    Product queryProductById(Integer product_id);

    boolean addProduct(Product product);

    boolean updateStock(Integer product_id,Integer buy_num);

    boolean deleteProduct(Integer product_id);

}
