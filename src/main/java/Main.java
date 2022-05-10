import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //задача 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCsv = parseCSV(columnMapping, fileName);
        String json = listToJson(listCsv);
        writeString(json, "new-data.json");


        //задача 2
        List<Employee> listXml = parseXML("data.xml");
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "new-data1.json");

        //задача 3
        String jsonRead = readString("new-data1.json");
        List<Employee> list = jsonToList(jsonRead);
        for (Employee employee : list) {
            System.out.println(employee);
        }
    }
    private static List<Employee> jsonToList(String jsonRead) {
        List<Employee> list = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(jsonRead);
            for (int i = 0; i < array.toArray().length; i++) {
                Object obj = array.get(i);
                JSONObject jsonObject = (JSONObject) obj;
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                String jsonObj = jsonObject.toString();
                Employee employee = gson.fromJson(jsonObj, Employee.class);
                list.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String fileName) {
        String line = "";
        StringBuilder lineJson = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                lineJson.append(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineJson.toString();
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(fileName));
            Node node = document.getDocumentElement();
            NodeList nodeList = node.getChildNodes();
            String firstName = null, lastName = null, country = null;
            int age = 0;
            long id = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    NodeList nodeEmp = node_.getChildNodes();
                    for (int j = 0; j < nodeEmp.getLength(); j++) {
                        Node no = nodeEmp.item(j);
                        if (Node.ELEMENT_NODE == no.getNodeType()) {
                            switch (no.getNodeName()) {
                                case "id":
                                    id = Long.parseLong(no.getTextContent());
                                    break;
                                case "firstName":
                                    firstName = no.getTextContent();
                                    break;
                                case "lastName":
                                    lastName = no.getTextContent();
                                    break;
                                case "country":
                                    country = no.getTextContent();
                                    break;
                                default:
                                    age = Integer.parseInt(no.getTextContent());
                            }
                        }
                    }
                    staff.add(new Employee(id, firstName, lastName, country, age));
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return staff;
    }


    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }
                .getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staffCsv = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staffCsv = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffCsv;
    }
}
