package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Setup the image chooser dialog.
 */
public class ImageChooserDialogFragment extends DialogFragment {

    /**
     * This interface tells that the activity that creates an instance of this dialog fragment
     * must implement the methods below in order to receive event callbacks.
     */
    public interface ImageChooserDialogListener {
        void onDialogCameraClick(DialogFragment dialog);

        void onDialogGalleryClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events.
    ImageChooserDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the ImageChooserDialogListener.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the ImageChooserDialogListener so we can send events to the host.
            mListener = (ImageChooserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception.
            throw new ClassCastException(activity.toString()
                    + " must implement ImageChooserDialogListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater and Inflate the layout for the dialog.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout.
        View view = inflater.inflate(R.layout.dialog_image_chooser, null);

        View cameraView = view.findViewById(R.id.action_camera);
        View galleryView = view.findViewById(R.id.action_gallery);

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the camera click event back to the host activity.
                mListener.onDialogCameraClick(ImageChooserDialogFragment.this);
                // Dismiss the dialog fragment.
                dismiss();
            }
        });

        galleryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the gallery click event back to the host activity.
                mListener.onDialogGalleryClick(ImageChooserDialogFragment.this);
                // Dismiss the dialog fragment.
                dismiss();
            }
        });

        // Create an AlertDialog.Builder and set the layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }
}
