package tester.execution.output;

import static tester.execution.configuration.Paths.LOG_FILE_PATH;
import static tester.settings.Constants.IMPROVE_JSON_VISUALIZATION;
import static tester.execution.configuration.ScriptTesterProperties.UNDEFINED_SCRIPT_TESTER_PROPERTY;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import it.greenvulcano.gvesb.buffer.GVBuffer;

public class Visualizer {

	public static void printGVBuffer(GVBuffer gvbuffer, String bufferName) {
		String output = generateBufferInfo(gvbuffer, bufferName);
		System.out.print(output);
	}

	public static void writeInTheLog(GVBuffer gvbuffer, String bufferName) throws IOException {
		String output = generateBufferInfo(gvbuffer, bufferName);
		Files.write(Paths.get(LOG_FILE_PATH), output.getBytes(), StandardOpenOption.APPEND);
	}

	private static String generateBufferInfo(GVBuffer gvbuffer, String bufferName) {
		String output = "";
		output += "---------------- GV BUFFER --------------------\n";
		output += "\n";
		if(bufferName!=null) {
			output += "> Name = " + bufferName + "\n";
			output += "\n";
		}
		String bufferObject;
		try {
			bufferObject = (String)gvbuffer.getObject();
		} catch (Exception e) {
			// object is Undefined
			bufferObject = UNDEFINED_SCRIPT_TESTER_PROPERTY;
		}
		output = printElement(output, bufferObject, "Object");
		output += "\n";

		if(gvbuffer.getPropertyNames().length<1) {
			output += "> No Properties" + "\n";
		} else {
			output += "> Properties:" + "\n";
			for(String key:gvbuffer.getPropertyNames()) {
				String propertyValue = gvbuffer.getProperty(key);
				output += "    ";
				output = printElement(output, propertyValue, key);
			}
		}
		output += "-----------------------------------------------" + "\n\n";
		return output;
	}

	private static String printElement(String output, String element, String elementName) {
		if(IMPROVE_JSON_VISUALIZATION) {
			element = formatJson(element);
		}
		if(element!=null && element.contains("\n")) {
			output += "> " + elementName + " (multiline view):\n" + element + "\n";
		} else {
			String info = null;
			if(element==null) {
				info = "is null";
			} else if (element.equals("")){
				info = "is empty";
			} else if (element.equals(UNDEFINED_SCRIPT_TESTER_PROPERTY)){
				info = "is undefined";
			}
			if(info==null) {
				output += "> " + elementName + " = " + element + "\n";
			} else {
				output += "> " + elementName + " " + info + "\n";
			}	
		}
		return output;
	}

	private static String formatJson(String s) {

		String data = s;
		try {
			Object json = new JSONTokener(data).nextValue();
			if (json instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(s);
				return (jsonObject.toString(4));
			} else if (json instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(s);
				return (jsonArray.toString(4));
			}
		} catch (Exception e) {
			// Not a Json :(
		}
		return s;
	}

}
