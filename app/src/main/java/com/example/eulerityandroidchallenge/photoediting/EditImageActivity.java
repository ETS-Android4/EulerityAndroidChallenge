package com.example.eulerityandroidchallenge.photoediting;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.example.eulerityandroidchallenge.App;
import com.example.eulerityandroidchallenge.R;
import com.example.eulerityandroidchallenge.databinding.ActivityEditImageBinding;
import com.example.eulerityandroidchallenge.models.ImageObject;
import com.example.eulerityandroidchallenge.photoediting.base.BaseActivity;
import com.example.eulerityandroidchallenge.photoediting.filters.FilterListener;
import com.example.eulerityandroidchallenge.photoediting.filters.FilterViewAdapter;
import com.example.eulerityandroidchallenge.photoediting.tools.EditingToolsAdapter;
import com.example.eulerityandroidchallenge.photoediting.tools.ToolType;
import com.example.eulerityandroidchallenge.viewmodels.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.File;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

/**
 *      This Activity is where users edit their selected image
 */

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        ShapeBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE";

    public static final int UPLOAD_CODE = 0;
    public static final int SAVE_CODE = 1;

    ActivityEditImageBinding binding;

    PhotoEditor mPhotoEditor;
    private ShapeBSFragment mShapeBSFragment;
    private ShapeBuilder mShapeBuilder;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private final EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this, this);
    private final FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    ImageObject imageObject;

    UploadStatus status;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_image);

        initViews();

        handleIntentImage(binding.photoEditorView.getSource());

        PropertiesBSFragment mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mShapeBSFragment = new ShapeBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mShapeBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvConstraintTools.setLayoutManager(llmTools);
        binding.rvConstraintTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFilterView.setLayoutManager(llmFilters);
        binding.rvFilterView.setAdapter(mFilterViewAdapter);

        boolean pinchTextScalable = getIntent().getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true);

        mPhotoEditor = new PhotoEditor.Builder(this, binding.photoEditorView)
                .setPinchTextScalable(pinchTextScalable)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

    }

    private void handleIntentImage (ImageView source) {
        Intent intent = this.getIntent();
        imageObject = intent.getParcelableExtra("imageObject");
        Picasso.get().load(imageObject.getUrl()).into(source);
    }

    private void initViews () {
        binding.imgUndo.setOnClickListener(this);
        binding.imgRedo.setOnClickListener(this);
        binding.imgSave.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);
        binding.imgUpload.setOnClickListener(this);
    }

    @Override
    public void onEditTextChangeListener (final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, newColorCode) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(newColorCode);

            mPhotoEditor.editText(rootView, inputText, styleBuilder);
            mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_text));
            mEditingToolsAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onAddViewListener (ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener (ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener (ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener (ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onTouchSourceImage (MotionEvent event) {
        Log.d(TAG, "onTouchView() called with: event = [" + event + "]");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;
            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;
            case R.id.imgSave:
                saveImage();
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgUpload:
                uploadImage();
                break;
            default:
                break;
        }
    }

    //This method executes when the user hits the upload button, checking permissions before calling UploadStatus to upload the image
    //Converts the contents of the PhotoEditor into a Bitmap and sends it to UploadStatus
    private void uploadImage () {
        final boolean hasStoragePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        if (hasStoragePermission) {
            showSnackbar(getResources().getString(R.string.upload_progress_message));
            mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    status = new ViewModelProvider(EditImageActivity.this).get(UploadStatus.class);
                    binding.photoEditorView.getSource().setImageBitmap(saveBitmap);
                    status.init(imageObject, saveBitmap);
                    status.getUploadStatus().observe(EditImageActivity.this, aBoolean -> {
                        if (aBoolean) {
                            showSnackbar(getResources().getString(R.string.upload_success_message));
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditImageActivity.this, "Failed to generate bitmap!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, UPLOAD_CODE);
        }
    }

    //Converts the contents of the PhotoEditor as a Bitmap before saving it as a File in storage
    private void saveImage () {
        final String fileName = System.currentTimeMillis() + ".jpeg";
        final boolean hasStoragePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        if (hasStoragePermission) {
            mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    File file = App.bitmapToFile(saveBitmap, fileName);
                    Picasso.get().load(file).into(binding.photoEditorView.getSource());
                    showSnackbar(getResources().getString(R.string.save_success_message));
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditImageActivity.this, "Failed to generate bitmap!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, SAVE_CODE);
        }
    }

    @Override
    public void onColorChanged (int colorCode) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
    }

    @Override
    public void onOpacityChanged (int opacity) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
    }

    @Override
    public void onShapeSizeChanged (int shapeSize) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize));
    }

    @Override
    public void onShapePicked (ShapeType shapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
    }

    @Override
    public void onEmojiClick (String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_emoji));
        mEditingToolsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStickerClick (Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_sticker));
        mEditingToolsAdapter.notifyDataSetChanged();
    }

    @Override
    public void isPermissionGranted (boolean isGranted, String permission, int requestCode) {
        if (isGranted) {
            if (requestCode == SAVE_CODE) {
                saveImage();
            } else {
                uploadImage();
            }
        }
    }

    private void showSaveDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton(App.getResourcesStatic().getString(R.string.edit_image_save), (dialog, which) -> saveImage());
        builder.setNegativeButton(App.getResourcesStatic().getString(R.string.edit_image_cancel), (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton(App.getResourcesStatic().getString(R.string.edit_image_discard), (dialog, which) -> finish());
        builder.create().show();

    }

    @Override
    public void onFilterSelected (PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected (ToolType toolType) {

        switch (toolType) {
            case SHAPE:
                mPhotoEditor.setBrushDrawingMode(true);
                mShapeBuilder = new ShapeBuilder();
                mPhotoEditor.setShape(mShapeBuilder);
                mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_shape));
                showBottomSheetDialogFragment(mShapeBSFragment);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);

                    mPhotoEditor.addText(inputText, styleBuilder);
                    mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_text));
                    mEditingToolsAdapter.notifyDataSetChanged();
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_eraser));
                break;
            case FILTER:
                mEditingToolsAdapter.setHighlightedTool(getResources().getString(R.string.label_filter));
                showFilter(true);
                break;
            case EMOJI:
                showBottomSheetDialogFragment(mEmojiBSFragment);
                break;
            case STICKER:
                showBottomSheetDialogFragment(mStickerBSFragment);
                break;
        }
        mEditingToolsAdapter.notifyDataSetChanged();
    }

    private void showBottomSheetDialogFragment (BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }


    void showFilter (boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(binding.rootView);

        if (isVisible) {
            mConstraintSet.clear(binding.rvFilterView.getId(), ConstraintSet.START);
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(binding.rvFilterView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(binding.rootView, changeBounds);

        mConstraintSet.applyTo(binding.rootView);
    }

    @Override
    public void onBackPressed () {
        if (mIsFilterVisible) {
            showFilter(false);
            mEditingToolsAdapter.setHighlightedTool("");
            mEditingToolsAdapter.notifyDataSetChanged();
        }
        else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }
}
