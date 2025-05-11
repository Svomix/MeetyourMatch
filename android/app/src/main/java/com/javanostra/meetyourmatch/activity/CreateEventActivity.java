package com.javanostra.meetyourmatch.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox; // Используем CheckBox
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout; // Используем FlexboxLayout
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.MapApiService;
import com.javanostra.meetyourmatch.persistance.api_service.TagApiService;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.EventUploadDTO;
import com.javanostra.meetyourmatch.persistance.entity.FileUploadedDTO;
import com.javanostra.meetyourmatch.persistance.entity.MapObjectDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";
    private static final int MAX_TAGS = 6;
    private static final String UCROP_FILE_NAME_PREFIX = "MeetYourMatch_Cover_"; // Добавим префикс

    // Views
    private Toolbar toolbar;
    private ImageView coverPreviewImageView;
    private Button selectImageButton;
    private EditText titleEditText, descriptionEditText, priceEditText, sourceUrlEditText;
    private TextView dateTimeTextView;
    private Calendar selectedDateTimeCalendar = Calendar.getInstance();
    private Timestamp selectedTimestamp = null;
    private AutoCompleteTextView locationAutoComplete;
    private FlexboxLayout tagsFlexboxLayout; // Заменен ChipGroup на FlexboxLayout
    private TextView tagLimitErrorTextView;
    private Button createEventButton;
    private ProgressBar progressBar;

    // API Services
    private EventApiService eventApiService;
    private TagApiService tagApiService;
    private MapApiService mapApiService;

    // Data
    private List<Tag> availableTags = new ArrayList<>();
    private List<MapObjectDTO> availableLocations = new ArrayList<>();
    private Map<String, String> locationNameToIdMap = new HashMap<>(); // Для связи имени и ID локации
    private List<Long> selectedTagIds = new ArrayList<>();
    private String selectedLocationId = null;
    private Uri croppedImageUri = null; // URI обрезанного изображения
    private String uploadedCoverFileId = null; // ID загруженного файла

    // ActivityResultLaunchers
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> uCropLauncher;

    // Флаги и Handler для отслеживания загрузки
    private boolean tagsLoaded = false;
    private boolean locationsLoaded = false;
    private final Handler dataLoadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initViews();
        setupToolbar();
        initApiServices();
        initResultLaunchers();
        setupListeners();
        addInputFilters();

        fetchInitialData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_create_event);
        coverPreviewImageView = findViewById(R.id.image_event_cover_preview);
        selectImageButton = findViewById(R.id.button_select_image);
        titleEditText = findViewById(R.id.edit_text_event_title);
        descriptionEditText = findViewById(R.id.edit_text_event_description);
        dateTimeTextView = findViewById(R.id.text_view_event_date_time);
        priceEditText = findViewById(R.id.edit_text_event_price);
        sourceUrlEditText = findViewById(R.id.edit_text_event_source_url);
        locationAutoComplete = findViewById(R.id.autocomplete_location);
        tagsFlexboxLayout = findViewById(R.id.flexbox_layout_tags); // Находим FlexboxLayout
        tagLimitErrorTextView = findViewById(R.id.text_view_tag_limit_error);
        createEventButton = findViewById(R.id.button_create_event);
        progressBar = findViewById(R.id.progress_bar_create_event);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initApiServices() {
        eventApiService = RetrofitClient.getRetrofit(this).create(EventApiService.class);
        tagApiService = RetrofitClient.getRetrofit(this).create(TagApiService.class);
        mapApiService = RetrofitClient.getRetrofit(this).create(MapApiService.class);
    }

    // Инициализация ActivityResultLaunchers
    private void initResultLaunchers() {
        // Запрос разрешений
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        launchImagePicker();
                    } else {
                        Toast.makeText(this, R.string.error_permission_denied_storage, Toast.LENGTH_SHORT).show();
                    }
                });

        // Выбор изображения
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri sourceUri = result.getData().getData();
                        Log.d(TAG, "Image picked: " + sourceUri);
                        launchUCrop(sourceUri); // Запускаем UCrop после выбора
                    } else {
                        Log.w(TAG, "Image picking cancelled or failed.");
                    }
                });

        // Обрезка изображения (UCrop)
        uCropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Log.d(TAG, "uCropLauncher callback received. ResultCode: " + result.getResultCode()); // Лог входа

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        final Uri resultUri = UCrop.getOutput(result.getData()); // Получаем URI результата
                        if (resultUri != null) {
                            croppedImageUri = resultUri; // Сохраняем URI
                            Log.i(TAG, "UCrop successful. Cropped image URI: " + croppedImageUri.toString());

                            // --- Сначала сбрасываем стили ImageView ---
                            coverPreviewImageView.setPadding(0, 0, 0, 0);
                            coverPreviewImageView.setBackground(null);
                            coverPreviewImageView.setImageTintList(null);
                            // -----------------------------------------

                            // --- Затем загружаем изображение ---
                            Glide.with(this)
                                    .load(croppedImageUri)
                                    .placeholder(R.drawable.baseline_insert_photo_24) // Убедитесь, что этот drawable существует
                                    .error(R.drawable.baseline_error_outline_24)       // Убедитесь, что этот drawable существует
                                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, @Nullable Object model, @NonNull com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                            Log.e(TAG, "Glide onLoadFailed for URI: " + model, e);
                                            Toast.makeText(CreateEventActivity.this, R.string.error_loading_preview, Toast.LENGTH_SHORT).show(); // Добавьте строку
                                            return false; // Позволяем Glide обработать error() drawable
                                        }

                                        @Override
                                        public boolean onResourceReady(@NonNull android.graphics.drawable.Drawable resource, @NonNull Object model, @NonNull com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                            Log.d(TAG, "Glide onResourceReady for URI: " + model);
                                            return false; // Позволяем Glide установить ресурс
                                        }
                                    })
                                    .into(coverPreviewImageView);
                            // ------------------------------------

                        } else {
                            // Эта ситуация не должна происходить при RESULT_OK, но логируем на всякий случай
                            Log.e(TAG, "UCrop returned RESULT_OK, but output URI is null!");
                            Toast.makeText(this, R.string.error_cropping_image, Toast.LENGTH_SHORT).show();
                            croppedImageUri = null; // Сбрасываем URI
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        final Throwable cropError = UCrop.getError(result.getData());
                        Log.e(TAG, "UCrop error: ", cropError);
                        Toast.makeText(this, getString(R.string.error_cropping_image) + (cropError != null ? ": " + cropError.getMessage() : ""), Toast.LENGTH_LONG).show();
                        croppedImageUri = null; // Сбрасываем URI при ошибке
                    } else {
                        // Пользователь отменил UCrop
                        Log.w(TAG, "UCrop cancelled or failed. ResultCode: " + result.getResultCode());
                        // croppedImageUri остается как был (возможно, null или предыдущее значение)
                    }
                });
    }

    // Настройка слушателей
    private void setupListeners() {
        selectImageButton.setOnClickListener(v -> checkPermissionAndPickImage());
        coverPreviewImageView.setOnClickListener(v -> checkPermissionAndPickImage());
        createEventButton.setOnClickListener(v -> attemptCreateEvent());
        dateTimeTextView.setOnClickListener(v -> showDatePickerDialog());
    }

    // Установка фильтров длины
    private void addInputFilters() {
        titleEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
        descriptionEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(500)});
        priceEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
        sourceUrlEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(100)});
    }

    // Запуск загрузки начальных данных
    private void fetchInitialData() {
        showLoading(true);
        tagsLoaded = false;
        locationsLoaded = false;
        fetchTags();
        fetchLocations();
    }

    private void showDatePickerDialog() {
        int year = selectedDateTimeCalendar.get(Calendar.YEAR);
        int month = selectedDateTimeCalendar.get(Calendar.MONTH);
        int day = selectedDateTimeCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonthOfYear, selectedDayOfMonth) -> {
                    selectedDateTimeCalendar.set(Calendar.YEAR, selectedYear);
                    selectedDateTimeCalendar.set(Calendar.MONTH, selectedMonthOfYear);
                    selectedDateTimeCalendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                    showTimePickerDialog();
                }, year, month, day);

        // Установка минимальной даты (завтра)
        DatePicker datePicker = datePickerDialog.getDatePicker();
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR, 1);
        minDate.set(Calendar.HOUR_OF_DAY, 0); minDate.set(Calendar.MINUTE, 0); minDate.set(Calendar.SECOND, 0); minDate.set(Calendar.MILLISECOND, 0);
        datePicker.setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    // Новый метод для показа TimePickerDialog
    private void showTimePickerDialog() {
        // Используем текущее время из календаря или дефолтное (напр. 12:00)
        int hour = selectedDateTimeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTimeCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                // R.style.YourTimePickerTheme, // Опционально: тема
                (view, selectedHourOfDay, selectedMinute) -> {
                    // Сохраняем выбранное ВРЕМЯ в наш календарь
                    selectedDateTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHourOfDay);
                    selectedDateTimeCalendar.set(Calendar.MINUTE, selectedMinute);
                    selectedDateTimeCalendar.set(Calendar.SECOND, 0); // Сбрасываем секунды
                    selectedDateTimeCalendar.set(Calendar.MILLISECOND, 0);

                    selectedDateTimeCalendar.add(Calendar.MONTH, 1);
                    // Сохраняем итоговый Timestamp
                    selectedTimestamp = new Timestamp(selectedDateTimeCalendar.getTimeInMillis());

                    // Форматируем дату и время для отображения
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                    dateTimeTextView.setText(displayFormat.format(selectedDateTimeCalendar.getTime()));
                    dateTimeTextView.setError(null); // Убираем ошибку

                    Log.d(TAG, "Date and Time selected: " + displayFormat.format(selectedDateTimeCalendar.getTime()) + ", Timestamp: " + selectedTimestamp.getTime());

                }, hour, minute, android.text.format.DateFormat.is24HourFormat(this)); // Учитываем формат 12/24 часа

        timePickerDialog.show();
    }

    // Загрузка тегов
    private void fetchTags() {
        if (tagApiService == null) {
            Log.e(TAG, "TagApiService is null!");
            tagsLoadFinished(); // Считаем загрузку завершенной (с ошибкой)
            return;
        }
        tagApiService.getAllTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    availableTags = response.body();
                    Log.d(TAG, "Tags fetched: " + availableTags.size());
                    populateTags();
                } else {
                    Log.e(TAG, "Failed to fetch tags: " + response.code());
                    Toast.makeText(CreateEventActivity.this, R.string.error_loading_tags, Toast.LENGTH_SHORT).show();
                }
                tagsLoadFinished();
            }
            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Error fetching tags", t);
                Toast.makeText(CreateEventActivity.this, R.string.error_network_tags, Toast.LENGTH_SHORT).show();
                tagsLoadFinished();
            }
        });
    }

    // Загрузка локаций
    private void fetchLocations() {
        if (mapApiService == null) {
            Log.e(TAG, "MapApiService is null!");
            locationsLoadFinished(); // Считаем загрузку завершенной (с ошибкой)
            return;
        }
        mapApiService.getAllMapObjects().enqueue(new Callback<List<MapObjectDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<MapObjectDTO>> call, @NonNull Response<List<MapObjectDTO>> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    availableLocations = response.body();
                    Log.d(TAG, "Locations fetched: " + availableLocations.size());
                    populateLocations();
                } else {
                    Log.e(TAG, "Failed to fetch locations: " + response.code());
                    Toast.makeText(CreateEventActivity.this, R.string.error_loading_locations, Toast.LENGTH_SHORT).show();
                }
                locationsLoadFinished();
            }
            @Override
            public void onFailure(@NonNull Call<List<MapObjectDTO>> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Error fetching locations", t);
                Toast.makeText(CreateEventActivity.this, R.string.error_network_locations, Toast.LENGTH_SHORT).show();
                locationsLoadFinished();
            }
        });
    }

    // Проверка завершения загрузки
    private synchronized void checkLoadingComplete() {
        if (tagsLoaded && locationsLoaded && !isFinishing()) {
            dataLoadHandler.post(() -> showLoading(false));
        }
    }
    private void tagsLoadFinished() { tagsLoaded = true; checkLoadingComplete(); }
    private void locationsLoadFinished() { locationsLoaded = true; checkLoadingComplete(); }


    // Отображение тегов (используя CheckBox и FlexboxLayout)
    private void populateTags() {
        tagsFlexboxLayout.removeAllViews();
        selectedTagIds.clear();
        tagLimitErrorTextView.setVisibility(View.GONE);

        // Используем стиль AppTagCheckBoxStyle, определенный в styles.xml
        int tagStyle = R.style.AppTagCheckBoxStyle;

        for (Tag tag : availableTags) {
            CheckBox checkBoxTag = new CheckBox(this, null, 0, tagStyle);
            checkBoxTag.setText(tag.getName());

            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) (getResources().getDisplayMetrics().density * 4); // 4dp
            lp.setMargins(margin, margin, margin, margin);
            checkBoxTag.setLayoutParams(lp);

            checkBoxTag.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Long tagId = getTagIdByName(buttonView.getText().toString()); // Используем Long для remove
                if (tagId == -1L) return;

                if (isChecked) {
                    if (selectedTagIds.size() < MAX_TAGS) {
                        selectedTagIds.add(tagId);
                        tagLimitErrorTextView.setVisibility(View.GONE);
                    } else {
                        buttonView.setChecked(false);
                        tagLimitErrorTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, R.string.error_tag_limit_reached, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectedTagIds.remove(tagId); // Удаляем объект Long
                    tagLimitErrorTextView.setVisibility(View.GONE);
                }
                Log.d(TAG, "Selected Tag IDs: " + selectedTagIds);
            });
            tagsFlexboxLayout.addView(checkBoxTag);
        }
    }

    // Получение ID тега по имени
    private Long getTagIdByName(String name) { // Возвращаем Long
        for (Tag tag : availableTags) {
            if (tag.getName() != null && tag.getName().equalsIgnoreCase(name)) {
                // Сравниваем Long ID
                return tag.getId();
            }
        }
        return -1L; // Используем -1L для ненайденного Long
    }

    // Отображение локаций в AutoCompleteTextView
    private void populateLocations() {
        List<String> locationNames = new ArrayList<>();
        locationNameToIdMap.clear();
        selectedLocationId = null;
        locationAutoComplete.setText("");

        for (MapObjectDTO mapObject : availableLocations) {
            // Используем title как имя локации (убедитесь, что title содержит имя)
            if (mapObject.getId() != null && mapObject.getTitle() != null) {
                locationNames.add(mapObject.getTitle());
                locationNameToIdMap.put(mapObject.getTitle(), mapObject.getId());
            } else {
                Log.w(TAG, "Skipping location due to null id or title: " + mapObject);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                locationNames);
        locationAutoComplete.setAdapter(adapter);

        locationAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = adapter.getItem(position); // Получаем выбранное имя
            if(selectedName != null) {
                selectedLocationId = locationNameToIdMap.get(selectedName);
                Log.d(TAG, "Location selected: Name=" + selectedName + ", ID=" + selectedLocationId);
                // Скрываем клавиатуру
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        });
    }

    // --- Логика выбора и обрезки изображения ---
    private void checkPermissionAndPickImage() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else {
            Log.d(TAG, "Requesting permission: " + permission);
            requestPermissionLauncher.launch(permission);
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void launchUCrop(@NonNull Uri sourceUri) {
        // Создаем уникальное имя файла для обрезанного изображения в кеше
        String destinationFileName = UCROP_FILE_NAME_PREFIX + UUID.randomUUID().toString() + ".jpg";
        File destinationFile = new File(getCacheDir(), destinationFileName);
        Uri destinationUri = Uri.fromFile(destinationFile); // URI для файла в кеше

        // Настраиваем опции UCrop
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(85);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        // Установка цветов UCrop под вашу тему
        options.setToolbarColor(ContextCompat.getColor(this, R.color.bars));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.startColor)); // Или R.color.bars
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimaryAction));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColorModern)); // Ваш фон

        // --- ИСПРАВЛЕНИЕ ---
        // 1. Создаем запрос UCrop
        UCrop ucropRequest = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // Соотношение 1:1
                .withMaxResultSize(1080, 1080) // Максимальный размер результата
                .withOptions(options);

        // 2. Получаем Intent из запроса UCrop
        Intent ucropIntent = ucropRequest.getIntent(this);

        // 3. Запускаем полученный Intent с помощью нашего ActivityResultLauncher
        Log.d(TAG, "Launching UCrop Intent...");
        uCropLauncher.launch(ucropIntent);
        // --- КОНЕЦ ИСПРАВЛЕНИЯ ---

        // Старый неверный вызов:
        // UCrop.of(sourceUri, destinationUri)
        //         .withAspectRatio(1, 1)
        //         .withMaxResultSize(1080, 1080)
        //         .withOptions(options)
        //         .start(this, uCropLauncher); // <-- Эта строка вызывала ошибку
    }


    // --- Логика создания события ---
    private void attemptCreateEvent() {
        if (!validateInput()) {
            Log.w(TAG, "Validation failed.");
            return;
        }
        showLoading(true);
        createEventButton.setEnabled(false);

        if (croppedImageUri != null) {
            uploadImageAndProceed();
        } else {
            Log.e(TAG, "Attempting to create event without required image!");
            Toast.makeText(this, R.string.error_select_image, Toast.LENGTH_SHORT).show();
            showLoading(false);
            createEventButton.setEnabled(true);
        }
    }

    // Валидация полей
    private boolean validateInput() {
        boolean isValid = true;

        // Title
        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleEditText.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            titleEditText.setError(null);
        }

        // Description
        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionEditText.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            descriptionEditText.setError(null);
        }

        // Date
        if (selectedTimestamp == null) {
            dateTimeTextView.setError(getString(R.string.error_select_date_time)); // Новая строка ошибки
            Toast.makeText(this, R.string.error_select_date_time, Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            // Проверка на минимальную дату/время (завтра 00:00)
            Calendar minDateTimeCheck = Calendar.getInstance();
            minDateTimeCheck.add(Calendar.DAY_OF_YEAR, 1);
            minDateTimeCheck.set(Calendar.HOUR_OF_DAY, 0); minDateTimeCheck.set(Calendar.MINUTE, 0);
            minDateTimeCheck.set(Calendar.SECOND, 0); minDateTimeCheck.set(Calendar.MILLISECOND, 0);

            if (selectedTimestamp.before(new Timestamp(minDateTimeCheck.getTimeInMillis()))) {
                dateTimeTextView.setError(getString(R.string.error_date_time_too_early)); // Новая строка ошибки
                Toast.makeText(this, R.string.error_date_time_too_early, Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                dateTimeTextView.setError(null);
            }
        }

        // Price
        String priceStr = priceEditText.getText().toString();
        if (!priceStr.isEmpty()) {
            try {
                int price = Integer.parseInt(priceStr);
                if (price < 0 || price > 99999) {
                    priceEditText.setError(getString(R.string.error_price_range));
                    isValid = false;
                } else {
                    priceEditText.setError(null);
                }
            } catch (NumberFormatException e) {
                priceEditText.setError(getString(R.string.error_invalid_number));
                isValid = false;
            }
        } else {
            priceEditText.setError(null); // Допустим, цена необязательна
        }

        // Source URL
        String urlStr = sourceUrlEditText.getText().toString().trim();
        if (urlStr.isEmpty()) {
            sourceUrlEditText.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!Patterns.WEB_URL.matcher(urlStr).matches()) {
            sourceUrlEditText.setError(getString(R.string.error_invalid_url));
            isValid = false;
        } else {
            sourceUrlEditText.setError(null);
        }

        // Tags (опционально)
        // if (selectedTagIds.isEmpty()) {
        //     Toast.makeText(this, R.string.error_select_tags, Toast.LENGTH_SHORT).show();
        //     isValid = false;
        // }

        // Location
        if (selectedLocationId == null) {
            locationAutoComplete.setError(getString(R.string.error_select_location));
            Toast.makeText(this, R.string.error_select_location, Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            locationAutoComplete.setError(null);
        }

        // Image
        if (croppedImageUri == null) {
            Toast.makeText(this, R.string.error_select_image, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // Шаг 1: Загрузка изображения
    private void uploadImageAndProceed() {
        Log.d(TAG, "Starting image upload for URI: " + croppedImageUri);
        File imageFile = uriToFile(croppedImageUri);

        if (imageFile == null) {
            Log.e(TAG, "Failed to convert cropped URI to File.");
            Toast.makeText(this, R.string.error_processing_image, Toast.LENGTH_SHORT).show();
            showLoading(false);
            createEventButton.setEnabled(true);
            return;
        }

        RequestBody requestFile = null;
        try {
            // Определяем MIME тип
            String mimeType = getContentResolver().getType(croppedImageUri);
            if (mimeType == null) {
                mimeType = "image/jpeg"; // Тип по умолчанию
                Log.w(TAG, "Could not determine MIME type, defaulting to " + mimeType);
            }
            Log.d(TAG, "Creating RequestBody with MIME type: " + mimeType);
            requestFile = RequestBody.create(MediaType.parse(mimeType), imageFile);
        } catch (Exception e) {
            Log.e(TAG, "Error creating RequestBody", e);
            Toast.makeText(this, R.string.error_processing_image, Toast.LENGTH_SHORT).show();
            showLoading(false);
            createEventButton.setEnabled(true);
            return;
        }


        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        if (eventApiService == null) {
            Log.e(TAG, "EventApiService is null before uploading image!");
            showLoading(false);
            createEventButton.setEnabled(true);
            return;
        }

        eventApiService.uploadEventImage(body).enqueue(new Callback<FileUploadedDTO>() {
            @Override
            public void onResponse(@NonNull Call<FileUploadedDTO> call, @NonNull Response<FileUploadedDTO> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    uploadedCoverFileId = response.body().getFileId();
                    Log.d(TAG, "Image uploaded successfully. File ID: " + uploadedCoverFileId);
                    createEventOnServer(); // Переходим к созданию события
                } else {
                    Log.e(TAG, "Image upload failed: " + response.code() + " - " + response.message());
                    try {
                        Log.e(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) { Log.e(TAG, "Error reading error body", e); }
                    Toast.makeText(CreateEventActivity.this, R.string.error_uploading_image, Toast.LENGTH_LONG).show();
                    showLoading(false);
                    createEventButton.setEnabled(true);
                }
            }
            @Override
            public void onFailure(@NonNull Call<FileUploadedDTO> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Image upload network error", t);
                Toast.makeText(CreateEventActivity.this, R.string.error_network_upload, Toast.LENGTH_LONG).show();
                showLoading(false);
                createEventButton.setEnabled(true);
            }
        });
    }

    // Вспомогательный метод для конвертации URI в File
    @Nullable
    private File uriToFile(Uri uri) {
        File file = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String tempFileName = "upload_" + System.currentTimeMillis();
            // Используем filesDir вместо cacheDir, если файлы должны сохраняться дольше
            File outputDir = getFilesDir(); // Или getCacheDir()
            file = File.createTempFile(tempFileName, ".tmp", outputDir);
            // file.deleteOnExit(); // Не используем deleteOnExit, если файл нужен сразу после

            inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream for URI: " + uri);
                return null;
            }
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            Log.d(TAG, "File created from URI: " + file.getAbsolutePath() + ", Size: " + file.length());
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Error converting URI to File", e);
            if (file != null && file.exists()) {
                file.delete(); // Удаляем частично созданный файл при ошибке
            }
            return null;
        } finally {
            try { if (inputStream != null) inputStream.close(); } catch (IOException e) { Log.e(TAG, "Error closing input stream", e); }
            try { if (outputStream != null) outputStream.close(); } catch (IOException e) { Log.e(TAG, "Error closing output stream", e); }
        }
    }

    // Шаг 2: Создание события на сервере
    private void createEventOnServer() {
        Log.d(TAG, "Preparing EventUploadDTO...");

        EventUploadDTO dto = new EventUploadDTO();
        dto.setTitle(titleEditText.getText().toString().trim());
        dto.setDescription(descriptionEditText.getText().toString().trim());
        dto.setPrice(priceEditText.getText().toString().trim());
        dto.setSourceUrl(sourceUrlEditText.getText().toString().trim());
        dto.setTags(new ArrayList<>(selectedTagIds));
        dto.setLocationId(selectedLocationId);
        dto.setCoverFileId(uploadedCoverFileId);
        dto.setDate(selectedTimestamp);

        Log.d(TAG, "Sending EventUploadDTO: Title=" + dto.getTitle() + ", Date=" + dto.getDate() + ", LocID=" + dto.getLocationId() + ", Tags=" + dto.getTags() + ", CoverID=" + dto.getCoverFileId());

        if (eventApiService == null) { /* ... обработка ошибки ... */ return; }

        eventApiService.uploadEvent(dto).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (isFinishing()) return;
                showLoading(false);
                createEventButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Event created successfully! ID: " + response.body().getId());
                    Toast.makeText(CreateEventActivity.this, R.string.event_created_successfully, Toast.LENGTH_LONG).show();
                    finish(); // Успех - закрываем Activity
                } else {
                    Log.e(TAG, "Failed to create event: " + response.code() + " - " + response.message());
                    try { Log.e(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "null")); }
                    catch (Exception e) { Log.e(TAG, "Error reading error body", e); }
                    Toast.makeText(CreateEventActivity.this, R.string.error_creating_event, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Network error creating event", t);
                showLoading(false);
                createEventButton.setEnabled(true);
                Toast.makeText(CreateEventActivity.this, R.string.error_network_creating_event, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Управление видимостью ProgressBar и блокировкой UI
    private void showLoading(boolean isLoading) {
        if (isFinishing()) return;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        titleEditText.setEnabled(!isLoading);
        descriptionEditText.setEnabled(!isLoading);
        dateTimeTextView.setEnabled(!isLoading);
        priceEditText.setEnabled(!isLoading);
        sourceUrlEditText.setEnabled(!isLoading);
        locationAutoComplete.setEnabled(!isLoading);
        selectImageButton.setEnabled(!isLoading);
        coverPreviewImageView.setEnabled(!isLoading);

        for (int i = 0; i < tagsFlexboxLayout.getChildCount(); i++) {
            View child = tagsFlexboxLayout.getChildAt(i);
            child.setEnabled(!isLoading);
        }
        // Кнопка создания блокируется/разблокируется отдельно
    }

    // Обработка нажатия кнопки "назад" в тулбаре
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Можно добавить диалог подтверждения, если есть несохраненные данные
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
