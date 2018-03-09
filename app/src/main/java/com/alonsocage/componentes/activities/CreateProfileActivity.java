

package com.alonsocage.componentes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.alonsocage.componentes.R;
import com.alonsocage.componentes.managers.DatabaseHelper;
import com.alonsocage.componentes.managers.ProfileManager;
import com.alonsocage.componentes.managers.listeners.OnProfileCreatedListener;
import com.alonsocage.componentes.model.Profile;
import com.alonsocage.componentes.utils.PreferencesUtil;
import com.alonsocage.componentes.utils.ValidationUtil;

public class CreateProfileActivity extends PickImageActivity implements OnProfileCreatedListener {
    private static final String TAG = CreateProfileActivity.class.getSimpleName();
    public static final String LARGE_IMAGE_URL_EXTRA_KEY = "CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY";


    private EditText nameEditText;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Profile profile;
    private String largeAvatarURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        largeAvatarURL = getIntent().getStringExtra(LARGE_IMAGE_URL_EXTRA_KEY);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profile = ProfileManager.getInstance(this).buildProfile(firebaseUser, largeAvatarURL);

        nameEditText.setText(profile.getUsername());

        if (profile.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(profile.getPhotoUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .error(R.drawable.ic_stub)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_stub);
        }

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
    }

    @Override
    public ProgressBar getProgressView() {
        return progressBar;
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void onImagePikedAction() {
        startCropImageActivity();
    }

    private void attemptCreateProfile() {


        nameEditText.setError(null);


        String name = nameEditText.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        } else if (!ValidationUtil.isNameValid(name)) {
            nameEditText.setError(getString(R.string.error_profile_name_length));
            focusView = nameEditText;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            showProgress();
            profile.setUsername(name);
            ProfileManager.getInstance(this).createOrUpdateProfile(profile, imageUri, this);
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        handleCropImageResult(requestCode, resultCode, data);
    }

    @Override
    public void onProfileCreated(boolean success) {
        hideProgress();

        if (success) {
            finish();
            PreferencesUtil.setProfileCreated(this, success);
            DatabaseHelper.getInstance(CreateProfileActivity.this.getApplicationContext())
                    .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), profile.getId());
        } else {
            showSnackBar(R.string.error_fail_create_profile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.continueButton:
                if (hasInternetConnection()) {
                    attemptCreateProfile();
                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
