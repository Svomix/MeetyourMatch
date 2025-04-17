package com.javanostra.meetyourmatch.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.javanostra.meetyourmatch.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;

import com.javanostra.meetyourmatch.overlay.AnimatedMarker;
import com.javanostra.meetyourmatch.overlay.CustomMarkerClusterer;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.entity.Event;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private MapView mapView = null;
    private CustomMarkerClusterer markerClusterer;
    private List<Event> events = new ArrayList<>();

    float scaleFactor = 1.0f;
    double minZoom = 3.0, maxZoom = 17.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        Log.i("MapFragment", "OSMDroid User Agent set to: " + ctx.getPackageName());

        fetchAllEvents();

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTilesScaledToDpi(true);
        mapView.setTilesScaleFactor(scaleFactor);

        mapView.setMinZoomLevel(minZoom);
        mapView.setMaxZoomLevel(maxZoom);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(14.0);
        GeoPoint startPoint = new GeoPoint(53.212042, 50.177401);
        //GeoPoint startPoint = new GeoPoint(59.934280, 30.335099);
        mapController.setCenter(startPoint);

        setupMarkerClusterer(mapView);

        return view;
    }

    private void addClusteredMarkers(CustomMarkerClusterer clusterer) {
        if (clusterer == null || getContext() == null || events == null) {
            return;
        }
        Context context = getContext();
        Drawable defaultMarkerIcon = ContextCompat.getDrawable(context, R.drawable.my_location_marker);

        Log.i("MapFragment", "Adding " + events.size() + " markers...");
        int addedCount = 0;
        for (Event currentEvent : events) {
            if (currentEvent.getLocation() != null)
            {
                GeoPoint point = new GeoPoint(
                        currentEvent.getLocation().getLatitude(),
                        currentEvent.getLocation().getLongitude());

                Marker marker = createMarker(
                        point,
                        currentEvent.getTitle(),
                        currentEvent.getDescription(),
                        defaultMarkerIcon,
                        currentEvent);

                if (marker != null) {
                    clusterer.add(marker);
                    addedCount++;
                } else {
                    Log.w("MapFragment", "Marker creation error: " + currentEvent.getTitle());
                }
            } else {
                Log.w("MapFragment", "Invflid coords: " + currentEvent.getTitle());
            }
        }
        Log.i("MapFragment", "Added " + addedCount + " markers.");

        clusterer.invalidate();
    }

    private void setupMarkerClusterer(MapView map) {
        if (getContext() == null) return;

        markerClusterer = new CustomMarkerClusterer(getContext());

        try {
            Drawable clusterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cluster_background);
            Bitmap clusterBitmap = drawableToBitmap(clusterDrawable);

            markerClusterer.setIcon(clusterBitmap);

            markerClusterer.getTextPaint().setColor(Color.WHITE);
            markerClusterer.getTextPaint().setTextSize(19 * getResources().getDisplayMetrics().density);
            markerClusterer.getTextPaint().setTextAlign(Paint.Align.CENTER);

        } catch (Exception e) {
            Log.e("MapFragment", "Ошибка настройки иконки кластера", e);
        }

        markerClusterer.setRadius(100);
        markerClusterer.setMaxClusteringZoomLevel(17);
        markerClusterer.setAnimation(true);

        map.getOverlays().add(markerClusterer);
    }

    private Marker createMarker(GeoPoint p, String title, String snippet, Drawable icon, Event relatedEvent) {
        if (mapView == null) return null;

        Marker marker = new AnimatedMarker(mapView);
        marker.setPosition(p);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        //marker.setSnippet(snippet);

        if (icon != null) {
            marker.setIcon(icon);
        }

        marker.setRelatedObject(relatedEvent);

        marker.setOnMarkerClickListener((m, map) -> {
            Object related = m.getRelatedObject();
            if (related instanceof Event) {
                Event clickedEvent = (Event) related;
                ArrayList<Event> eventList = new ArrayList<>();
                eventList.add(clickedEvent);
                showEventDetailsSheet(eventList);
            } else {
                Log.w("MapFragment", "Клик по маркеру без связанного объекта Event");
                // m.showInfoWindow();
                Toast.makeText(getContext(), "Информация о событии недоступна", Toast.LENGTH_SHORT).show();
            }
            map.getController().animateTo(m.getPosition());

            return true;
        });

        return marker;
    }

    private void showEventDetailsSheet(ArrayList<Event> events) {
        if (events == null || events.isEmpty()) {
            Log.w("MapFragment", "\n" +
                    "An attempt to show details for an empty list of events.");
            return;
        }
        if (!isAdded()) {
            Log.w("MapFragment", "The fragment is not attached, can't show the bottom sheet.");
            return;
        }
        EventBottomSheetDialogFragment bottomSheet = EventBottomSheetDialogFragment.newInstance(events);
        bottomSheet.show(getParentFragmentManager(), "EventDetailsBottomSheetTag");
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();
        int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        if (width <= 0) width = 96;
        if (height <= 0) height = 96;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void fetchAllEvents() {
        EventApiService apiService = RetrofitClient.getRetrofit(getActivity().getApplicationContext()).create(EventApiService.class);
        Log.d("MapFragment", "Requesting Events...");
        apiService.findAllEventsPageout(0, 450).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body();
                    Log.d("MapFragment", "Events received: " + events.size());
                    if (markerClusterer != null) {
                        markerClusterer.getItems().clear();
                        addClusteredMarkers(markerClusterer);
                        Log.d("MapFragment", "Markers are added after loading the events.");
                    } else {
                        Log.w("MapFragment", "Clusterer has not yet been initialized by the time the events are received.");
                    }
                } else {
                    Log.e("MapFragment", "Event loading error: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                Log.e("MapFragment", "Ошибка сети при загрузке событий", t);
                Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mapView != null) {
            mapView.onDetach();
        }
        mapView = null;
    }
}