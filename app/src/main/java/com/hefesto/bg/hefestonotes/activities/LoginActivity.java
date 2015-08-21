package com.hefesto.bg.hefestonotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hefesto.bg.hefestonotes.R;
import com.hefesto.bg.hefestonotes.auxclases.Seguridad;

public class LoginActivity extends AppCompatActivity {
    private Button buttonGuardar;
    private EditText editPassword;
    private EditText editPregunta;
    private EditText editRespuesta;
    private static final String TAG = ListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editPassword = (EditText) findViewById(R.id.editContraseña);
        editPregunta = (EditText) findViewById(R.id.editPregunta);
        editRespuesta = (EditText) findViewById(R.id.editRespuesta);
        buttonGuardar = (Button) findViewById(R.id.buttonSaveSeg);
        buttonGuardar.setOnClickListener(new MiButtonGuardarOnClickListener());
        mensajeExplicativo();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_help) {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle(R.string.help);
            dialogo.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_help));
            dialogo.setMessage(R.string.explicacion_login);
            dialogo.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    });
            AlertDialog confirma = dialogo.create();
            confirma.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class MiButtonGuardarOnClickListener implements
            View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick SaveButton");
            String password = editPassword.getText().toString();
            String pregunta = editPregunta.getText().toString();
            String respuesta = editRespuesta.getText().toString();

            if (!password.isEmpty() && !pregunta.isEmpty() && !respuesta.isEmpty()) {
                Intent miIntent = new Intent();
                Bundle extras = new Bundle();
                Seguridad parametros = new Seguridad(password, pregunta, respuesta);
                extras.putSerializable(Seguridad.SEGURIDAD, parametros);
                miIntent.putExtras(extras);
                setResult(RESULT_OK, miIntent);
                decirGuardado();
                finish();
            } else {
                decirRellenar();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            decirRellenar();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void decirRellenar() {
        Toast.makeText(this, R.string.rellenar_todo, Toast.LENGTH_LONG).show();
    }

    private void decirGuardado() {
        Toast.makeText(this, R.string.nota_guardada, Toast.LENGTH_LONG).show();
    }
    private void mensajeExplicativo(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setMessage(R.string.empty_key);
        dialogo.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                });
        AlertDialog confirma = dialogo.create();
        confirma.show();
    }
}
