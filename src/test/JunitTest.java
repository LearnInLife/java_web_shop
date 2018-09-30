package test;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.shop.dao.ProductDao;
import com.shop.domain.Product;

public class JunitTest {
	@Test
	public void test(){
		System.out.println("test");
	}
	
	@Test
	public void testProductByPids() {
		ProductDao dao = new ProductDao();
		String[] split = {"1","10","12","3"};
		List<Product> historyProductList;
		try {
			historyProductList = dao.findProductByPids(split);
			for (Product product : historyProductList) {
				System.out.println(product);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
