package com.example.medialogy6colourapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera; // Controls the camera
import androidx.camera.core.CameraSelector; // Find the camera
import androidx.camera.core.Preview; // Streams the camera feed to the preview
import androidx.camera.lifecycle.ProcessCameraProvider; // Binds the camera to the lifecycle
import androidx.camera.view.PreviewView; // Camera preview window
import androidx.core.content.ContextCompat; // Access features in Context

import com.google.common.util.concurrent.ListenableFuture; // Chaining asynchronous operations

import java.util.concurrent.ExecutionException; // Task abortion exception


public class CameraActivity extends AppCompatActivity {
    public ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // Views
    PreviewView previewView;
    Camera camera;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Request a camera provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Get the preview window and continue button
        previewView = findViewById(R.id.previewView);
        button = findViewById(R.id.MoveOnCamera);

        // Check if the camera is available
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));


        // Continue button
        button.setOnClickListener(view -> {
            Intent i = new Intent(CameraActivity.this, InstructionActivity.class);
            startActivity(i);
        });
    }

    // bindPreview - Bind the lifecycle of the camera to the preview window
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        // Create a preview
        Preview preview = new Preview.Builder()
                .build();

        // Specify front-facing camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // Bind the preview to the previewView, and bind the camera to the lifecycle
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }
}