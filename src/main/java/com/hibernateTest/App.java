package com.hibernateTest;

import com.hibernateTest.stock.Category;
import com.hibernateTest.stock.Stock;
import com.hibernateTest.stock.StockCategory;
import com.hibernateTest.util.HibernateUtil;
import org.hibernate.classic.Session;

import java.util.*;

public class App {

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        //Iterate map from result
       /* Map<Stock, List<StockCategory>> map = getStockCategories(session);
        for (Map.Entry<Stock, List<StockCategory>> stockListEntry : map.entrySet()) {
            System.out.println("Stock:" + stockListEntry.getKey());
            for (StockCategory stockCategory : stockListEntry.getValue()) {
                System.out.print("StockCategory: " + stockCategory.getCreatedBy() + " ");
            }
            System.out.println();
        }*/

        //Join fetch
        List<Stock> allStocks = getAllStocks(session);
        for (Stock stock : allStocks) {
            System.out.println("Stock:" + stock);
            for (StockCategory stockCategory : stock.getStockCategories()) {
                System.out.print("StockCategory: " + stockCategory.getCreatedBy() + " ");
            }
            System.out.println();
        }

        session.getTransaction().commit();

        //Fill map from result
       /* List<Object[]> rows = query.list();
        Map<Date, Integer> statsPerDate = new HashMap<Date, Integer>(rows.size());
        for (Object[] row : rows) {
            Date date = (Date) row[0];
            Integer stat = (Integer) row[1];
            statsPerDate.put(date, stat);
        }*/
    }


    private static Map<Stock, List<StockCategory>> getStockCategories(Session session) {
        final String HQL = "from StockCategory";
//        final String HQL = "SELECT DISTINCT s FROM StockCategory s JOIN FETCH s.pk.stock JOIN FETCH s.pk.category";


        HashMap<Stock, List<StockCategory>> stockListHashMap = new HashMap<Stock, List<StockCategory>>();
        List<StockCategory> list = (List<StockCategory>) session.createQuery(HQL).list();


        for (StockCategory stockCategory : list) {
            Stock stock = stockCategory.getStock();
            List<StockCategory> stockCategories = stockListHashMap.get(stock);
            if (stockCategories != null) {
                stockCategories.add(stockCategory);
            } else {
                stockCategories = new ArrayList<StockCategory>();
                stockCategories.add(stockCategory);
                stockListHashMap.put(stock, stockCategories);
            }
        }

        return stockListHashMap;
    }

    private static List<Stock> getAllStocks(Session session) {
//      final String HQL = "FROM Stock ";
        final String HQL = "SELECT DISTINCT s FROM Stock s join fetch s.stockCategories";
        return (List<Stock>) session.createQuery(HQL).list();
    }

    public static void main2(String[] args) {
        System.out.println("Hibernate many to many - join table + extra column (Annotation)");
		Session session = HibernateUtil.getSessionFactory().openSession();

		session.beginTransaction();

		Stock stock = new Stock();
        stock.setStockCode("7052");
        stock.setStockName("PADINI");
        Stock stock2 = new Stock();
        stock2.setStockCode("70521");
        stock2.setStockName("PADINI1");

        Category category1 = new Category("CONSUMER1", "CONSUMER COMPANY1");
        Category category2 = new Category("CONSUMER2", "CONSUMER COMPANY2");
        Category category3 = new Category("CONSUMER3", "CONSUMER COMPANY3");
        Category category4 = new Category("CONSUMER4", "CONSUMER COMPANY4");
        //new category, need save to get the id first
        session.save(category1);
        session.save(category2);
        session.save(category3);
        session.save(category4);

        StockCategory stockCategory1 = new StockCategory();
        stockCategory1.setStock(stock);
        stockCategory1.setCategory(category2);
        stockCategory1.setCreatedDate(new Date());
        stockCategory1.setCreatedBy("system2");

        StockCategory stockCategory2 = new StockCategory();
        stockCategory2.setStock(stock);
        stockCategory2.setCategory(category3);
        stockCategory2.setCreatedDate(new Date());
        stockCategory2.setCreatedBy("system3");

        StockCategory stockCategory3 = new StockCategory();
        stockCategory3.setStock(stock2);
        stockCategory3.setCategory(category4);
        stockCategory3.setCreatedDate(new Date());
        stockCategory3.setCreatedBy("system4");

        StockCategory stockCategory4 = new StockCategory();
        stockCategory4.setStock(stock2);
        stockCategory4.setCategory(category3);
        stockCategory4.setCreatedDate(new Date());
        stockCategory4.setCreatedBy("system5");


        stock.getStockCategories().add(stockCategory1);
        stock.getStockCategories().add(stockCategory2);

        stock2.getStockCategories().add(stockCategory3);
        stock2.getStockCategories().add(stockCategory4);

        session.save(stock);
        session.save(stock2);

		session.getTransaction().commit();
		System.out.println("Done");
	}
}
