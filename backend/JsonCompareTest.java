package trainingsplanBackEnd;

import org.json.simple.parser.JSONParser;



public class JsonCompareTest {

	public static void main(String[] args) throws JSONException 
	{
		JSONParser parser = new JSONParser();

		JsonObject json1 = "{\"plan\":[{\"username\": \"jakob engl\",\"planname\": \"vieresplit updatet\",\"day\":[{\"exercise\":{\"name\":\"schulterdruexken\",\"weight\":\"40 30\",\"reps\":\"10 6\",\"sets\":\"4\",\"maxrep\":\"50\",\"pause\":\"2\"}},{\"exercise\":{\"name\":\"frontheben\",\"weight\":\"8 6 4\",\"reps\":\"max\",\"sets\":\"4\",\"maxrep\":\"12\",\"pause\":\"1\"}},{\"exercise\":{\"name\":\"facepulls\",\"weight\":\"20\",\"reps\":\"18\",\"sets\":\"4\",\"maxrep\":\"20\",\"pause\":\"1\"}},{\"exercise\":{\"name\":\"seitheben\",\"weight\":\"7 5 3\",\"reps\":\"max\",\"sets\":\"4\",\"maxrep\":\"12\",\"pause\":\"2\"}}]},{\"day\":[{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}}]}]}";
		String json2 = "{\"plan\":[{\"username\": \"jakob engl\",\"planname\": \"vieresplit updatet\",\"day\":[{\"exercise\":{\"name\":\"schulterdruexken\",\"weight\":\"40 30\",\"reps\":\"10 6\",\"sets\":\"4\",\"maxrep\":\"50\",\"pause\":\"2\"}},{\"exercise\":{\"name\":\"frontheben\",\"weight\":\"8 6 4\",\"reps\":\"max\",\"sets\":\"5\",\"maxrep\":\"12\",\"pause\":\"1\"}},{\"exercise\":{\"name\":\"facepulls\",\"weight\":\"20\",\"reps\":\"18\",\"sets\":\"4\",\"maxrep\":\"20\",\"pause\":\"1\"}},{\"exercise\":{\"name\":\"seitheben\",\"weight\":\"7 5 3\",\"reps\":\"max\",\"sets\":\"4\",\"maxrep\":\"12\",\"pause\":\"2\"}}]},{\"day\":[{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}}]}]}";
		
		JsonObject empObj = new JsonObject(json1);
        reader.close();
        // read string data
        System.out.println("Emp Name: " + empObj.getString("emp_name"));
        // read json array
        JsonArray arrObj = empObj.getJsonArray("direct_reports");
        System.out.println("\nDirect Reports:");
        for(JsonValue value : arrObj){
            System.out.println(value.toString());
        }
			
	}
}
