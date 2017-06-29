package dao;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExchangeRateDAO {

    private final static String OER_URL = "http://openexchangerates.org/api/";
    private static final String LATEST = "latest.json?app_id=%s";
    private static final String APP_ID = "6588fb4ba3d94690b088e4df9ac12cef";

    private final static ObjectMapper mapper = new ObjectMapper();

    public int convertToUSD(int value, String currency) {
        try {
            Map<String, BigDecimal> exchangeRates = new HashMap<>();
            String urlString = String.format(OER_URL + LATEST, APP_ID);
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            JsonNode node = mapper.readTree(conn.getInputStream());
            Iterator<Map.Entry<String, JsonNode>> fieldNames = node.get("rates").fields();
            fieldNames.forEachRemaining(e -> exchangeRates.put(e.getKey(), e.getValue().decimalValue()));
            BigDecimal multiplier = exchangeRates.get(currency);
            int result = BigDecimal.valueOf(value).divide(multiplier, 2, BigDecimal.ROUND_HALF_UP).intValue();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error getting exchange rate", e);
        }
    }
}
