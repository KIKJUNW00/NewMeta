package com.newmeta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Dummy 클래스: CSV 데이터를 임시로 저장하기 위한 클래스
class Dummy {
	String epc_code;        // EPC 코드
	String product_name;    // 제품 이름
	String hub_type;        // 허브 유형
	String event_type;      // 이벤트 유형
	String event_time;      // 이벤트 발생 시간
	
	@Override
	public String toString() {
		return "Dummy [epc_code=" + epc_code + ", product_name=" + product_name + ", hub_type=" + hub_type
				+ ", event_type=" + event_type + ", event_time=" + event_time + "]";
	}
}

// Product 클래스: 제품 데이터를 나타내는 클래스
class Product {
	String epc_code;        // EPC 코드
	String product_name;    // 제품 이름
}

// Hub 클래스: 허브 데이터를 나타내는 클래스
class Hub {
	int hub_id;             // 허브 ID
	String hub_name;        // 허브 이름
	double lon;             // 경도 (사용 예정)
	double lat;             // 위도 (사용 예정)
}

// Event 클래스: 이벤트 데이터를 나타내는 클래스
class Event {
	int event_id;           // 이벤트 ID
	String event_type;      // 이벤트 유형
}

// Product_Event_Log 클래스: 제품 이벤트 로그 데이터를 나타내는 클래스
class Product_Event_Log {
	String epc_code;        // EPC 코드
	int hub_id;             // 허브 ID
	int event_id;           // 이벤트 ID
	String event_time;      // 이벤트 발생 시간
}

public class Program {

	// 전역 변수로 이벤트, 허브, 제품 목록 선언
	static List<Event> events = new ArrayList<>();	
	static List<Hub> hubs = new ArrayList<>();
	static List<Product> products = new ArrayList<>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {

		long start = System.currentTimeMillis(); // 실행 시간 측정 시작
		
		List<Dummy> list = new ArrayList<>(); // Dummy 객체를 저장할 리스트 생성
		
		// 여러 CSV 파일 로드
		System.out.println("Dummy_1_utf8.csv Loading...");
		loadCSV(list, "C:\\Users\\user\\Desktop\\csv/Dummy_1_utf8.csv");

		System.out.println("Dummy_2_utf8.csv Loading...");
		loadCSV(list, "C:\\Users\\user\\Desktop\\csv/Dummy_2_utf8.csv");
		
		System.out.println("Dummy_3_utf8.csv Loading...");
		loadCSV(list, "C:\\Users\\user\\Desktop\\csv/Dummy_3_utf8.csv");
		
		System.out.println("Dummy_4_utf8.csv Loading...");
		loadCSV(list, "C:\\Users\\user\\Desktop\\csv/Dummy_4_utf8.csv");
		
		System.out.println("Dummy_5_utf8.csv Loading...");
		loadCSV(list, "C:\\Users\\user\\Desktop\\csv/Dummy_5_utf8.csv");
		
		// 각 데이터에 대해 SQL 파일 생성
		System.out.println("products.sql Generating...");
		generateSQLforProduct(list, "products.sql");
			
		System.out.println("hubs.sql Generating...");
		generateSQLforHub(list, "hubs.sql");

		System.out.println("events.sql Generating...");
		generateSQLforEvent(list, "events.sql");
		
		System.out.println("product_event_log.sql Generating...");
		generateSQLforLog(list, "product_event_log.sql");
		
		long end = System.currentTimeMillis(); // 실행 시간 측정 종료
		
		System.out.println("Done!:" + (end - start)); // 총 실행 시간 출력
	}

	// 허브 ID를 가져오는 메서드
	private static int getHubId(Dummy d) {
		
		for(Hub h : hubs) {
			if (h.hub_name.equals(d.hub_type))
				return h.hub_id;
		}
		return -1;
	}
	
	// 이벤트 ID를 가져오는 메서드
	private static int getEventId(Dummy d) {
		
		for(Event e : events) {
			if (e.event_type.equals(d.event_type))
				return e.event_id;
		}
		return -1;
	} 
	
	// Product_Event_Log SQL 생성 메서드
	private static void generateSQLforLog(List<Dummy> dummys, String fname) throws IOException {

		try(FileWriter out = new FileWriter(fname)) {
			for (Dummy d : dummys) {
				String epc_code = d.epc_code;
				int hub_id = getHubId(d);
				int event_id  = getEventId(d);
				String event_time = d.event_time;
	
				StringBuffer sb = new StringBuffer("insert into Product_Event_Log(epc_code, hub_id, event_id, event_time) values(");
				sb.append("'" + epc_code + "'," + hub_id + "," + event_id + ",'" + event_time + "');");
				out.write(sb.toString() + "\n");
			}
		} 				
	} 
	
	// Event SQL 생성 메서드
	private static void generateSQLforEvent(List<Dummy> dummys, String fname) throws IOException {

		Set<String> set = new HashSet<>(); // 이벤트 유형을 저장할 Set
		
		for (Dummy d : dummys) {
			set.add(d.event_type);
		}
		
		int id = 1;
		for(String key : set) {
			Event e = new Event();
			e.event_id = id++;
			e.event_type = key;
			
			events.add(e);
		}
		
		try(FileWriter out = new FileWriter(fname)) {
			
			for(Event e : events) {
				StringBuffer sb = new StringBuffer("insert into Event(event_id, event_type) values(");
				sb.append(e.event_id + ",'");
				sb.append(e.event_type + "');");
				out.write(sb.toString() + "\n");
			}
		} 				
	} 
	
	// Hub SQL 생성 메서드
	private static void generateSQLforHub(List<Dummy> dummys, String fname) throws IOException {
		
		Set<String> set = new HashSet<>(); // 허브 유형을 저장할 Set
		
		for (Dummy d : dummys) {
			set.add(d.hub_type);
		}
		
		int id = 1;
		for(String key : set) {
			Hub h = new Hub();
			h.hub_id = id++;
			h.hub_name = key;
			
			hubs.add(h);
		}
		
		try(FileWriter out = new FileWriter(fname)) {
			
			for(Hub h : hubs) {
				StringBuffer sb = new StringBuffer("insert into Hub(hub_id, hub_name) values(");
				sb.append(h.hub_id + ",'");
				sb.append(h.hub_name + "');");
				out.write(sb.toString() + "\n");
			}
		} 		
	}
	
	// Product SQL 생성 메서드
	private static void generateSQLforProduct(List<Dummy> dummys, String fname) throws IOException {
		
		Map<String, Dummy> map = new HashMap<>(); // EPC 코드와 Dummy 매핑
		
		for (Dummy d : dummys) {
			map.put(d.epc_code, d);
		}
		
		Set<String> set = map.keySet();
		for(String key : set) {
			Dummy d = map.get(key);
			
			Product p = new Product();
			p.epc_code = d.epc_code;
			p.product_name = d.product_name;
			
			products.add(p);
		}
		
		try(FileWriter out = new FileWriter(fname)) {
			
			for(Product p : products) {
				StringBuffer sb = new StringBuffer("insert into Product(epc_code, product_name) values(");
				sb.append("'" + p.epc_code + "','");
				sb.append(p.product_name + "');");
				out.write(sb.toString() + "\n");
			}
		} 		
	}
	
	// CSV 파일 로드 메서드
	private static void loadCSV(List<Dummy> list, String fname) throws FileNotFoundException, IOException {
		
		try(BufferedReader in = new BufferedReader(new FileReader(fname))) {
			
			// 첫 줄은 제목이므로 건너뜀
			String str = in.readLine();
			if (str == null) return;
			
			while((str = in.readLine()) != null) {
				String[] arr = str.split(","); // CSV 데이터를 콤마로 분리
				
				Dummy d = new Dummy();
				d.epc_code = arr[0];
				d.product_name = arr[2];
				d.hub_type = arr[3];
				d.event_type = arr[4];
				d.event_time = arr[5];
				
				list.add(d);
			}
		}
	} 
}
