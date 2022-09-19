import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		String[] columnMapping = { "id", "firstName", "lastName", "country", "age" };
		String fileName = "data.csv";
		String csvJsonFileName = "data.json";
		String xmlFileName = "data.xml";
		String xmlJsonFileName = "data2.json";

		List<Employee> list = parseCSV(columnMapping, fileName);
		String json = (String) listToJson(list);
		writeString(json, csvJsonFileName);

		List<Employee> listFromXML = parseXML(xmlFileName);
		String jsonFromXML = (String) listToJson(listFromXML);
		writeString(jsonFromXML, xmlJsonFileName);
	}

	private static List<Employee> parseXML(String xmlFileName) {
		try {
			List<Employee> list = new ArrayList<>();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(xmlFileName));

			Node root = doc.getDocumentElement();
			NodeList nodeList = root.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {

				Node node = nodeList.item(i);

				if (Node.ELEMENT_NODE == node.getNodeType()) {

					Element employee = (Element) node;

					long id = Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent());
					String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
					String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
					String country = employee.getElementsByTagName("country").item(0).getTextContent();
					int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());

					list.add(new Employee(id, firstName, lastName, country, age));
				}
			}
			return list;
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	private static void writeString(String json, String fileName) {
		try (FileWriter fileWriter = new FileWriter(fileName)) {
			fileWriter.write(json);
			fileWriter.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static <T> Object listToJson(List<T> list) {
		Gson gson = new Gson();
		Type listType = new TypeToken<List<T>>() {
		}.getType();

		return gson.toJson(list, listType);
	}

	private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

		try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
			ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
			strategy.setType(Employee.class);
			strategy.setColumnMapping(columnMapping);

			CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).withMappingStrategy(strategy).build();
			return csv.parse();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}