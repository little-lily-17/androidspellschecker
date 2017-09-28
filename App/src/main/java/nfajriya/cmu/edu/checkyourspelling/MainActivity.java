package nfajriya.cmu.edu.checkyourspelling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //the apps will starting here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create instance
        super.onCreate(savedInstanceState);
        //set activity_main as the content view
        setContentView(R.layout.activity_main);
        final MainActivity ma = this;

        //find view by id = button, set it to submitButton
        Button submitButton = (Button)findViewById(R.id.button);
        //add listener to the button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                //get user input from the editText
                String userInput = ((EditText)findViewById(R.id.editText)).getText().toString();
                //call getData method from GetServerData class
                GetServerData gp = new GetServerData();
                gp.getData(userInput, ma); // Done asynchronously in another thread.  It calls resultReady() in this thread when complete.
            }
        });
    }
    //after result ready, show it to the user
    public void resultReady(String result){
        //find vieww by id = textView, set it to resultView
        TextView resultView = (TextView)findViewById(R.id.textView);
        if(result != null){
            //set result to TextView
            resultView.setText(result);
        }
    }
}
