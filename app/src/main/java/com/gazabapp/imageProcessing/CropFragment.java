package com.gazabapp.imageProcessing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;
import java.io.IOException;


//@RuntimePermissions
public class CropFragment extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    String[] mode = {"FIT IMAGE", "SQUARE", "FREE", "3:4", "4:3", "9:16", "16:9", "7:5"};
    Uri uri;
    public Bitmap bitmaps;
    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;
    private LinearLayout mRootLayout;

    // Note: only the system can call this constructor by reflection.
    public CropFragment() {
    }

    public static CropFragment getInstance() {
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_main, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bind Views
        bindViews(view);
        // apply custom font
        FontUtils.setFont(mRootLayout);
//        mCropView.setDebug(true);
        // set bitmap to CropImageView

        if (getArguments() != null) {
            uri =  Uri.parse(getArguments().getString("uri"));
        }
        Log.e("uri...", "h" + uri.toString());

        try {
            bitmaps = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Spinner cropMode = (Spinner) view.findViewById(R.id.cropMode);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.crop_item_spinner, mode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mode[position].equalsIgnoreCase("FIT IMAGE")) {
                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);

                } else if (mode[position].equalsIgnoreCase("SQUARE")) {
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);

                } else if (mode[position].equalsIgnoreCase("3:4")) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);

                } else if (mode[position].equalsIgnoreCase("4:3")) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);

                } else if (mode[position].equalsIgnoreCase("9:16")) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);

                } else if (mode[position].equalsIgnoreCase("16:9")) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);

                } else if (mode[position].equalsIgnoreCase("7:5")) {
                    mCropView.setCustomRatio(7, 5);
                } else if (mode[position].equalsIgnoreCase("FREE")) {
                    mCropView.setCropMode(CropImageView.CropMode.FREE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Setting the ArrayAdapter data on the Spinner
        cropMode.setAdapter(adapter);
        if (mCropView.getImageBitmap() == null) {
            Bitmap bitmap = resize(bitmaps, 640, 640);
            mCropView.setImageBitmap(bitmap);

            //showProgress();
            /*Compressor.Builder builder=new Compressor.Builder(getContext());
            builder.setMaxWidth(640);
            builder.setMaxHeight(640);
            builder.setQuality(60);
            File file = new File(CropActivity.uri.getPath());
            builder.build().compressToFileAsObservable(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File file) {

                            Bitmap bm= null;
                            try {
                                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mCropView.setImageBitmap(bm);
                            dismissProgress();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("Erroe","err"+throwable.getMessage());
                            dismissProgress();
                            Toast.makeText(getContext(),"something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    });*/
        }
        mCropView.setOutputHeight(640);    // fix resolution in pixels, as of now resolution is set in CropActivity that is why it is commented
        //     mCropView.setCompressQuality(90);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            showProgress();
            mCropView.startLoad(result.getData(), mLoadCallback);
        } else if (requestCode == REQUEST_SAF_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            showProgress();
            mCropView.startLoad(Utils.ensureUriPermission(getContext(), result), mLoadCallback);
        }
    }


    // Bind views //////////////////////////////////////////////////////////////////////////////////

    private void bindViews(View view) {
        mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonBackImage).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        mRootLayout = (LinearLayout) view.findViewById(R.id.layout_root);
    }

    public void cropImage() {
        showProgress();
        mCropView.startCrop(uri, mCropCallback, mSaveCallback);
    }


    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    public void dismissProgress() {
        if (!isAdded()) return;
        FragmentManager manager = getFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri() {
        return Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
    }
    // Handle button event /////////////////////////////////////////////////////////////////////////

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone:
                    //  cropImageWithCheck(MainFragment.this);
                    cropImage();
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.buttonBackImage:
                    try {
                        ((CropActivity) getActivity()).getOutputMediaFile().delete();
                        getActivity().finish();
                    } catch (Exception e) {
                        Log.e("exception", e.getMessage());
                    }
                    break;
            }
        }
    };

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            dismissProgress();
        }

        @Override
        public void onError() {
            dismissProgress();
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {


        }

        @Override
        public void onError() {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {

            Log.e("croppedImage",outputUri.getPath()+"       asda");
            ((CropActivity) getActivity()).startResultActivity(outputUri);
            dismissProgress();
        }

        @Override
        public void onError() {
            dismissProgress();
        }
    };

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}