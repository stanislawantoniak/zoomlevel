package com.mars.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class ZoomLevelWizzard extends MSSQLSource{
	/**
	 * This function listens at endpoint "/api/zoomlevel". Two ways to invoke it
	 * using "curl" command in bash: 1. curl -d "HTTP Body" {your
	 * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	
	private int bestCountMin = 7;
	private int bestCountMax = 15;
	private int maxRecurrencyDepth = 3;
	
	public ZoomLevelWizzard() throws IOException {
		super();
		//bestCountMin = Integer.getInteger(properties.getProperty("bestCountMin"));
		//bestCountMax = Integer.getInteger(properties.getProperty("bestCountMax"));
	}
	
	@FunctionName("zoomlevel")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) throws IOException, SQLException {

		context.getLogger().info("Java HTTP trigger processed a request.");

		final Double latitude;
		final Double longitude;
		// Parse query parameters
		try {
			latitude = Double.valueOf(request.getQueryParameters().get("latitude"));
			longitude = Double.valueOf(request.getQueryParameters().get("longitude"));
		} catch (NumberFormatException e) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Please pass a latitude and longitude as query parameters").build();
		}
		
		//1D is for 1 mile as a starting delta
		//in zoom params there is logic for adjusting long degrees to lat
		ZoomParams zp = new ZoomParams(1D, latitude, longitude, 0, null);
		
		ZoomLevelResponse zr = zoom(zp);
		
		return request.createResponseBuilder(HttpStatus.OK).body(zr).build();

	}
	
	private ZoomLevelResponse zoom(ZoomParams zp) throws SQLException {
		
		int count = readCount(zp.getLatitude(), zp.getLongitude(), zp.getLatDelta(), zp.getLongDelta());

		zp.setCount(count);
		
		ZoomLevelResponse currentResult = new ZoomLevelResponse(zp);
		
		if (isBetweenMinMax(count)) {
			return currentResult;
		} else {
			if (zp.getRecurrencyDepth() > maxRecurrencyDepth)
				return currentResult;
			else {
				
				Double mult = zp.getMultiplier();
				log.info("count: "+count);
				log.info("best count min: "+bestCountMin);
				if (mult == null)
					//set zoom in or out depending on how many results we have
					if (count > bestCountMax)
						mult = .5D;
					else 
						mult = 2D;
				else 
					//we zoomed in/out but not getting too little/many results
					//time to stop recurrency
					if ((mult > 1 && count > bestCountMax)
							||
						(mult < 1 && count < bestCountMin))
						return currentResult;
				
				ZoomParams nextParams = new ZoomParams(zp.getMilesDelta() * mult , zp.getLatitude(), zp.getLongitude(), zp.getRecurrencyDepth() + 1, mult);
				ZoomLevelResponse nextResult = zoom(nextParams);
				//todo - add logic in case next level result is worse than current
				nextResult.getDetails().add(zp);
				return nextResult;
			}
		}
	}
	Boolean isBetweenMinMax(int count) {
		return count >= bestCountMin && count <= bestCountMax; 
	}
	
	private int readCount(Double latitude, Double longitude, Double deltaLat, Double deltaLong) throws SQLException {
		
		StringBuilder statement = new StringBuilder("exec square_query ")
				.append(Double.toString(latitude)).append(", ")
				.append(Double.toString(longitude)).append(", ")
				.append(Double.toString(deltaLat)).append(", ")
				.append(Double.toString(deltaLong)).append(";");
		
		log.info("Statement: "+statement.toString());
		
		PreparedStatement readStatement = cpds.getConnection().prepareStatement(statement.toString());
		ResultSet resultSet = readStatement.executeQuery();
		resultSet.next();
		return resultSet.getInt("count");
	}
	
}
