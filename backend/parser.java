package trainingsplanBackEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;


public class parser 
{
	String input;
	
	public parser(String input)
	{
		this.input = input;
	}
	
	public void parse() throws JSONException 
	{		
		List<String> days = new ArrayList<String>();
		List<String> exercises = new ArrayList<String>();
		int count = 0;
		
		count = countMatches(input, "day");

		for(int i = 0; i < count; i++)
		{
			exercises = saveExercise(getExercisesPerDay(getDay(saveDays(input, days), i)), exercises);
		}
		
		for(int j = 0; j < exercises.size(); j++)
		{
			getExerciseValues(exercises, j);
		}
	}
	
	public static int countMatches(String str, String toFind) 
	{
		int lastIndex = 0;
		int count = 0;

		while (lastIndex != -1) 
		{
		    lastIndex = str.indexOf(toFind, lastIndex);

		    if (lastIndex != -1) 
		    {
		        count++;
		        lastIndex += toFind.length();
		    }
		}
		
		return count;
	}
	
	public static List<String> saveDays(String plan, List<String> days)
	{
		Matcher m = Pattern.compile(Pattern.quote("[")+ "(.*?)"+ Pattern.quote("]")).matcher(plan);
		String day = "";
		
		while(m.find())
		{
			String match = m.group(1);
			
			if(match.substring(0, Math.min(match.length(), 8)).equalsIgnoreCase("{\"day\":["))
			{
				day = "{\"plan\": " + match + "]}}";
			}
			else
			{
				day = "{\"plan\": {\"day\":[" + match + "]}}";
			}
			
			days.add(day);
		}
		
		return days;
	}
	
	public static String getDay(List<String> days, int index)
	{
		return days.get(index).toString();
	}
	
	public static String cut(String input, String toCut)
	{
		JsonObject object = Json.parse(input).asObject();
		JsonArray items = object.get("plan").asArray();
		return items.toString();
	}
	
	public static String getExercisesPerDay(String day) throws JSONException
	{
		JSONObject req = new JSONObject(day);
		JSONObject locs = req.getJSONObject("plan");
		JSONArray recs = locs.getJSONArray("day");
		
		return removeFrontBack(recs.toString());
	}
	
	public static String removeFrontBack(String str) 
	{
		return str.substring(1, str.length()-1);
	}
	
	public static List<String> saveExercise(String singleExercise, List<String> exercises)
	{
		Matcher m = Pattern.compile(Pattern.quote("{\"exercise\":{")+ "(.*?)"+ Pattern.quote("}}")).matcher(singleExercise);
		String day = "";
		
		while(m.find())
		{
			String match = m.group(1);
			
			exercises.add("{" + match + "}");
		}
		
		return exercises;
	}
	
	public static void getExerciseValues(List<String> exercises, int index) throws JSONException
	{
        JSONObject jsonObject = new JSONObject(exercises.get(index));
        
        System.out.println(jsonObject.get("name"));
        System.out.println(jsonObject.get("weight"));
        System.out.println(jsonObject.get("reps"));
        System.out.println(jsonObject.get("sets"));
        System.out.println(jsonObject.get("maxrep"));
        System.out.println(jsonObject.get("pause"));
	}
}
