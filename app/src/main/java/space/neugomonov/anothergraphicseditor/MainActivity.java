package space.neugomonov.anothergraphicseditor;

import android.app.Dialog;
import android.os.Bundle;
import java.io.IOException;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView mTextView;
    private ImageButton currPaint, drawBtn,newFile,erase,save;
    private DrawingView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        newFile = (ImageButton)findViewById(R.id.new_btn);
        erase = (ImageButton)findViewById(R.id.erase_btn);
        save = (ImageButton)findViewById(R.id.save_btn);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(10);
        newFile.setOnClickListener(this);
        erase.setOnClickListener(this);
        save.setOnClickListener(this);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        mTextView = (TextView)findViewById(R.id.textView);
        mTextView.setText(String.valueOf(seekBar.getProgress()));
    }
    public void paintClicked(View view){
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.draw_btn){
                drawView.setErase(false);
        }
        if(v.getId()==R.id.erase_btn){
            drawView.setErase(true);
            drawView.setBrushSize(drawView.getLastBrushSize());
        }
        if(v.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing unless you saved it)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        if(v.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
//                    try {
                         Toast.makeText(getApplicationContext(),
                                "Drawing saved to gallery.", Toast.LENGTH_LONG).show();
                    }
                    else {
//                    catch (IOException e){
//                        Log.e(MainActivity.class.getCanonicalName(), e.getMessage());
//                        Toast.makeText(MainActivity.this, "Error while saving: "
//                         + e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Error occurred.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();

                }
            });
            saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mTextView.setText(String.valueOf(seekBar.getProgress()));
        drawView.setBrushSize(progress);
        drawView.setLastBrushSize(progress);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
