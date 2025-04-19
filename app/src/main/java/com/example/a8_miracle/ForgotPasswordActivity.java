package com.example.a8_miracle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput, codeInput;
    private Button sendCodeButton, verifyCodeButton;
    private static final String SEND_CODE_URL = "https://8miracle.serv00.net/account/send_reset_code.php";
    private static final String VERIFY_CODE_URL = "https://8miracle.serv00.net/account/verify_code.php";
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        codeInput = findViewById(R.id.codeInput);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        verifyCodeButton = findViewById(R.id.verifyCodeButton);

        // Initially hide the code input and verify button
        codeInput.setVisibility(View.GONE);
        verifyCodeButton.setVisibility(View.GONE);

        // Set click listeners
        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailInput.getText().toString().trim();
                if (!userEmail.isEmpty()) {
                    sendResetCode(userEmail);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeInput.getText().toString().trim();
                if (!code.isEmpty()) {
                    verifyCode(userEmail, code);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendResetCode(final String email) {
        // Show loading indicator
        sendCodeButton.setEnabled(false);
        sendCodeButton.setText("Sending...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_CODE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                Toast.makeText(ForgotPasswordActivity.this, "Verification code sent to your email", Toast.LENGTH_LONG).show();

                                // Show code input and verify button
                                codeInput.setVisibility(View.VISIBLE);
                                verifyCodeButton.setVisibility(View.VISIBLE);

                                // Update send button
                                sendCodeButton.setText("Resend Code");
                                sendCodeButton.setEnabled(true);
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                                sendCodeButton.setEnabled(true);
                                sendCodeButton.setText("Send");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            sendCodeButton.setEnabled(true);
                            sendCodeButton.setText("Send");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPasswordActivity.this, "Connection error. Please try again", Toast.LENGTH_SHORT).show();
                        sendCodeButton.setEnabled(true);
                        sendCodeButton.setText("Send");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void verifyCode(final String email, final String code) {
        verifyCodeButton.setEnabled(false);
        verifyCodeButton.setText("Verifying...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VERIFY_CODE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                Toast.makeText(ForgotPasswordActivity.this, "Code verified successfully", Toast.LENGTH_SHORT).show();

                                // Navigate to password reset page
                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("code", code);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                                verifyCodeButton.setEnabled(true);
                                verifyCodeButton.setText("Verify");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            verifyCodeButton.setEnabled(true);
                            verifyCodeButton.setText("Verify");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPasswordActivity.this, "Connection error. Please try again", Toast.LENGTH_SHORT).show();
                        verifyCodeButton.setEnabled(true);
                        verifyCodeButton.setText("Verify");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("code", code);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}