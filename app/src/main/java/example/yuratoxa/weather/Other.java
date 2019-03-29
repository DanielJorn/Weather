/*
public void setVariablesFrom(Forecast forecast) {
        Resources res = context.getResources();
        Picasso.with(context)
                .load("https://api.openweathermap.org/img/w/" + forecast.getImageName())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView);
        ArrayList<String> spinnerStrings = forecast.getSpinnerForecastList();
        String cloudy = res.getString(R.string.cloudy, forecast.getCloudy());
        String pressure = res.getString(R.string.pressure, forecast.getPressure());

        cloudyView.setText(cloudy);
        descriptionView.setText(forecast.getDescription());
        pressureView.setText(pressure);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, spinnerStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forecastSpinner.setAdapter(spinnerAdapter);
    }

*/

/*    private void getFieldsFromWeatherList(WeatherList list) {
        description = list.getWeather().get(0).getDescription();
        cloudy = list.getClouds().getAll();
        pressure = list.getMain().getPressure().intValue();
        date = list.getDtTxt();
        image = list.getWeather().get(0).getIcon();
    }
    */