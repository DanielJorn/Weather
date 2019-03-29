package example.yuratoxa.weather;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

class Forecast {

    private Resources res;
    private Context context;
    private String description;
    private int cloudy;
    private int pressure;
    private String date;
    private String imageName;
    private ArrayList<String> spinnerForecastList;

    public Forecast(Context ctx) {
        this.context = ctx;
        res = context.getResources();
        String loading = res.getString(R.string.loading);
        this.description = loading;
        this.cloudy = 0;
        this.pressure = 0;
        //this.imageName = R.drawable.loading;
        this.date = loading;
        spinnerForecastList = new ArrayList<>();
        spinnerForecastList.add(loading);
    }

    public Forecast(Context ctx, String description,
                    int cloudy, int pressure, String date,
                    String imageName, ArrayList<String> spinnerForecastList) {
        this.context = ctx;
        res = context.getResources();
        this.description = description;
        this.cloudy = cloudy;
        this.pressure = pressure;
        this.date = date;
        this.imageName = imageName;
        this.spinnerForecastList = (ArrayList<String>) spinnerForecastList.clone();
    }

    public void setAllVariables(String description,
                                int cloudy, int pressure, String date,
                                String imageName, ArrayList<String> spinnerForecastList) {
        this.description = description;
        this.cloudy = cloudy;
        this.pressure = pressure;
        this.date = date;
        this.imageName = imageName;
        this.spinnerForecastList = (ArrayList<String>) spinnerForecastList.clone();
    }

    public ArrayList<String> getSpinnerForecastList() {
        return spinnerForecastList;
    }

    public void setSpinnerForecastList(ArrayList<String> spinnerForecastList) {
        this.spinnerForecastList = spinnerForecastList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Resources getRes() {
        return res;
    }

    public void setRes(Resources res) {
        this.res = res;
    }

    public int getPressure() {
        return pressure;
    }

    public String getDescription() {
        return description;
    }

    public int getCloudy() {
        return cloudy;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCloudy(int cloudy) {
        this.cloudy = cloudy;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean notEquals(Forecast forecast) {
        return !(forecast.description.equals(this.description)
                && forecast.cloudy == this.cloudy
                && forecast.pressure == this.pressure
                && forecast.date.equals(this.date)
                && forecast.imageName.equals(this.imageName)
                && forecast.spinnerForecastList.equals(this.spinnerForecastList));

    }
}
