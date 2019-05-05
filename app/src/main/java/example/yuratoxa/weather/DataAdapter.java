package example.yuratoxa.weather;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private final String TAG = "my tag";
    private LayoutInflater inflater;
    private List<WeatherList> allWeatherLists;
    private int cloudy, pressure;
    private String description, date, image;

    private Context context;
    private int[] colors = new int[2];

    private final long ONE_SECOND = 1000L;
    private final long ONE_MINUTE = ONE_SECOND * 60;
    private final long ONE_HOUR = ONE_MINUTE * 60;
    private final long ONE_DAY = 24 * ONE_HOUR;

    DataAdapter(Context context, List<WeatherList> allWeatherLists) {
        this.allWeatherLists = allWeatherLists;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapter.ViewHolder holder, int position) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.color_first_cell, typedValue, true);

        int receivedColor = typedValue.data;
        colors[0] = receivedColor;

        theme.resolveAttribute(R.attr.color_second_cell, typedValue, true);
        receivedColor = typedValue.data;
        colors[1] = receivedColor;

        holder.getView().setBackgroundColor(colors[position % 2]);

        // if (allWeatherLists == null) holder.setVariablesFrom(new Forecast(context));

        if (allWeatherLists == null) holder.setImage(R.drawable.loading);

        long todayMidnight;
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        long currentTime = today.getTime();

        long hours = calendar.get(Calendar.HOUR_OF_DAY) * ONE_HOUR;
        long minutes = calendar.get(Calendar.MINUTE) * ONE_MINUTE;
        long seconds = calendar.get(Calendar.SECOND) * ONE_SECOND;
        todayMidnight = currentTime - (hours + minutes + seconds);
        todayMidnight = todayMidnight + (position * ONE_DAY);

        final ArrayList<WeatherList> currentWeather = new ArrayList<>();
        final ArrayList<String> spinnerStrings = new ArrayList<>();
        final Spinner spinner = holder.forecastSpinner;
        long tomorrowMidnight = todayMidnight + ONE_DAY;

        if (allWeatherLists != null) {
            for (WeatherList weatherList : allWeatherLists) {

                if (weatherList.getDt() * ONE_SECOND < tomorrowMidnight
                        && weatherList.getDt() * ONE_SECOND >= todayMidnight) {
                    currentWeather.add(weatherList);
                    spinnerStrings.add(weatherList.getDtTxt());
                }
            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, spinnerStrings);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    for (WeatherList weatherList : currentWeather) {

                        if (weatherList.getDtTxt().equals(spinner.getSelectedItem())) {
                            getFieldsFromWeatherList(weatherList);
                            holder.setVariablesFrom(new Forecast(context, description, cloudy,
                                    pressure, date, image, spinnerStrings));
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void setAllWeatherLists(List<WeatherList> allWeatherLists) {
        this.allWeatherLists = allWeatherLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView cloudyView, descriptionView, pressureView;// dateView;
        private final Spinner forecastSpinner;
        private View view;

        private void setImage(@DrawableRes int resId) {
            imageView.setImageResource(resId);
        }

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            cloudyView = view.findViewById(R.id.cloudy);
            descriptionView = view.findViewById(R.id.description);
            pressureView = view.findViewById(R.id.pressure);
            forecastSpinner = view.findViewById(R.id.forecast_spinner);
            this.view = view;
        }

        View getView() {
            return view;
        }

        void setVariablesFrom(Forecast forecast) {
            Resources res = context.getResources();
            Picasso.with(context)
                    .load("https://api.openweathermap.org/img/w/" + forecast.getImageName())
                    .placeholder(R.drawable.loading)
                    .into(imageView);
            String cloudy = res.getString(R.string.cloudy, forecast.getCloudy());
            String pressure = res.getString(R.string.pressure, forecast.getPressure());

            cloudyView.setText(cloudy);
            descriptionView.setText(forecast.getDescription());
            pressureView.setText(pressure);
        }
    }
    private void getFieldsFromWeatherList(WeatherList list) {
        description = list.getWeather().get(0).getDescription();
        cloudy = list.getClouds().getAll();
        pressure = list.getMain().getPressure().intValue();
        date = list.getDtTxt();
        image = list.getWeather().get(0).getIcon();
    }

}
