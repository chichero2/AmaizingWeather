package com.amaizzzing.amaizingweather.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.adapters.HistoryFilterAdapter;
import com.amaizzzing.amaizingweather.functions.DateFunctions;
import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.models.City;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentHistoryFilter extends Fragment {
    private final static String INPUT_DATE1 = "date1";
    private final static String INPUT_DATE2 = "date2";
    private final static int BEGIN_TEMPERATURE = 0;
    private final static int END_TEMPERATURE = 30;

    private Spinner spinnerCity_FragmentHistory;
    private TextView date_FragmentHistory;
    private TextView date2_FragmentHistory;
    private RecyclerView rv_FragmentHistory;
    private MaterialButton filterButton_FragmentHistory;
    private TextInputLayout textInputBeginTemp_FragmentHistory;
    private TextInputLayout textInputEndTemp_FragmentHistory;
    private CardView cv_HistoryFragment;

    private ArrayList<CityRoom> cities;
    private Repository cityRepository;
    private CompositeDisposable compositeDisposable;
    private Calendar dateAndTime = Calendar.getInstance();
    private Calendar dateAndTime2 = Calendar.getInstance();
    private WeatherRequestRepositoryRoom weatherRequestRepositoryRoom;
    private ArrayList<WeatherRequest> weatherRequests;
    private String whichDateChosen = INPUT_DATE1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_filter, container, false);

        initViews(v);
        initVariables();
        initListeners();
        setInitialDateTime();
        setInitialDateTime2();
        createSpinnerData();

        return v;
    }

    private void createSpinnerData() {
        compositeDisposable.add(cityRepository.getCityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityList -> {
                    cities = (ArrayList<CityRoom>) cityList;
                    createSpinner(cities);
                }));
    }

    private void initListeners() {
        date_FragmentHistory.setOnClickListener(v -> {
            whichDateChosen = INPUT_DATE1;
            setDate();
        });
        date2_FragmentHistory.setOnClickListener(v -> {
            whichDateChosen = INPUT_DATE2;
            setDate();
        });
        filterButton_FragmentHistory.setOnClickListener(v -> {
            if (validateEditTexts()) {
                showFindWeather();
            }
        });
        rv_FragmentHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    showOrHideSearchCardView();
                }
            }
        });
    }

    private void showOrHideSearchCardView() {
        if (rv_FragmentHistory.getLayoutManager() != null) {
            int review_position = ((LinearLayoutManager) rv_FragmentHistory.getLayoutManager()).findFirstVisibleItemPosition();
            if (review_position == 0) {
                cv_HistoryFragment.setVisibility(View.VISIBLE);
            } else {
                cv_HistoryFragment.setVisibility(View.GONE);
            }
        }
    }

    private void showFindWeather() {
        weatherRequests = new ArrayList<>();
        long startDay = DateFunctions.atStartOfDay(dateAndTime.getTimeInMillis());
        long endDay = DateFunctions.atEndOfDay(dateAndTime2.getTimeInMillis());
        int beginTemp = Integer.parseInt(textInputBeginTemp_FragmentHistory.getEditText().getText().toString());
        int endTemp = Integer.parseInt(textInputEndTemp_FragmentHistory.getEditText().getText().toString());
        compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherByNameCityBetweenDatesBetweenTemp(spinnerCity_FragmentHistory.getSelectedItem().toString(), startDay, endDay, beginTemp, endTemp)
                .flatMapObservable(Observable::fromIterable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weatherRequestRooms -> weatherRequests.add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRooms)),
                        Throwable::printStackTrace,
                        this::initRecyclerView
                )
        );
    }

    private boolean validateEditTexts() {
        if (textInputBeginTemp_FragmentHistory.getEditText() != null && textInputEndTemp_FragmentHistory.getEditText() != null) {
            if (textInputBeginTemp_FragmentHistory.getEditText().getText().toString().equals("")) {
                textInputBeginTemp_FragmentHistory.setError(getString(R.string.error_enter_temper));
                return false;
            } else {
                textInputBeginTemp_FragmentHistory.setError(null);
            }
            if (textInputEndTemp_FragmentHistory.getEditText().getText().toString().equals("")) {
                textInputEndTemp_FragmentHistory.setError(getString(R.string.error_enter_temper));
                return false;
            } else {
                textInputEndTemp_FragmentHistory.setError(null);
            }
        } else {
            OtherFunctions.makeShortToast(getString(R.string.text_error_load_data), requireActivity());
            return false;
        }
        return true;
    }

    private void initRecyclerView() {
        rv_FragmentHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        HistoryFilterAdapter historyFilterAdapter = new HistoryFilterAdapter(weatherRequests);
        rv_FragmentHistory.setAdapter(historyFilterAdapter);
        historyFilterAdapter.notifyDataSetChanged();
    }

    private void createSpinner(ArrayList<CityRoom> cityList) {
        ArrayAdapter<CityRoom> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity_FragmentHistory.setAdapter(adapter);
    }

    private void initVariables() {
        cityRepository = new ContextCity(getContext()).getRepository();
        compositeDisposable = new CompositeDisposable();
        weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        weatherRequests = new ArrayList<>();
        textInputBeginTemp_FragmentHistory.getEditText().setText(String.valueOf(BEGIN_TEMPERATURE));
        textInputEndTemp_FragmentHistory.getEditText().setText(String.valueOf(END_TEMPERATURE));
    }

    private void initViews(View v) {
        spinnerCity_FragmentHistory = v.findViewById(R.id.spinnerCity_FragmentHistory);
        date_FragmentHistory = v.findViewById(R.id.date_FragmentHistory);
        date2_FragmentHistory = v.findViewById(R.id.date2_FragmentHistory);
        rv_FragmentHistory = v.findViewById(R.id.rv_FragmentHistory);
        filterButton_FragmentHistory = v.findViewById(R.id.filterButton_FragmentHistory);
        textInputBeginTemp_FragmentHistory = v.findViewById(R.id.textInputBeginTemp_FragmentHistory);
        textInputEndTemp_FragmentHistory = v.findViewById(R.id.textInputEndTemp_FragmentHistory);
        cv_HistoryFragment = v.findViewById(R.id.cv_HistoryFragment);
    }

    private void setDate() {
        if (whichDateChosen.equals(INPUT_DATE1)) {
            showDatePicker(dateAndTime);
        } else {
            showDatePicker(dateAndTime2);
        }
    }

    private void showDatePicker(Calendar dAndT) {
        new DatePickerDialog(requireActivity(), d,
                dAndT.get(Calendar.YEAR),
                dAndT.get(Calendar.MONTH),
                dAndT.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        if (whichDateChosen.equals(INPUT_DATE1)) {
            prepareDateAndTime(dateAndTime, year, monthOfYear, dayOfMonth);
            setInitialDateTime();
        } else {
            prepareDateAndTime(dateAndTime2, year, monthOfYear, dayOfMonth);
            setInitialDateTime2();
        }
    };

    private void prepareDateAndTime(Calendar dAndT, int year, int monthOfYear, int dayOfMonth) {
        dAndT.set(Calendar.YEAR, year);
        dAndT.set(Calendar.MONTH, monthOfYear);
        dAndT.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    private void setInitialDateTime() {
        date_FragmentHistory.setText(DateUtils.formatDateTime(requireActivity(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void setInitialDateTime2() {
        date2_FragmentHistory.setText(DateUtils.formatDateTime(requireActivity(),
                dateAndTime2.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }
}
