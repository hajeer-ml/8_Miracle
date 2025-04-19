package com.example.a8_miracle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText newPasswordInput, confirmPasswordInput;
    private Button resetPasswordButton;
    private static final String RESET_PASSWORD_URL = "https://8miracle.serv00.net/account/reset_password.php";
    private String email, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get data from intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        code = intent.getStringExtra("code");

        // Initialize views
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.codeInput); // Note: In your layout, this is called codeInput but is used for confirming password
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter and confirm your new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword(email, code, newPassword);
            }
        });
    }

    private void resetPassword(final String email, final String code, final String newPassword) {
        resetPasswordButton.setEnabled(false);
        resetPasswordButton.setText("Resetting...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RESET_PASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_LONG).show();


                                Intent intent = new Intent(ResetPasswordActivity.this, Navbar.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setText("Save");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ResetPasswordActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            resetPasswordButton.setEnabled(true);
                            resetPasswordButton.setText("Save");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResetPasswordActivity.this, "Connection error. Please try again", Toast.LENGTH_SHORT).show();
                        resetPasswordButton.setEnabled(true);
                        resetPasswordButton.setText("Save");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("code", code);
                params.put("new_password", newPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}