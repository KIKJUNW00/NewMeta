//package com.newmeta;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.newmeta.domain.Product;
//import com.newmeta.persistence.ProductRepository;
//
//@SpringBootTest
//public class ProductDummyDataTest {
//
//    @Autowired
//    ProductRepository productRepository;
//
//    @Test
//    public void testLoadDummyData() {
//        List<Product> dummyProducts = List.of(
//            Product.builder().epcId("001.8801234.567890.123456789").productSerial((long) 1).productName("대한민국").build(),
//            Product.builder().epcId("002.0001234.567890.123456789").productSerial((long) 2).productName("미국,캐나다").build(),
//            Product.builder().epcId("003.4501234.567890.123456789").productSerial((long) 3).productName("일본").build(),
//            Product.builder().epcId("004.3001234.567890.123456789").productSerial((long) 4).productName("프랑스").build(),
//            Product.builder().epcId("005.4601234.567890.123456789").productSerial((long) 5).productName("러시아").build(),
//            Product.builder().epcId("006.5001234.567890.123456789").productSerial((long) 6).productName("영국").build(),
//            Product.builder().epcId("007.6901234.567890.123456789").productSerial((long) 7).productName("중국").build(),
//            Product.builder().epcId("008.9301234.567890.123456789").productSerial((long) 8).productName("오스트레일리아").build()
//        );
//
//        productRepository.saveAll(dummyProducts);
//    }
//}
//
