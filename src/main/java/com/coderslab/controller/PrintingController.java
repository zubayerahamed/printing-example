/**
 * 
 */
package com.coderslab.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.coderslab.Service.StudentService;
import com.coderslab.entity.Student;
import com.csvreader.CsvWriter;

/**
 * @author Zubayer Ahamed
 *
 */
@Controller
@RequestMapping("/")
public class PrintingController {

	private static final Logger logger = LoggerFactory.getLogger(PrintingController.class);

	@Autowired
	private StudentService studentService;

	@GetMapping("/")
	public String loadHomePage(Model model) {
		model.addAttribute("students", studentService.findAll());
		return "index";
	}

	@GetMapping("/print")
	public ResponseEntity<byte[]> printStudents() {
		String msg = "Unable to create document";

		byte[] byt = null;
		ByteArrayOutputStream out = null;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "html"));
		headers.add("X-Content-Type-Options", "nosniff");

		StringBuilder template = null;
		try {
			template = new StringBuilder(this.getClass().getClassLoader().getResource("static").toURI().getPath())
					.append(File.separator).append("xsl-template").append(File.separator)
					.append("student-template.xsl");
		} catch (URISyntaxException e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		if (template == null) {
			msg = "No template found";
			return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Student> students = studentService.findAll();
		try {
			out = transfromToPDFBytes(generateXMLDocumentObject(students), template.toString());
		} catch (TransformerFactoryConfigurationError | TransformerException | ParserConfigurationException
				| FOPException e) {
			if (logger.isErrorEnabled())
				logger.error("{}", e);
			return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (out == null) {
			byt = msg.getBytes();
		} else {
			byt = out.toByteArray();
			headers.setContentType(new MediaType("application", "pdf"));
		}

		return new ResponseEntity<>(byt, headers, HttpStatus.OK);
	}

	@GetMapping("/csv")
	public ResponseEntity<byte[]> generateCSV() {
		String msg = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "html"));
		headers.add("X-Content-Type-Options", "nosniff");

		List<Student> students = null;
		students = studentService.findAll();
		if (students == null) {
			msg = "No Data found";
			return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.OK);
		}

		String absolutePath = null;
		try {
			absolutePath = generateStudentsCSV(students).getAbsolutePath();
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
			msg = "Can't generate CSV file";
			return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.OK);
		}

		byte[] bytes = null;
		File file = new File(absolutePath);
		if (file.exists()) {
			try {
				InputStream in = new FileInputStream(file);
				bytes = IOUtils.toByteArray(in);
			} catch (IOException e) {
				logger.info("Failed to download status CSV file!", e);
			}
		}

		headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "csv"));
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		headers.add("Content-Disposition",
				"attachment; filename=student" + sdf.format(new Date()) + ".csv");
		headers.add("X-Content-Type-Options", "nosniff");

		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	// Generate Object to CSV
	public File generateStudentsCSV(List<Student> students) throws IOException {
		File temp = File.createTempFile("student", ".csv");
		CsvWriter csv = new CsvWriter(new FileWriter(temp, true), ',');
		if (temp.exists()) {
			csv.write("ID");
			csv.write("NAME");
			csv.write("EMAIL");
			csv.endRecord();
		}
		for (Student s : students) {
			csv.write(String.valueOf(s.getId()));
			csv.write(s.getName());
			csv.write(s.getEmail());
			csv.endRecord();
		}
		csv.close();
		return temp;
	}

	// Transform XML Document to PDF
	public ByteArrayOutputStream transfromToPDFBytes(Document doc, String template)
			throws TransformerFactoryConfigurationError, TransformerException, FOPException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File file = new File(template);

		Source xslSrc = new StreamSource(file);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSrc);
		if (transformer == null) {
			throw new TransformerException("File not found: " + template);
		}

		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(new DOMSource(doc), res);

		return out;
	}

	// Generate XML Document from Object
	public Document generateXMLDocumentObject(List<Student> students) throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.newDocument();

		// add elements to document
		Element rootElement = doc.createElement("students");
		doc.appendChild(rootElement);

		for (Student student : students) {
			rootElement.appendChild(getStudent(doc, student));
		}

		return doc;
	}

	// Create Object Element
	public Node getStudent(Document doc, Student student) {
		Element studentElement = doc.createElement("student");
		studentElement.appendChild(getStudentElement(doc, "id", String.valueOf(student.getId())));
		studentElement.appendChild(getStudentElement(doc, "name", student.getName()));
		studentElement.appendChild(getStudentElement(doc, "email", student.getEmail()));
		return studentElement;
	}

	// Create Element and set its value
	public Node getStudentElement(Document doc, String name, String value) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value));
		return element;
	}
}
