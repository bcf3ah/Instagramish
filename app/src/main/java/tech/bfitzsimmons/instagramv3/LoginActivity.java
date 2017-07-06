package tech.bfitzsimmons.instagramv3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    TextView authSwitch;
    TextView authButton;
    EditText usernameInput;
    EditText passwordInput;
    Intent intent;

    //boolean to see if we are in signupMode (as opposed to sign in mode)
    private boolean signupMode = false;

    //set up click listener for authSwitch (to change auth mode from sign in <-> sign up) or to get rid of keyboard if user clicks on whitespace
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.authSwitch) {
            if (authSwitch.getText().toString().equals("Or sign up")) {
                authSwitch.setText("Or sign in");
                authButton.animate().rotationBy(360f).setDuration(500);
                authButton.setText("Sign up");
                signupMode = true;
            } else {
                authSwitch.setText("Or sign up");
                authButton.animate().rotationBy(360f).setDuration(500);
                authButton.setText("Sign in");
                signupMode = false;
            }
        } else if (view.getId() == R.id.background || view.getId() == R.id.instagramIcon) {
            //else user clicked on blank space or the instagram icon, get rid of keyboard
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //set up key listener to let user click enter on keyboard to submit form
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        //if the user hits the enter key (the second criteria is to make sure it just submits once, not on keyup as well), then run authenticate method with random view
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            authenticate(view);
        }
        return false;
    }

    //convenience method to go to user list activity
    public void goToUserListActivity() {
        intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    //Sign in/Sign up auth methods for ParseServer
    public void authenticate(View view) {
        ParseUser user = new ParseUser();
        final String username = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();
        if (!signupMode) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        //if successful, show success Toast then send the user to the UserListActivity
                        Toast.makeText(LoginActivity.this, "Welcome back " + user.getUsername() + "!", Toast.LENGTH_LONG).show();
                        goToUserListActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (signupMode) {
            user.setUsername(username);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //if successful, show success Toast then send the user to the UserListActivity
                        Toast.makeText(LoginActivity.this, "Successfully signed up! Welcome " + username + "!", Toast.LENGTH_LONG).show();
                        goToUserListActivity();
                    } else {
                        String message;
                        if (e.getMessage().length() > 45) {
                            message = "Username and password cannot be blank";
                        } else {
                            message = e.getMessage();
                        }
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize authSwitch and authButton
        authSwitch = (TextView) findViewById(R.id.authSwitch);
        authButton = (TextView) findViewById(R.id.authButton);

        //initialize username and password inputs
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        //initialize the view click listener defined above
        authSwitch.setOnClickListener(this);

        //initialize key event listener for the enter key on the keyboard (so users can submit form that way)
        passwordInput.setOnKeyListener(this);

        //init background layout and logo so if user clicks on it, it will remove keyboard
        ConstraintLayout background = (ConstraintLayout) findViewById(R.id.background);
        ImageView icon = (ImageView) findViewById(R.id.instagramIcon);

        //give them both click listeners
        background.setOnClickListener(this);
        icon.setOnClickListener(this);

        //if user is already signed in upon app start, go straight to userList activity
        if (ParseUser.getCurrentUser() != null) {
            goToUserListActivity();
        }
    }
}
