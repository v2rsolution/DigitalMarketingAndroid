/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;
import com.wscubetech.seovideotutorials.utils.MyValidations;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader, txtUpdate;
    ImageView imgProfile;

    //Text Input and EditText
    TextInputLayout inpName, inpEmail;
    EditText etName, etEmail;
    Dialog progress;
    DialogMsg dialogMsg;

    UserModel userModel;

    int flagCameraGallery = 0;
    File imageFile;
    Uri fileUri;
    static final String FOLDER_PROFILE = "Digital Marketing";

    Bitmap myBmp;

    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        userModel = new UserDetailsPrefs(this).getUserModel();

        toolbarOperation();
        onClickListeners();
        textChangeListeners();
        setUserDetailsUi();

        progress = new MyProgressDialog(this).getDialog();
        dialogMsg = new DialogMsg(this);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        inpName = (TextInputLayout) findViewById(R.id.inpName);
        inpEmail = (TextInputLayout) findViewById(R.id.inpEmail);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        txtHeader.setText("Edit Profile");
    }

    private void onClickListeners() {
        txtUpdate.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
    }

    private void textChangeListeners() {
        ValidationsListeners listeners = new ValidationsListeners(this);
        etName.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpName));
        etEmail.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpEmail));
    }

    private void setUserDetailsUi() {
        etName.setText(userModel.getUserName());
        etEmail.setText(userModel.getUserEmail());
        etName.setSelection(userModel.getUserName().trim().length());

        new LoadUserImage().loadImageInImageView(this, Urls.imageUrl + userModel.getUserImage(), imgProfile);
    }


    private void loadImageInImageView(File file) {
        try {
            Log.v("FileimageFile", file.getAbsolutePath());
            Glide.with(this).load(file).asBitmap().placeholder(R.drawable.circle_user).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new BitmapImageViewTarget(imgProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imgProfile.setImageDrawable(circularBitmapDrawable);
                }
            });
        } catch (Exception e) {
            Log.v("ExceptionImageShow", "" + e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtUpdate:
                String strName;
                strName = etName.getText().toString().trim();
                MyValidations validations = new MyValidations(this);

                if (strName.length() < 1 || !validations.checkName(strName)) {
                    inputOperationError(inpName, etName, "Please enter a valid name");
                    return;
                }

                userModel.setUserName(strName);

                if (imageFile != null)
                    Log.v("ImageFilePath", imageFile.getAbsolutePath());

                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    if (imageFile != null)
                        okHttpUpdateProfile(userModel.getUserId(), userModel.getUserName(), imageFile.getAbsolutePath());
                    else
                        okHttpUpdateProfile(userModel.getUserId(), userModel.getUserName(), null);
                } else {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }

                break;

            case R.id.imgProfile:
                marshmallowCheckAndPick();
                break;
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    public void marshmallowCheckAndPick() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            } else {
                profilePhotoPick();
            }
        } else {
            profilePhotoPick();
        }
    }

    private void profilePhotoPick() {
        final Dialog dialog = new MyDialog(this).getMyDialog(R.layout.dialog_camera_gallery);
        LinearLayout linCamera, linGallery, linParent;
        TextView txtCamera, txtGallery;
        linCamera = (LinearLayout) dialog.findViewById(R.id.linCamera);
        linGallery = (LinearLayout) dialog.findViewById(R.id.linGallery);
        linParent = (LinearLayout) dialog.findViewById(R.id.linParent);

        linCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                flagCameraGallery = 1;
                cameraCapture();
            }
        });
        linGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                flagCameraGallery = 2;
                Intent goGallery = new Intent(Intent.ACTION_PICK);
                goGallery.setType("image/*");
                startActivityForResult(goGallery, 20);
            }
        });

        linParent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        dialog.show();
    }

    private void cameraCapture() {
        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory() + File.separator
                + FOLDER_PROFILE);

        if (!file.isDirectory()) {
            file.mkdirs();
        }

        imageFile = new File(Environment.getExternalStorageDirectory() + File.separator
                + FOLDER_PROFILE + "/my_profile" + ".jpg");

        fileUri = Uri.fromFile(imageFile);
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(chooserIntent, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(fileUri);
                    myBmp = getResizedBitmap(BitmapFactory.decodeStream(imageStream), 256, 256);
                    Log.d("img", fileUri.getPath());
                } catch (Exception e) {
                    Log.v("Exception", "" + e);
                }

            } else if (requestCode == 20) {
                Uri selectedImg = data.getData();
                InputStream myInputImage = null;
                try {
                    myInputImage = getContentResolver()
                            .openInputStream(selectedImg);
                    myBmp = getResizedBitmap(BitmapFactory.decodeStream(myInputImage), 256, 256);
                } catch (FileNotFoundException e) {
                    Log.v("ExceptionImage", "" + e);
                }


            }

            if (myBmp != null) {
                new SaveBitmapAsync(myBmp).execute();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    profilePhotoPick();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please turn on the permission for camera", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
                                   int bitmapHeight) {
        Bitmap bmp = Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight,
                true);
        return bmp;
    }

    private class SaveBitmapAsync extends AsyncTask<Void, Void, Void> {

        Bitmap bmp;

        public SaveBitmapAsync(Bitmap bmp) {
            this.bmp = bmp;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            saveBitmap(bmp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadImageInImageView(imageFile);
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + FOLDER_PROFILE;
        File fileDir = new File(dir);
        if (!fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + FOLDER_PROFILE + "/my_profile.jpg");
        Log.v("imageFile", "" + imageFile);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.v("ErrorFile", e + "");
        } catch (IOException e) {
            Log.v("ErrorIO", e + "");
        }
    }

    private void reduceImageFromCamera(Bitmap bmp) {
        if (bmp != null) {
            bmp = Bitmap.createScaledBitmap(bmp, 256, 256, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + FOLDER_PROFILE + "/my_profile.jpg");
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (Exception e) {
                Log.v("CameraReduceException", "" + e);
            }
        }
    }

    //[0]userId   [1]userName  [2]userImage
    private void okHttpUpdateProfile(String... params) {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("user_id", params[0]));
        arrayKeyValueModel.add(new KeyValueModel("user_name", params[1]));

        if (imageFile != null) {
            arrayKeyValueModel.add(new KeyValueModel("user_photo", params[2], true));
            Log.v("user_photo", params[2]);
        }
        OkHttpCalls calls = new OkHttpCalls(Urls.EDIT_PROFILE, arrayKeyValueModel);

        if (imageFile != null) {
            calls.initiateCallWithImageAndOtherValues(imageFile, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleResponse(true, "");
                }

                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    String response = res.body().string();
                    handleResponse(false, response);
                }
            });
        } else {
            calls.initiateCall(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleResponse(true, "");
                }

                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    String response = res.body().string();
                    handleResponse(false, response);
                }
            });
        }

    }

    private void handleResponse(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress.isShowing())
                    progress.dismiss();

                if (failed) {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                } else {
                    try {
                        Log.v("ResponseEditProfile", response);
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("status") == 1) {
                            dialogMsg.showSuccessDialog("Your profile is successfully updated", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogMsg.getDialog().dismiss();
                                    saveNewUserValues();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
                        Log.v("Exception", "" + e);
                    }

                }

            }
        });
    }

    private void saveNewUserValues() {
        UserDetailsPrefs prefs = new UserDetailsPrefs(this);
        prefs.setUserModel(userModel);
        finish();
    }
}
