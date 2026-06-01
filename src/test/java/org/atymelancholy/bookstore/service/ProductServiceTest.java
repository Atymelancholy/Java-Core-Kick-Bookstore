package org.atymelancholy.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.dao.ProductDao;
import org.atymelancholy.bookstore.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ProductDao productDao;

    @Test
    void listPageDelegatesToDao() {
        when(daoFactory.products()).thenReturn(productDao);
        var sample = List.of(
                new Product(1, "A", "d", 100, 1),
                new Product(2, "B", "d", 200, 2));
        when(productDao.findPage(ProductService.PAGE_SIZE, 0)).thenReturn(sample);
        var svc = new ProductService(daoFactory);
        assertEquals(sample, svc.listPage(0));
    }

    @Test
    void productCountDelegatesToDao() {
        when(daoFactory.products()).thenReturn(productDao);
        when(productDao.countAll()).thenReturn(42L);
        var svc = new ProductService(daoFactory);
        assertEquals(42L, svc.productCount());
    }
}
