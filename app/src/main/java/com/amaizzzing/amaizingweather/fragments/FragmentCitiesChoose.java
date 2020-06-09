package com.amaizzzing.amaizingweather.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.AllDataFromServer;
import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.adapters.CitiesChooseAdapter;
import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.mappers.CityMapper;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.models.City;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.HistoryChooseCitiesRepository;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentCitiesChoose extends Fragment implements CitiesChooseAdapter.OnItemClickListener, Constants {
    private static final int COUNT_SEARCH_SYMBOLS = 3;
    private static final int LIMIT_LOAD_HISTORY_CITIES = 100;

    private RecyclerView rvCities_CitiesChoose;
    private CheckBox checkMoreInfo_CitiesChoose;
    private CitiesChooseAdapter citiesChooseAdapter;
    private ProgressBar pbNetworkLoad_CitiesChoose;
    private SearchView searchViewLandscape_CitiesChoose;

    private ArrayList<CityRoom> cities;
    private ArrayList<CityRoom> citiesFromSearch;
    private ArrayList<CityRoom> finalCitiesList;
    private CompositeDisposable compositeDisposable;

    private Repository cityRepository;
    private WeatherRequestRepositoryRoom weatherRequestRepositoryRoom;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities_choose, container, false);

        initViews(v);
        initVariables();
        initListeners();
        createData();

        return v;
    }

    private void initVariables() {
        cityRepository = new ContextCity(getContext()).getRepository();
        weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        compositeDisposable = new CompositeDisposable();
        citiesFromSearch = new ArrayList<>();
        finalCitiesList = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_search_fragment_menu, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        final androidx.appcompat.widget.SearchView searchText = (androidx.appcompat.widget.SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAndLoadCities(newText);
                return true;
            }
        });
    }

    private void searchAndLoadCities(String newText) {
        if (newText.length() >= COUNT_SEARCH_SYMBOLS) {
            pbNetworkLoad_CitiesChoose.setVisibility(View.VISIBLE);
            citiesFromSearch.clear();
            compositeDisposable.add(cityRepository.getCityByName(newText.toLowerCase())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            city -> {
                                scrollNotFindCity(newText);
                                pbNetworkLoad_CitiesChoose.setVisibility(View.GONE);
                            },
                            Throwable::printStackTrace,
                            () -> loadDataCity(newText)));
        }
    }

    private void loadDataCity(String newText) {
        compositeDisposable.add(AllDataFromServer.getGeonames(newText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(geonamesModel -> {
                    City city = new City(
                            geonamesModel.getName(),
                            geonamesModel.getCountryName(),
                            geonamesModel.getAdminName1(),
                            -1,
                            geonamesModel.getLat(),
                            geonamesModel.getLng(),
                            new ArrayList<>()
                    );
                    citiesFromSearch.add(CityMapper.CityToRoom(city));
                }, throwable -> {
                    OtherFunctions.makeShortToast(getResources().getString(R.string.text_error_load_from_server), getContext());
                    pbNetworkLoad_CitiesChoose.setVisibility(View.GONE);
                    throwable.printStackTrace();
                }, () -> {
                    initRecycler();
                    pbNetworkLoad_CitiesChoose.setVisibility(View.GONE);
                }));
    }

    private void scrollNotFindCity(String newText) {
        int i;
        for (i = 0; i < finalCitiesList.size(); i++) {
            if (finalCitiesList.get(i).getName().toLowerCase().equals(newText.toLowerCase())) {
                break;
            }
        }
        rvCities_CitiesChoose.smoothScrollToPosition(i);
    }

    private void initListeners() {
        checkMoreInfo_CitiesChoose.setOnClickListener(v -> initRecycler());
        searchViewLandscape_CitiesChoose.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAndLoadCities(newText);
                return true;
            }
        });
    }

    private void createData() {
        cities = new ArrayList<>();
        compositeDisposable.add(cityRepository.getLastHistoryIdCities(LIMIT_LOAD_HISTORY_CITIES)
                .flatMapObservable(Observable::fromIterable)
                .doOnNext(city -> {
                    city.setForecast(new ArrayList<>());
                    compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherForCity(city.getId_city())
                            .flatMapObservable(Observable::fromIterable)
                            .subscribe(
                                    weatherRequestRoom -> city.getForecast().add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRoom)),
                                    Throwable::printStackTrace,
                                    () -> {
                                    }
                            )
                    );

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        city -> cities.add(city),
                        Throwable::printStackTrace,
                        this::initRecycler)
        );
    }

    private void initRecycler() {
        rvCities_CitiesChoose.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCities_CitiesChoose.setHasFixedSize(true);
        citiesChooseAdapter = new CitiesChooseAdapter(createFinalCitiesList(), checkMoreInfo_CitiesChoose.isChecked());
        citiesChooseAdapter.setOnItemClickListener(this);
        rvCities_CitiesChoose.setAdapter(citiesChooseAdapter);
        citiesChooseAdapter.notifyDataSetChanged();
        scrollToActualPosition();
    }

    private void scrollToActualPosition() {
        if (citiesFromSearch.size() != 0) {
            rvCities_CitiesChoose.scrollToPosition(cities.size() - 1);
        } else {
            rvCities_CitiesChoose.scrollToPosition(0);
        }
    }

    private ArrayList<CityRoom> createFinalCitiesList() {
        finalCitiesList = new ArrayList<>();
        finalCitiesList.addAll(cities);
        finalCitiesList.addAll(citiesFromSearch);
        return finalCitiesList;
    }

    private void initViews(View v) {
        rvCities_CitiesChoose = v.findViewById(R.id.rvCities_CitiesChoose);
        checkMoreInfo_CitiesChoose = v.findViewById(R.id.checkMoreInfo_CitiesChoose);
        pbNetworkLoad_CitiesChoose = v.findViewById(R.id.pbNetworkLoad_CitiesChoose);
        searchViewLandscape_CitiesChoose = v.findViewById(R.id.searchViewLandscape_CitiesChoose);
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            searchViewLandscape_CitiesChoose.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int pos) {
        finalCitiesList.get(pos).clearWeatherForecast();
        if (requireActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            OtherFunctions.hideKeyboardFrom(requireActivity(), rvCities_CitiesChoose);
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            citiesChooseAdapter.notifyDataSetChanged();
        }
        HistoryChooseCitiesRepository historyChooseCitiesRepository = new HistoryChooseCitiesRepository();
        historyChooseCitiesRepository.addToHistory(finalCitiesList.get(pos))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        cityRepository.publisherNotify(finalCitiesList.get(pos).getName());
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }
}
