 package com.example.a8_miracle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

 public class Login extends AppCompatActivity {

     private EditText Email, Password;
     private Button btnLogin;
     private TextView txtSignUp , fopassw;
     private static final String URL = "https://8miracle.serv00.net/account/login.php";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);


         Email = findViewById(R.id.Email);
         Password = findViewById(R.id.Password);
         btnLogin = findViewById(R.id.btnlogin);
         txtSignUp = findViewById(R.id.textView3);
         fopassw = findViewById(R.id.textView2);

         fopassw.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(Login.this, ForgotPasswordActivity.class));
             }
         });

         txtSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(Login.this, SignUp.class));
             }
         });


         btnLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String email = Email.getText().toString().trim();
                 String password = Password.getText().toString().trim();

                 if (!email.isEmpty() && !password.isEmpty()) {
                     loginUser(email, password);
                 } else {
                     Toast.makeText(Login.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }

     private void loginUser(String email, String password) {
         StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         try {
                             Log.d("LoginResponse", response);

                             JSONObject jsonObject = new JSONObject(response);
                             String status = jsonObject.getString("status");
                             String message = jsonObject.getString("message");

                             if (status.equals("success")) {
                                 String userID = jsonObject.getString("userID");


                                 SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                 SharedPreferences.Editor editor = sharedPreferences.edit();
                                 editor.putString("userID", userID);
                                 editor.putBoolean("isLoggedIn", true);
                                 editor.apply();

                                 Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                 startActivity(new Intent(Login.this, Navbar.class));
                                 finish();
                             } else {
                                 Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                             }
                         } catch (JSONException e) {
                             Log.e("JSONError", "Error parsing response: " + response, e);
                             Toast.makeText(Login.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                         }
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(Login.this, "Server connection error", Toast.LENGTH_SHORT).show();
                     }
                 }) {
             @Override
             protected Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("email", email);
                 params.put("password", password);
                 return params;
             }
         };

         RequestQueue requestQueue = Volley.newRequestQueue(this);
         requestQueue.add(stringRequest);
     }
 }
