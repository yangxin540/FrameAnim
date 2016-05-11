package com.juan.frameanimdemo.model;


import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class GsonBooleanDeserializer implements JsonDeserializer<Object> {

	@Override
	public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

		try {
			String value = json.getAsJsonPrimitive().getAsString();
			
			if("1".equalsIgnoreCase(value.toLowerCase()) || "true".equalsIgnoreCase(value.toLowerCase())){
				return true;
			}
			
			if("0".equalsIgnoreCase(value.toLowerCase()) || "false".equalsIgnoreCase(value.toLowerCase())){
				return false;
			}
			
		} catch (Exception e) {
			Log.e("GsonBooleanDeserializer", "Cannot parse json data '" + json.toString() + "' exception:"+e);
		}
		
		return false;
	}
	
}
