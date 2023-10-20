package com.example.socket;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private TextView textView;
    private EditText editText;
    private Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.button);

        try {
            socket = IO.socket("http://socket.dam.inspedralbes.cat:3450/"); // Reemplaza "URL_DEL_SERVIDOR" con la URL de tu servidor de chat
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SocketConnection", "Conexión con el servidor exitosa");
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("SocketConnection", "Error de conexión: " + args[0].toString());
        });

        socket.connect();

        // Escuchar eventos del servidor
        socket.on("chat message", onMessageReceived);

        button.setOnClickListener(view -> {
            String message = editText.getText().toString();
            if (!message.isEmpty()) {
                // Enviar el mensaje al servidor
                socket.emit("chat message", message);
                editText.setText(""); // Limpiar el campo de texto
                Log.d("Mensaje enviado", message);
            }
        });
    }

    private Emitter.Listener onMessageReceived = args -> {
        String message = (String) args[0];
        // Mostrar el mensaje recibido en el TextView
        runOnUiThread(() -> {
            textView.setText(message);
        });
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desconectar el socket al salir de la aplicación
        socket.disconnect();
    }
}