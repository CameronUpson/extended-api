package org.rspeer.runetek.api.component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ExPriceCheck {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static JsonObject OSBUDDY_SUMMARY_JSON;

    /**
     * Gets the price of the item id from the RuneScape website.
     *
     * @param id The id of the item.
     * @return The price of the item; 0 otherwise.
     */
    public static int getRSPrice(int id) throws IOException {
        final Request request = new Request.Builder()
                .url("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=" + id)
                .get()
                .build();
        final Response response = HTTP_CLIENT.newCall(request).execute();
        if (!response.isSuccessful())
            return 0;

        if (response.body() == null)
            return 0;

        final Gson gson = new Gson().newBuilder().create();
        final String price_text = gson.fromJson(response.body().string(), JsonObject.class)
                .getAsJsonObject("item")
                .getAsJsonObject("current")
                .get("price")
                .getAsString();
        final int price = Integer.parseInt(price_text.replaceAll("\\D+", ""));
        return price_text.matches("[0-9]+") ? price : price * (price_text.charAt(0) == 'k' ? 1000 : 1000000);
    }

    /**
     * Sets the OSBuddy price summary json.
     */
    private static void setOSBuddySummaryJson() throws IOException {
        final Request request = new Request.Builder()
                .url("https://storage.googleapis.com/osbuddy-exchange/summary.json")
                .get()
                .build();
        final Response response = HTTP_CLIENT.newCall(request).execute();
        if (!response.isSuccessful())
            return;

        if (response.body() == null)
            return;

        final Gson gson = new Gson().newBuilder().create();
        OSBUDDY_SUMMARY_JSON = gson.fromJson(response.body().string(), JsonObject.class);
    }

    /**
     * Gets the price of the item id from the OSBuddy price summary json. The entire summary data is stored upon first
     * retrieval.
     *
     * @param id The id of the item.
     * @return The price of the item; 0 otherwise.
     */
    public static int getOSBuddyPrice(int id) throws IOException {
        if (OSBUDDY_SUMMARY_JSON == null)
            setOSBuddySummaryJson();

        final JsonObject json_objects = OSBUDDY_SUMMARY_JSON.getAsJsonObject(Integer.toString(id));
        if (json_objects == null)
            return 0;

        return json_objects.get("sell_average").getAsInt();
    }
}
