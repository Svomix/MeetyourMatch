package com.javanostra.meetyourmatch.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.PagedResponse;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.TokenHelper;
import com.javanostra.meetyourmatch.persistance.entity.ChatUserDTO;
import com.javanostra.meetyourmatch.persistance.entity.Relation;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRelationDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AccountDetailsActivity";
    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_USERNAME = "USERNAME"; 

    private ImageView profileAvatarHeader;
    private TextView usernameTextView, cityTextView, genderTextView, descriptionTextView, descriptionLabel, userOnline;
    private AppCompatButton friendActionButton, startChatButton, blockActionButton;
    private AppCompatButton acceptRequestButton, rejectRequestButton;
    private ProgressBar progressBar;
    private NestedScrollView descriptionScrollView;
    private Toolbar toolbar;
    private View divider;
    private LinearLayout metadataLayout;
    private LinearLayout buttonsContainer;
    private LinearLayout friendRequestActionsLayout; 

    private UserApiService userApiService;
    private AccountApiService accountApiService;

    private UserProfileDTO currentUserProfile;
    private UserRelationDTO currentUserRelationDTO;
    private boolean mHasOutgoingRequestToTarget = false; 
    private boolean mHasIncomingRequestFromTarget = false; 
    private boolean mRelationDataLoaded = false; 

    private long targetUserId = -1L; 
    private String targetUsername; 
    private String targetAvatarPath;
    private String currentAppUserId; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        targetUserId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        
        targetUsername = getIntent().getStringExtra(EXTRA_USERNAME);

        if (targetUserId == -1L) {
            handleFatalError(getString(R.string.error_no_user_specified));
            return;
        }

        
        currentAppUserId = TokenHelper.extractUsernameFromToken(new CookieManager(this).getCookie());
        if (currentAppUserId == null) {
            handleFatalError(getString(R.string.error_cannot_determine_user));
            return;
        }
        Log.d(TAG, "Current app user (from token): " + currentAppUserId);
        Log.d(TAG, "Viewing profile for targetUserId: " + targetUserId + ", targetUsername (from intent): " + targetUsername);


        initViews();
        setupToolbar();
        initApiServices();
        setupButtonClickListeners();

        
        fetchUserDetailsById(targetUserId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (currentUserProfile != null && targetUserId != -1L) {
            Log.d(TAG, "onResume: Refreshing relations and requests status for user ID: " + targetUserId);
            startRelationAndRequestsFetch(targetUserId);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_account_details);
        profileAvatarHeader = findViewById(R.id.profileAvatarHeader);
        usernameTextView = findViewById(R.id.profile_username);
        cityTextView = findViewById(R.id.profile_city);
        genderTextView = findViewById(R.id.profile_gender);
        descriptionTextView = findViewById(R.id.profile_description);
        descriptionLabel = findViewById(R.id.profile_description_label);
        friendActionButton = findViewById(R.id.button_friend_action);
        startChatButton = findViewById(R.id.button_start_chat);
        blockActionButton = findViewById(R.id.button_block_action);
        acceptRequestButton = findViewById(R.id.button_accept_request);
        rejectRequestButton = findViewById(R.id.button_reject_request);
        progressBar = findViewById(R.id.progressBar_account_details);
        descriptionScrollView = findViewById(R.id.descriptionScrollView);
        divider = findViewById(R.id.divider);
        metadataLayout = findViewById(R.id.metadataLayout);
        buttonsContainer = findViewById(R.id.buttons_container);
        friendRequestActionsLayout = findViewById(R.id.friend_request_actions);
        userOnline = findViewById(R.id.user_online);

        
        setContentVisibility(false);
        setAllButtonsEnabled(false);
    }

    private void setContentVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        
        metadataLayout.setVisibility(visibility);
        divider.setVisibility(visibility);
        descriptionScrollView.setVisibility(visibility);
        
        buttonsContainer.setVisibility(visibility);
    }

    private void setAllButtonsEnabled(boolean enabled) {
        setButtonEnabled(friendActionButton, enabled);
        setButtonEnabled(startChatButton, enabled);
        setButtonEnabled(blockActionButton, enabled);
        setButtonEnabled(acceptRequestButton, enabled);
        setButtonEnabled(rejectRequestButton, enabled);
    }

    private void setButtonEnabled(AppCompatButton button, boolean enabled) {
        if (button != null) {
            button.setEnabled(enabled);
            
            button.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initApiServices() {
        userApiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);
        accountApiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);
    }

    
    private void fetchUserDetailsById(long userId) {
        Log.d(TAG, "Fetching user details for ID: " + userId);
        progressBar.setVisibility(View.VISIBLE);
        setContentVisibility(false); 
        setAllButtonsEnabled(false); 

        userApiService.getUserById(userId).enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDTO> call, @NonNull Response<UserProfileDTO> response) {
                if (isDestroyed()) return; 

                if (response.isSuccessful() && response.body() != null) {
                    currentUserProfile = response.body();
                    
                    targetUserId = currentUserProfile.getId();
                    targetUsername = currentUserProfile.getUsername();
                    targetAvatarPath = currentUserProfile.getAvatarPath();
                    Log.d(TAG, "User details fetched successfully: ID=" + targetUserId + ", Username=" + targetUsername);
                    
                    populateBaseUI(currentUserProfile);
                    
                    startRelationAndRequestsFetch(targetUserId);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Failed to fetch user details: " + response.code() + " - " + response.message());
                    
                    showErrorState(getIntent().getStringExtra(EXTRA_USERNAME),
                            getString(R.string.error_loading_profile) + ": " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDTO> call, @NonNull Throwable t) {
                if (isDestroyed()) return;
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error fetching user details", t);
                showErrorState(getIntent().getStringExtra(EXTRA_USERNAME), getString(R.string.error_network));
            }
        });
    }

    
    private void startRelationAndRequestsFetch(long userId) {
        Log.d(TAG, "Starting fetch chain for relations and requests for user ID: " + userId);
        
        progressBar.setVisibility(View.VISIBLE);
        
        setContentVisibility(false);
        setInteractionButtonsEnabled(false); 

        
        currentUserRelationDTO = null;
        mHasOutgoingRequestToTarget = false;
        mHasIncomingRequestFromTarget = false;
        mRelationDataLoaded = false; 

        
        fetchUserRelationInternal(userId);
    }

    
    private void fetchUserRelationInternal(long relatedUserId) {
        Log.d(TAG, "1. Fetching base user relation for ID: " + relatedUserId);
        accountApiService.getUserRelation(relatedUserId).enqueue(new Callback<UserRelationDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserRelationDTO> call, @NonNull Response<UserRelationDTO> response) {
                if (isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    currentUserRelationDTO = response.body();
                    
                    Log.d(TAG, "1. Base relations fetched: myRelation=" + (currentUserRelationDTO.getMyRelation() != null ? currentUserRelationDTO.getMyRelation() : "null")
                            + ", userRelation=" + (currentUserRelationDTO.getUserRelation() != null ? currentUserRelationDTO.getUserRelation() : "null"));
                } else {
                    currentUserRelationDTO = null; 
                    Log.e(TAG, "1. Failed to fetch base relations: " + response.code() + " - " + response.message());
                    
                    
                }
                
                fetchOutgoingRequestsInternal(relatedUserId);
            }

            @Override
            public void onFailure(@NonNull Call<UserRelationDTO> call, @NonNull Throwable t) {
                if (isDestroyed()) return;
                currentUserRelationDTO = null; 
                Log.e(TAG, "1. Network error fetching base relations", t);
                
                
                fetchOutgoingRequestsInternal(relatedUserId);
            }
        });
    }

    
    private void fetchOutgoingRequestsInternal(long relatedUserId) {
        Log.d(TAG, "2. Fetching outgoing friend requests list to check for target ID: " + relatedUserId);
        int page = 1;
        int limit = 100;

        
        accountApiService.getOutgoingFriendRequests(page, limit, null).enqueue(new Callback<PagedResponse<UserProfileDTO>>() {
            @Override
            public void onResponse(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Response<PagedResponse<UserProfileDTO>> response) {
                if (isDestroyed()) return;
                mHasOutgoingRequestToTarget = false;
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    Log.d(TAG, "2. Outgoing requests list (page " + page + ") fetched. Size: " + response.body().getContent().size() + ". Checking for ID: " + relatedUserId);
                    for (UserProfileDTO user : response.body().getContent()) {
                        
                        
                        if (user.getId() == relatedUserId) {
                            mHasOutgoingRequestToTarget = true;
                            Log.i(TAG, "2. SUCCESS: Found outgoing request TO target user ID: " + relatedUserId);
                            break;
                        }
                    }
                    if (!mHasOutgoingRequestToTarget) {
                        Log.d(TAG, "2. No outgoing request found TO target user ID: " + relatedUserId);
                    }
                } else {
                    Log.e(TAG, "2. Failed to fetch outgoing requests list: " + response.code() + " - " + response.message());
                }
                fetchIncomingRequestsInternal(relatedUserId); 
            }

            @Override
            public void onFailure(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Throwable t) {
                if (isDestroyed()) return;
                mHasOutgoingRequestToTarget = false;
                Log.e(TAG, "2. Network error fetching outgoing requests list", t);
                fetchIncomingRequestsInternal(relatedUserId); 
            }
        });
    }

    
    private void fetchIncomingRequestsInternal(long relatedUserId) {
        Log.d(TAG, "3. Fetching ingoing friend requests list to check for target ID: " + relatedUserId);
        int page = 1;
        int limit = 100;

        
        accountApiService.getIngoingFriendRequests(page, limit, null).enqueue(new Callback<PagedResponse<UserProfileDTO>>() {
            @Override
            public void onResponse(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Response<PagedResponse<UserProfileDTO>> response) {
                if (isDestroyed()) return;
                mHasIncomingRequestFromTarget = false;
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    Log.d(TAG, "3. Ingoing requests list (page " + page + ") fetched. Size: " + response.body().getContent().size() + ". Checking for ID: " + relatedUserId);
                    for (UserProfileDTO user : response.body().getContent()) {
                        
                        
                        if (user.getId() == relatedUserId) {
                            mHasIncomingRequestFromTarget = true; 
                            Log.i(TAG, "3. SUCCESS: Found ingoing request FROM target user ID: " + relatedUserId);
                            break;
                        }
                    }
                    if (!mHasIncomingRequestFromTarget) {
                        Log.d(TAG, "3. No ingoing request found FROM target user ID: " + relatedUserId);
                    }
                } else {
                    Log.e(TAG, "3. Failed to fetch ingoing requests list: " + response.code() + " - " + response.message());
                }
                finalizeFetchAndUpdateUI(); 
            }

            @Override
            public void onFailure(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Throwable t) {
                if (isDestroyed()) return;
                mHasIncomingRequestFromTarget = false;
                Log.e(TAG, "3. Network error fetching ingoing requests list", t);
                finalizeFetchAndUpdateUI(); 
            }
        });
    }

    
    private void finalizeFetchAndUpdateUI() {
        Log.d(TAG, "Finalizing fetch chain. RelationDTO loaded: " + (currentUserRelationDTO != null)
                + ", OutgoingReqToTarget: " + mHasOutgoingRequestToTarget + ", IncomingReqFromTarget: " + mHasIncomingRequestFromTarget);
        progressBar.setVisibility(View.GONE); 
        setContentVisibility(true); 
        mRelationDataLoaded = true; 
        updateButtonStates(); 
    }

    
    private void populateBaseUI(UserProfileDTO user) {
        if (user == null) {
            Log.e(TAG, "populateBaseUI called with null user object.");
            
            usernameTextView.setText(getString(R.string.error_loading_profile));
            profileAvatarHeader.setImageResource(R.drawable.big_avatar); 
            return;
        }

        usernameTextView.setText(user.getUsername());

        if (user.isOnline()) {
            userOnline.setText("Online");
            userOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_circle_24, 0, 0, 0);
        }

        Glide.with(this)
                .load(user.getAvatarPath())
                .placeholder(R.drawable.big_avatar) 
                .error(R.drawable.big_avatar)     
                .transform(new CircleCrop())      
                .into(profileAvatarHeader);

        
        boolean cityVisible = user.getCity() != null && user.getCity().getName() != null && !user.getCity().getName().isEmpty();
        cityTextView.setText(cityVisible ? user.getCity().getName() : "");
        cityTextView.setVisibility(cityVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.icon_city).setVisibility(cityVisible ? View.VISIBLE : View.GONE);

        
        boolean genderVisible = user.getGender() != null && !user.getGender().isEmpty();
        genderTextView.setText(genderVisible ? user.getGender() : "");
        genderTextView.setVisibility(genderVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.icon_gender).setVisibility(genderVisible ? View.VISIBLE : View.GONE);

        
        boolean metadataVisible = cityVisible || genderVisible;
        metadataLayout.setVisibility(metadataVisible ? View.VISIBLE : View.GONE);

        
        boolean descriptionVisible = user.getDescription() != null && !user.getDescription().isEmpty();
        descriptionTextView.setText(descriptionVisible ? user.getDescription() : "");
        descriptionTextView.setVisibility(descriptionVisible ? View.VISIBLE : View.GONE);
        descriptionLabel.setVisibility(descriptionVisible ? View.VISIBLE : View.GONE);

        
        divider.setVisibility(metadataVisible || descriptionVisible ? View.VISIBLE : View.GONE);
    }

    
    private void updateButtonStates() {
        
        if (currentUserProfile == null || currentAppUserId == null || targetUserId == -1L) {
            Log.w(TAG, "updateButtonStates: Pre-conditions not met (profile, current user, or target ID missing). Hiding buttons.");
            buttonsContainer.setVisibility(View.GONE);
            setAllButtonsEnabled(false);
            return;
        }

        
        boolean isOwnProfile = currentAppUserId.equals(currentUserProfile.getUsername());
        Log.d(TAG, "updateButtonStates: Is own profile? " + isOwnProfile + " (currentAppUser: " + currentAppUserId + ", targetUser: " + currentUserProfile.getUsername() + ")");
        if (isOwnProfile) {
            Log.d(TAG, "updateButtonStates: Viewing own profile. Hiding interaction buttons.");
            buttonsContainer.setVisibility(View.GONE);
            return;
        } else {
            buttonsContainer.setVisibility(View.VISIBLE); 
        }

        
        if (!mRelationDataLoaded) {
            Log.w(TAG, "updateButtonStates: Relation data not yet fully loaded. Displaying placeholder buttons.");
            
            setInteractionButtonsEnabled(false);
            friendActionButton.setVisibility(View.VISIBLE);
            startChatButton.setVisibility(View.VISIBLE);
            blockActionButton.setVisibility(View.VISIBLE);
            friendRequestActionsLayout.setVisibility(View.GONE); 
            
            friendActionButton.setText(R.string.button_add_friend);
            blockActionButton.setText(R.string.button_block_user);
            setButtonDrawable(friendActionButton, R.drawable.baseline_person_add_24);
            setButtonDrawable(blockActionButton, R.drawable.baseline_lock_outline_24);
            updateButtonAppearance(friendActionButton, R.style.ModernButtonStyle, R.color.buttonTextColorPrimary);
            updateButtonAppearance(startChatButton, R.style.ModernButtonStyle, R.color.buttonTextColorPrimary);
            updateButtonAppearance(blockActionButton, R.style.ModernDangerButtonStyle, R.color.white);
            return; 
        }

        
        Log.d(TAG, "updateButtonStates: Relation data loaded. Proceeding with state determination.");

        
        Relation myRelation = Relation.NONE;
        Relation userRelation = Relation.NONE;
        boolean relationLoadError = (currentUserRelationDTO == null); 

        if (!relationLoadError) {
            myRelation = currentUserRelationDTO.getMyRelation() != null ? currentUserRelationDTO.getMyRelation() : Relation.NONE;
            userRelation = currentUserRelationDTO.getUserRelation() != null ? currentUserRelationDTO.getUserRelation() : Relation.NONE;
        } else {
            Log.w(TAG,"updateButtonStates: currentUserRelationDTO is null (load error). Assuming NONE/NONE for relations.");
        }

        
        boolean iBlockedUser = !relationLoadError && (myRelation == Relation.BLOCKED); 
        boolean iAmBlocked = !relationLoadError && (userRelation == Relation.BLOCKED); 
        boolean areFriends = !relationLoadError && (myRelation == Relation.FRIEND || userRelation == Relation.FRIEND); 
        boolean isInteractionBlocked = iAmBlocked || iBlockedUser; 

        
        Log.i(TAG, String.format("Updating Buttons State: TargetID=%d, TargetUser=%s | MyRelation=%s, UserRelation=%s | iBlockedUser=%b, iAmBlocked=%b | Friends=%b | OutgoingReq=%b, IncomingReq=%b | RelLoadError=%b",
                targetUserId, targetUsername,
                myRelation, userRelation,
                iBlockedUser, iAmBlocked, areFriends,
                mHasOutgoingRequestToTarget, mHasIncomingRequestFromTarget,
                relationLoadError));

        
        friendActionButton.setVisibility(View.GONE);
        friendRequestActionsLayout.setVisibility(View.GONE);
        startChatButton.setVisibility(View.GONE);
        blockActionButton.setVisibility(View.GONE);

        

        
        blockActionButton.setVisibility(View.VISIBLE);
        if (relationLoadError) {
            
            Log.w(TAG, "Block button disabled: Relation load error.");
            setButtonEnabled(blockActionButton, false);
            blockActionButton.setText(R.string.button_block_user);
            setButtonDrawable(blockActionButton, R.drawable.baseline_lock_outline_24);
            updateButtonAppearance(blockActionButton, R.style.ModernDangerButtonStyle, R.color.white);
        } else {
            setButtonEnabled(blockActionButton, true);
            if (iBlockedUser) {
                Log.d(TAG, "Block button state: Unblock");
                blockActionButton.setText(R.string.button_unblock_user);
                setButtonDrawable(blockActionButton, R.drawable.baseline_lock_open_24);
                updateButtonAppearance(blockActionButton, R.style.ModernSecondaryButtonStyle, R.color.white);
            } else {
                Log.d(TAG, "Block button state: Block");
                blockActionButton.setText(R.string.button_block_user);
                setButtonDrawable(blockActionButton, R.drawable.baseline_lock_outline_24);
                updateButtonAppearance(blockActionButton, R.style.ModernDangerButtonStyle, R.color.white);
            }
        }

        
        startChatButton.setVisibility(View.VISIBLE);
        
        if (!isInteractionBlocked && !relationLoadError) {
            Log.d(TAG, "Start Chat button enabled.");
            setButtonEnabled(startChatButton, true);
            updateButtonAppearance(startChatButton, R.style.ModernButtonStyle, R.color.textColorPrimaryModern);
        } else {
            Log.w(TAG, "Start Chat button disabled. Blocked: " + isInteractionBlocked + ", RelationError: " + relationLoadError);
            setButtonEnabled(startChatButton, false);
            updateButtonAppearance(startChatButton, R.style.ModernButtonStyle, R.color.textColorPrimaryModern); 
        }

        
        
        if (!isInteractionBlocked && !relationLoadError) {
            
            if (mHasIncomingRequestFromTarget) {
                Log.d(TAG, "Displaying Accept/Reject buttons (Incoming Request = true)");
                friendRequestActionsLayout.setVisibility(View.VISIBLE); 
                friendActionButton.setVisibility(View.GONE);         
                setButtonEnabled(acceptRequestButton, true);
                setButtonEnabled(rejectRequestButton, true);
                
            } else {
                
                Log.d(TAG, "Displaying single Friend Action button (Incoming Request = false)");
                friendRequestActionsLayout.setVisibility(View.GONE);  
                friendActionButton.setVisibility(View.VISIBLE); 
                setButtonEnabled(friendActionButton, true);

                if (areFriends) {
                    Log.d(TAG, "Friend Action state: Remove Friend");
                    friendActionButton.setText(R.string.button_remove_friend);
                    setButtonDrawable(friendActionButton, R.drawable.baseline_person_remove_24);
                    updateButtonAppearance(friendActionButton, R.style.ModernSecondaryButtonStyle, R.color.white);
                } else if (mHasOutgoingRequestToTarget) {
                    Log.d(TAG, "Friend Action state: Cancel Request");
                    friendActionButton.setText(R.string.button_cancel_request);
                    setButtonDrawable(friendActionButton, R.drawable.baseline_close_24); 
                    updateButtonAppearance(friendActionButton, R.style.ModernSecondaryButtonStyle, R.color.white);
                } else {
                    
                    Log.d(TAG, "Friend Action state: Add Friend");
                    friendActionButton.setText(R.string.button_add_friend);
                    setButtonDrawable(friendActionButton, R.drawable.baseline_person_add_24);
                    updateButtonAppearance(friendActionButton, R.style.ModernButtonStyle, R.color.textColorPrimaryModern);
                }
            }
        } else {
            
            Log.d(TAG, "Hiding all Friend Action/Request buttons. Blocked: " + isInteractionBlocked + ", RelationError: " + relationLoadError);
            friendActionButton.setVisibility(View.GONE);
            friendRequestActionsLayout.setVisibility(View.GONE);
            
            if (!isInteractionBlocked && relationLoadError) {
                Log.w(TAG, "Displaying disabled Add Friend button due to relation load error.");
                friendActionButton.setVisibility(View.VISIBLE);
                setButtonEnabled(friendActionButton, false);
                friendActionButton.setText(R.string.button_add_friend);
                setButtonDrawable(friendActionButton, R.drawable.baseline_person_add_24);
                updateButtonAppearance(friendActionButton, R.style.ModernButtonStyle, R.color.textColorPrimaryModern);
            }
        }
    }

    
    private void updateButtonAppearance(AppCompatButton button, int styleResId, int colorResId) {
        if (button == null) return;
        int color = ContextCompat.getColor(this, colorResId);
        button.setTextColor(color);

        
        Drawable[] drawables = button.getCompoundDrawablesRelative(); 
        Drawable startDrawable = drawables[0]; 
        if (startDrawable != null) {
            
            DrawableCompat.setTint(startDrawable.mutate(), color);
        }
    }


    
    private void setInteractionButtonsEnabled(boolean enabled) {
        setButtonEnabled(friendActionButton, enabled);
        setButtonEnabled(startChatButton, enabled);
        setButtonEnabled(blockActionButton, enabled);
        setButtonEnabled(acceptRequestButton, enabled);
        setButtonEnabled(rejectRequestButton, enabled);
    }


    
    private void setButtonDrawable(AppCompatButton button, int drawableResId) {
        if (button == null) return;
        
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableResId, 0, 0, 0);
        
    }

    
    private void setupButtonClickListeners() {
        friendActionButton.setOnClickListener(v -> handleFriendActionClick());
        startChatButton.setOnClickListener(v -> handleStartChatClick());
        blockActionButton.setOnClickListener(v -> handleBlockActionClick());
        acceptRequestButton.setOnClickListener(v -> handleAcceptRequestClick());
        rejectRequestButton.setOnClickListener(v -> handleRejectRequestClick());
    }

    

    
    private void handleFriendActionClick() {
        
        if (targetUserId == -1L || targetUsername == null) {
            Log.w(TAG, "handleFriendActionClick: Missing target user data."); return; }
        if (!mRelationDataLoaded) {
            Log.w(TAG, "handleFriendActionClick: Relation data not loaded yet.");
            Toast.makeText(this, getString(R.string.please_wait_loading), Toast.LENGTH_SHORT).show(); return;
        }

        
        boolean relationLoadError = (currentUserRelationDTO == null);
        Relation myRelation = Relation.NONE;
        Relation userRelation = Relation.NONE;
        if (!relationLoadError) {
            myRelation = currentUserRelationDTO.getMyRelation() != null ? currentUserRelationDTO.getMyRelation() : Relation.NONE;
            userRelation = currentUserRelationDTO.getUserRelation() != null ? currentUserRelationDTO.getUserRelation() : Relation.NONE;
        }
        boolean areFriends = !relationLoadError && (myRelation == Relation.FRIEND || userRelation == Relation.FRIEND);

        
        if (areFriends) {
            Log.d(TAG, "handleFriendActionClick: Action -> Remove Friend");
            confirmRemoveFriend();
        } else if (mHasOutgoingRequestToTarget) {
            
            Log.d(TAG, "handleFriendActionClick: Action -> Cancel Request");
            confirmCancelRequest();
        } else if (!relationLoadError && !mHasIncomingRequestFromTarget) {
            
            Log.d(TAG, "handleFriendActionClick: Action -> Add Friend");
            confirmAddFriend();
        } else {
            
            Log.w(TAG, "Friend Action button clicked in unexpected state.");
            if (relationLoadError) {
                Toast.makeText(this, getString(R.string.error_loading_relations_try_again), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            }
        }
    }

    
    private void handleBlockActionClick() {
        if (targetUserId == -1L || targetUsername == null) return;
        if (!mRelationDataLoaded) {
            Log.w(TAG, "handleBlockActionClick: Relation data not loaded yet.");
            Toast.makeText(this, getString(R.string.please_wait_loading), Toast.LENGTH_SHORT).show(); return;
        }
        if (currentUserRelationDTO == null) {
            Log.w(TAG, "handleBlockActionClick: Cannot determine block status, relation DTO is null.");
            Toast.makeText(this, getString(R.string.error_loading_relations_try_again), Toast.LENGTH_SHORT).show(); return;
        }

        Relation myRelation = currentUserRelationDTO.getMyRelation() != null ? currentUserRelationDTO.getMyRelation() : Relation.NONE;

        if (myRelation == Relation.BLOCKED) {
            Log.d(TAG, "handleBlockActionClick: Action -> Unblock User");
            confirmUnblockUser();
        } else {
            Log.d(TAG, "handleBlockActionClick: Action -> Block User");
            confirmBlockUser();
        }
    }

    
    private void handleAcceptRequestClick() {
        if (targetUserId == -1L || targetUsername == null) return;
        Log.d(TAG, "handleAcceptRequestClick: Action -> Accept Request from user " + targetUserId);
        confirmAcceptRequest();
    }

    
    private void handleRejectRequestClick() {
        if (targetUserId == -1L || targetUsername == null) return;
        Log.d(TAG, "handleRejectRequestClick: Action -> Reject Request from user " + targetUserId);
        confirmRejectRequest();
    }

    
    private void handleStartChatClick() {
        if (currentUserProfile == null || targetUsername == null) {
            Toast.makeText(this, R.string.error_user_data_not_loaded, Toast.LENGTH_SHORT).show(); return; }
        if (!mRelationDataLoaded) {
            Log.w(TAG, "handleStartChatClick: Relation data not loaded yet.");
            Toast.makeText(this, getString(R.string.please_wait_loading), Toast.LENGTH_SHORT).show(); return;
        }

        
        boolean relationLoadError = (currentUserRelationDTO == null);
        boolean iAmBlocked = false;
        boolean iBlockedUser = false;
        if (!relationLoadError) {
            Relation myRel = currentUserRelationDTO.getMyRelation() != null ? currentUserRelationDTO.getMyRelation() : Relation.NONE;
            Relation userRel = currentUserRelationDTO.getUserRelation() != null ? currentUserRelationDTO.getUserRelation() : Relation.NONE;
            iBlockedUser = (myRel == Relation.BLOCKED);
            iAmBlocked = (userRel == Relation.BLOCKED);
        }

        if (iBlockedUser || iAmBlocked) {
            Toast.makeText(this, R.string.error_cannot_chat_blocked, Toast.LENGTH_LONG).show(); return; }
        if (relationLoadError) { 
            Toast.makeText(this, getString(R.string.error_loading_relations_try_again), Toast.LENGTH_SHORT).show(); return; }

        Log.d(TAG, "Attempting to start chat with ID: " + targetUserId + ", Username: " + targetUsername);
        
        openChatMessagesActivity(targetUserId, targetUsername, targetAvatarPath);
    }


    

    private void confirmAddFriend() {
        showConfirmationDialog(
                getString(R.string.confirm_add_friend_title),
                getString(R.string.confirm_add_friend_message, targetUsername),
                (dialog, which) -> sendFriendRequest(targetUserId)
        );
    }

    private void confirmRemoveFriend() {
        showConfirmationDialog(
                getString(R.string.confirm_remove_friend_title),
                getString(R.string.confirm_remove_friend_message, targetUsername),
                (dialog, which) -> sendRemoveFriendRequest(targetUserId)
        );
    }

    private void confirmCancelRequest() {
        showConfirmationDialog(
                getString(R.string.confirm_cancel_request_title),
                getString(R.string.confirm_cancel_request_message, targetUsername),
                
                (dialog, which) -> sendDeleteFriendRequest(targetUserId, true) 
        );
    }

    private void confirmAcceptRequest() {
        showConfirmationDialog(
                getString(R.string.confirm_accept_request_title),
                getString(R.string.confirm_accept_request_message, targetUsername),
                (dialog, which) -> sendAcceptFriendRequest(targetUserId)
        );
    }

    private void confirmRejectRequest() {
        showConfirmationDialog(
                getString(R.string.confirm_reject_request_title),
                getString(R.string.confirm_reject_request_message, targetUsername),
                
                (dialog, which) -> sendDeleteFriendRequest(targetUserId, false) 
        );
    }

    private void confirmBlockUser() {
        showConfirmationDialog(
                getString(R.string.confirm_block_user_title),
                getString(R.string.confirm_block_user_message, targetUsername),
                (dialog, which) -> sendBlockUserRequest(targetUserId)
        );
    }

    private void confirmUnblockUser() {
        showConfirmationDialog(
                getString(R.string.confirm_unblock_user_title),
                getString(R.string.confirm_unblock_user_message, targetUsername),
                (dialog, which) -> sendUnblockUserRequest(targetUserId)
        );
    }


    

    private void sendFriendRequest(long userId) {
        performApiCall(
                accountApiService.sendFriendRequest(userId), 
                getString(R.string.toast_request_sent),
                "sendFriendRequest",
                friendActionButton
        );
    }

    private void sendRemoveFriendRequest(long friendId) {
        performApiCall(
                accountApiService.deleteFriend(friendId), 
                getString(R.string.toast_friend_removed),
                "deleteFriend",
                friendActionButton
        );
    }

    private void sendAcceptFriendRequest(long userId) {
        performApiCall(
                accountApiService.acceptFriendRequest(userId), 
                getString(R.string.toast_friend_added),
                "acceptFriendRequest",
                acceptRequestButton
        );
    }

    
    private void sendDeleteFriendRequest(long userId, boolean isCancelling) {
        performApiCall(
                accountApiService.deleteFriendRequest(userId), 
                isCancelling ? getString(R.string.toast_request_cancelled) : getString(R.string.toast_request_rejected),
                "deleteFriendRequest (isCancelling=" + isCancelling + ")",
                isCancelling ? friendActionButton : rejectRequestButton 
        );
    }

    private void sendBlockUserRequest(long userIdToBlock) {
        performApiCall(
                accountApiService.addBlocked(userIdToBlock), 
                getString(R.string.toast_user_blocked),
                "addBlocked",
                blockActionButton
        );
    }

    private void sendUnblockUserRequest(long userIdToUnblock) {
        performApiCall(
                accountApiService.deleteBlocked(userIdToUnblock), 
                getString(R.string.toast_user_unblocked),
                "deleteBlocked",
                blockActionButton
        );
    }

    
    private void performApiCall(Call<ResponseDTO> call, String successMessage, String logTag, @Nullable AppCompatButton associatedButton) {
        progressBar.setVisibility(View.VISIBLE);
        
        if (associatedButton != null) {
            Log.d(TAG, "Disabling associated button: " + associatedButton.getText());
            setButtonEnabled(associatedButton, false);
        } else {
            Log.d(TAG, "Disabling all interaction buttons.");
            setInteractionButtonsEnabled(false); 
        }

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (isDestroyed()) return;
                

                if (response.isSuccessful()) {
                    Log.i(TAG, logTag + " successful for user ID: " + targetUserId);
                    Toast.makeText(AccountDetailsActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    
                    startRelationAndRequestsFetch(targetUserId);
                } else {
                    progressBar.setVisibility(View.GONE); 
                    Log.e(TAG, logTag + " failed: " + response.code() + " - " + response.message());
                    
                    String errorMsg = response.message();
                    if (errorMsg == null || errorMsg.isEmpty()) { errorMsg = "Error code " + response.code(); }
                    Toast.makeText(AccountDetailsActivity.this, getString(R.string.error_generic) + ": " + errorMsg, Toast.LENGTH_LONG).show();

                    
                    if (associatedButton != null) {
                        Log.d(TAG, "Re-enabling associated button on failure: " + associatedButton.getText());
                        setButtonEnabled(associatedButton, true);
                    } else {
                        Log.d(TAG, "Re-enabling all interaction buttons on failure.");
                        setInteractionButtonsEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                if (isDestroyed()) return;
                progressBar.setVisibility(View.GONE); 
                Log.e(TAG, "Network error on " + logTag, t);
                Toast.makeText(AccountDetailsActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();

                
                if (associatedButton != null) {
                    Log.d(TAG, "Re-enabling associated button on network failure: " + associatedButton.getText());
                    setButtonEnabled(associatedButton, true);
                } else {
                    Log.d(TAG, "Re-enabling all interaction buttons on network failure.");
                    setInteractionButtonsEnabled(true);
                }
            }
        });
    }

    private void openChatMessagesActivity(long recipientId, String recipientUsername, String recipientAvatarPath) {
        Intent intent = new Intent(this, ChatMessagesActivity.class);
        intent.putExtra(ChatMessagesActivity.EXTRA_SENDER_ID, currentAppUserId);
        intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_ID, recipientUsername);
        intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_LONG_ID, recipientId);
        intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_USERNAME, recipientUsername);
        intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_IMAGE_URL, recipientAvatarPath);
        startActivity(intent);
        finish();
    }

    private void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveListener) 
                .setNegativeButton(android.R.string.no, null) 
                .show();
    }

    
    private void showErrorState(@Nullable String username, String message) {
        progressBar.setVisibility(View.GONE);
        setContentVisibility(true); 
        buttonsContainer.setVisibility(View.GONE); 

        
        usernameTextView.setText(username != null ? username : getString(R.string.error_unknown_user));
        profileAvatarHeader.setImageResource(R.drawable.big_avatar); 

        
        metadataLayout.setVisibility(View.GONE);

        
        descriptionLabel.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.VISIBLE);
        descriptionTextView.setText(message);
        descriptionTextView.setTextColor(ContextCompat.getColor(this, R.color.red)); 
    }

    
    private void handleFatalError(String message) {
        Log.e(TAG, "Fatal Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish(); 
    }

    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); 
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}