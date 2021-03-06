package com.neastwest.imagesiphon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout imagesLayout;
    protected EditText urlTextBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning variables to views
        urlTextBox = (EditText) findViewById(R.id.urls_to_download);
        imagesLayout = (LinearLayout)findViewById(R.id.results_region);
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");

        //retrieving urls from text box, split by new line whitespace
        String[] images = urlTextBox.getText().toString().split("\\s+");

        //send the array of urls to execute
        executeImages(images);
    }

    protected void executeImages(String[] images) {
        Log.i("MESSAGE", "reached for loop");

        //enhanced for loop to execute AsyncTask on each image in array
        //creating a new Wrapper object to pass the URL and the MainActivity Context
        //Uses the apache UrlValidator library to determine if the entered URL is in
        //the correct format to be stored as a URL object.
        UrlValidator urlValidator = new UrlValidator();
        for (String image1: images) {
            if(urlValidator.isValid(String.valueOf(image1))) {
                Wrapper w = new Wrapper();
                w.setString(image1);
                new ImageDownloader().execute(w);
                Log.i("MESSAGE", "assigned to object");
            } else {
                TextView newTxtView = new TextView(MainActivity.this);
                newTxtView.setText("Invalid URL");
                newTxtView.setPadding(5, 5, 5, 5);
                imagesLayout.addView(newTxtView);

            }
        }
    }

    //AsyncTask to check a URL and return a View.
    //Returns an ImageView for a valid link and a TextView in case of errors.
    private class ImageDownloader extends AsyncTask <Wrapper, Void, View> {
        private String newImageUrl = "";
        Wrapper ww = new Wrapper();

        protected View doInBackground(Wrapper... imagesURL) {
            ww = imagesURL[0];
            Log.i("MESSAGE", newImageUrl);
            View w = null;

            //send new Wrapper object to viewCreator function
            try {
                w = ImageSiphon.viewCreator(ww);
                Log.i("MESSAGE", "retreive Image worked?");
                //Catch exception
            } catch (IOException e) {
                e.printStackTrace();
            }

            return w;
        }

        //Add the View created in the doInBackground process - to the imagesLayout LL
        protected void onPostExecute(View newView) {
            imagesLayout.addView(newView);
        }


    }

    //Wrapper class. This class holds the Context and URL variables. This was created in order
    //to be able to pass multiple variables through an AsyncTask. Normally I would not have
    //used an AsyncTask due to our need of passing multiple variables (context)
    //however for the purposes of demoing threads, this started out to demo AsyncTask, and
    //I just stuck with it. I would probably redo this with an ExecutorService.
    public class Wrapper {
        final Context context = MainActivity.this;
        String imageName = "";

        public void setString(String newName) {
            imageName = newName;
        }

        public String getString() {
            return imageName;
        }

        public Context getContext() {
            return context;
        }
    }

}

