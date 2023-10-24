package com.sladejunior.movies30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sladejunior.movies30.data.MainViewModel;
import com.sladejunior.movies30.data.Movie;
import com.sladejunior.movies30.utils.JSONUtils;
import com.sladejunior.movies30.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        movieAdapter = new MovieAdapter();
        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters.setAdapter(movieAdapter);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        switchSort.setChecked(true);

        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

//5
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Toast.makeText(MainActivity.this, "Clicked"+position, Toast.LENGTH_LONG).show();
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "Ro'yxat yakuni", Toast.LENGTH_LONG).show();
            }
        });

        textViewTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMethodOfSort(true);
            }
        });
        textViewPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMethodOfSort(false);
            }
        });

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });
    }

    public void setMethodOfSort(boolean isTopRated){
        int methodOfSort;
        if(isTopRated){
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            textViewPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            switchSort.setChecked(true);
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewTopRated.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            textViewPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            switchSort.setChecked(false);
        }
        downloadData(methodOfSort,1);

    }

    public void downloadData(int methodOfSort, int page){
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort,1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if(movies != null && !movies.isEmpty()){
            viewModel.deleteAllMovies();
            for (Movie movie: movies) {
                viewModel.insertMovie(movie);
            }
        }

    }

}
