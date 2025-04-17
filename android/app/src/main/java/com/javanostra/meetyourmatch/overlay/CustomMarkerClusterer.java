package com.javanostra.meetyourmatch.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.fragment.EventBottomSheetDialogFragment;
import com.javanostra.meetyourmatch.persistance.entity.Event;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMarkerClusterer extends RadiusMarkerClusterer {

    private static final int ZOOM_PADDING_PIXELS = 100;
    private static final double ZOOM_TOLERANCE = 0.1;
    private Context mContext;

    public CustomMarkerClusterer(Context ctx) {
        super(ctx);
        this.mContext = ctx;
    }

    @Override
    public Marker buildClusterMarker(StaticCluster cluster, MapView mapView) {
        Marker clusterMarker = super.buildClusterMarker(cluster, mapView);
        if (clusterMarker == null) return null;

        clusterMarker.setOnMarkerClickListener((marker, map) -> {
            List<Marker> items = new ArrayList<>();
            for (int i = 0; i < cluster.getSize(); i++) {
                items.add(cluster.getItem(i));
            }
            if (items == null || items.isEmpty()) {
                return true;
            }

            double currentZoom = map.getZoomLevelDouble();
            double maxZoom = map.getMaxZoomLevel();

            boolean shouldShowDetails = (maxZoom - currentZoom) < ZOOM_TOLERANCE;

            Log.d("CustomClusterClick", "Current Zoom: " + currentZoom + ", Max Zoom: " + maxZoom + ", Show Details: " + shouldShowDetails);

            if (shouldShowDetails) {
                Log.d("CustomClusterClick", "Showing BottomSheet for cluster size: " + items.size());
                ArrayList<Event> eventsToShow = new ArrayList<>();
                for (Marker itemMarker : items) {
                    Object related = itemMarker.getRelatedObject();
                    if (related instanceof Event) {
                        eventsToShow.add((Event) related);
                    } else {
                        Log.w("CustomMarkerClusterer", "Маркер в кластере не имеет связанного объекта Event!");
                    }
                }

                if (!eventsToShow.isEmpty() && mContext instanceof androidx.fragment.app.FragmentActivity) {
                    FragmentManager fm = ((androidx.fragment.app.FragmentActivity) mContext).getSupportFragmentManager();
                    EventBottomSheetDialogFragment bottomSheet = EventBottomSheetDialogFragment.newInstance(eventsToShow);
                    bottomSheet.show(fm, "EventDetailsBottomSheetTag_FromCluster");
                    map.getController().animateTo(cluster.getPosition());
                } else if (eventsToShow.isEmpty()) {
                    Log.w("CustomMarkerClusterer", "Нет событий (Event) для показа в BottomSheet из этого кластера.");
                    Toast.makeText(map.getContext(), "Нет данных для отображения", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("CustomMarkerClusterer", "Контекст кластеризатора не является FragmentActivity, не могу показать BottomSheet.");
                }

            } else {
                Log.d("CustomClusterClick", "Zooming to cluster with size: " + items.size());
                if (items.size() >= 2) {
                    BoundingBox clusterBoundingBox = BoundingBox.fromGeoPoints(
                            items.stream().map(Marker::getPosition).collect(Collectors.toList())
                    );
                    Log.d("CustomClusterClick", "Zooming to BBox: " + clusterBoundingBox.toString());
                    map.zoomToBoundingBox(clusterBoundingBox, true, ZOOM_PADDING_PIXELS);
                } else if (items.size() == 1) {
                    map.getController().animateTo(items.get(0).getPosition());
                }
            }

            return true;
        });

        return clusterMarker;
    }

    public void setupClusterAppearance() {
        if (mContext == null) return;
        try {
            Drawable clusterDrawable = ContextCompat.getDrawable(mContext, R.drawable.cluster_background);
            if (clusterDrawable != null) {
                Bitmap clusterBitmap = drawableToBitmap(clusterDrawable);
                if (clusterBitmap != null) {
                    setIcon(clusterBitmap);
                } else {
                    Log.e("CustomClusterer", "Не удалось создать Bitmap для иконки кластера.");
                }
            } else {
                Log.e("CustomClusterer", "Не удалось загрузить Drawable cluster_background.");
            }

            getTextPaint().setColor(Color.WHITE);
            getTextPaint().setTextSize(19 * mContext.getResources().getDisplayMetrics().density);
            getTextPaint().setTextAlign(Paint.Align.CENTER);

        } catch (Exception e) {
            Log.e("CustomClusterer", "Ошибка настройки иконки кластера", e);
        }
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            if (!drawable.getBounds().isEmpty()) {
                width = drawable.getBounds().width();
                height = drawable.getBounds().height();
            } else {
                width = 96;
                height = 96;
            }
        }

        if (width <= 0 || height <= 0) {
            Log.e("CustomClusterer", "Невозможно определить размеры Drawable для создания Bitmap.");
            return null;
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError | IllegalArgumentException e) {
            Log.e("CustomClusterer", "Ошибка создания Bitmap для Drawable: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onDetach(MapView mapView) {
        getItems().clear();
        mContext = null;
        super.onDetach(mapView);
    }
}